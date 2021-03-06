package ru.mew_hpm.sshfilemanager.ssh;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import ru.mew_hpm.sshfilemanager.R;
import ru.mew_hpm.sshfilemanager.dao.BackgroundCopyTask;
import ru.mew_hpm.sshfilemanager.dao.RemoteFile;
import ru.mew_hpm.sshfilemanager.dao.SSHCommand;
import ru.mew_hpm.sshfilemanager.dao.SSHCommandEventListener;
import ru.mew_hpm.sshfilemanager.dao.SSHLs;
import ru.mew_hpm.sshfilemanager.dao.TaskCommand;
import ru.mew_hpm.sshfilemanager.tools.AppUtils;

public class SSHHelper extends AsyncTask {
    private static final Map<String, SSHHelper>
            connectionsPool = new HashMap<>();

    private Map<String, SSHHelperEventListener>
            eventsListeners = new HashMap<>();

    private final Context
            context;

    private volatile boolean
            runThread = true;

    private volatile boolean
            connectDisabled = true;

    private JSch
            jsch = null;

    private Session
            currSSHSess = null;

    private ChannelSftp
            channelSftp;

    //private final CopyOnWriteArrayList<String>
     //       storageDevices = new CopyOnWriteArrayList<>();

    private final ConcurrentLinkedQueue<SSHLs>
            lsRequests = new ConcurrentLinkedQueue<>();

    private final ConcurrentLinkedQueue<SSHCommand>
            cmdExecRequests = new ConcurrentLinkedQueue<>();

    private final ConcurrentLinkedQueue<TaskCommand>
            cmdTasksRequests = new ConcurrentLinkedQueue<>();

    private long timerForProgress = 0;

    private volatile String sshAddr = null;
    private volatile String sshUser = null;
    private volatile String sshPass = null;
    private volatile String rootDir = "/";
    private volatile String mntDir  = "/mnt/";

    private final String
            tName;

    private final Activity
            fragmentActivity;

    private volatile int
            sshPort = 22;

    private SSHHelper(Context c, Activity f, String threadName) {
        context = c;
        tName = threadName;
        fragmentActivity = f;
    }

    public static SSHHelper add(Context c, Activity f, String name) {
        if (connectionsPool.containsKey(name)) return connectionsPool.get(name);

        final SSHHelper ssh = new SSHHelper(c, f, name);
        connectionsPool.put(name, ssh);
        ssh.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        return ssh;
    }

    public static void addEventsListener(String threadName, String listenerName, SSHHelperEventListener el) {
        if (!connectionsPool.containsKey(threadName)) return;

        final SSHHelper ssh = connectionsPool.get(threadName);
        if (ssh.getEventsListeners().containsKey(listenerName)) return;

        ssh.getEventsListeners().put(listenerName, el);
    }

    public static void stopAll() {
        for (SSHHelper ssh : connectionsPool.values()) {
            ssh.setRunThread(false);
        }
    }

    public static SSHHelper get(String name) {
        return connectionsPool.get(name);
    }

    public boolean isRunThread() {
        return runThread;
    }

    public void setRunThread(boolean runThread) {
        this.runThread = runThread;
    }

    void errorState(final Exception e) {
        fragmentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (String name : getEventsListeners().keySet()) {
                    getEventsListeners().get(name).OnError(tName, name, e);
                }
            }
        });
    }

    void errorState() {
        errorState(null);
    }

    void connected() {
        fragmentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (String name : getEventsListeners().keySet()) {
                    getEventsListeners().get(name).OnConnect(tName);
                }
            }
        });
    }

    void progress(final String data) {
        fragmentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (String name : getEventsListeners().keySet()) {
                    getEventsListeners().get(name).OnProgress(data);
                }
            }
        });
    }

    public void ls(SSHLs path) {
        if (isConnectDisabled()) return;
        if (path.getDir().trim().length() < 1) return;
        lsRequests.add(path);
    }

    private SSHCommand execCmd(SSHCommand cmd) throws JSchException, IOException {
        Log.d("-------", "COMMAND: "+cmd.getCommand());

        ChannelExec channel = (ChannelExec) currSSHSess.openChannel("exec");
        channel.setCommand(cmd.getCommand());
        channel.setInputStream(null);

        final InputStream
                in = channel.getInputStream(),
                err = channel.getErrStream();
        channel.connect();

        final BufferedReader
                readerIn = new BufferedReader(new InputStreamReader(in)),
                readerEr = new BufferedReader(new InputStreamReader(err));

        final StringBuilder
                errStr = new StringBuilder(),
                inStr  = new StringBuilder();

        String line;

        while ((line = readerIn.readLine()) != null) {
            inStr.append(line).append('\n');
            progress(line + "\n");
        }

        while ((line = readerEr.readLine()) != null) {
            errStr.append(line).append('\n');
            progress(line + "\n");
        }

        channel.disconnect();

        cmd.setCmdOut(inStr.toString());
        cmd.setErrorOut(errStr.toString());

        return cmd;
    }

    public void exec(SSHCommand cmd) {
        if (isConnectDisabled()) return;
        if (cmd.getCommand().trim().length() < 1) return;
        cmdExecRequests.add(cmd);
    }

    public boolean mkdir(String path) {
        try {
            channelSftp.mkdir(path);
            return true;
        } catch (SftpException e) {
            e.printStackTrace();
            return (e.id != 3);
        }
    }

    public void backgroundCopy(BackgroundCopyTask task) {
        final String script = AppUtils.InputStreamToString(context.getResources().openRawResource(R.raw.bg_copy_script))
                .replace("##COPYFROM##", task.getCopyFrom())
                .replace("##COPYTO##", task.getCopyTo())
                .replace("##TASKNAME##", task.getTaskName());
        final ByteArrayInputStream bais = new ByteArrayInputStream(script.getBytes());

        final Gson gson = new Gson();
        final ByteArrayInputStream baisGson = new ByteArrayInputStream(gson.toJson(task).getBytes());

        try {
            channelSftp.mkdir("/tmp/sshfm");
        } catch (SftpException e) {
            if (e.id == 3) return;
        }

        try {
            channelSftp.put(baisGson, "/tmp/sshfm/sshfm_"+task.getTaskName()+".json");
            channelSftp.put(bais, "/tmp/sshfm/sshfm_"+task.getTaskName()+".sh");
            channelSftp.chmod(0777, "/tmp/sshfm/sshfm_"+task.getTaskName()+".sh");

            final SSHCommand cpCmd = new SSHCommand(
                    "nohup /tmp/sshfm/sshfm_"+task.getTaskName()+".sh  >> /tmp/sshfm/sshfm_log 2>> /tmp/sshfm/sshfm_log < /dev/null &",
                    new SSHCommandEventListener() {
                        @Override
                        public void OnCmdExecResult(SSHCommand cmd) {
                            Log.d("-----", cmd.getCmdOut());
                            Log.d("-----", cmd.getErrorOut());
                        }
                    });
            exec(cpCmd);
        } catch (SftpException e) {
            e.printStackTrace();
        }
    }

    public void getBgCopyTasks(TaskCommand tc) {
        if (isConnectDisabled()) return;
        if (tc == null) return;
        cmdTasksRequests.add(tc);
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        jsch = new JSch();

        while (runThread) {
            if (isConnectDisabled()) {
                if (currSSHSess != null) {
                    channelSftp.disconnect();
                    currSSHSess.disconnect();
                    currSSHSess = null;
                    fragmentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (String name : getEventsListeners().keySet()) {
                                getEventsListeners().get(name).OnDisconnect(tName);
                            }
                        }
                    });
                }
                continue;
            }
            if ((sshAddr == null) || (sshUser == null) || (sshPass == null)) continue;

            if (currSSHSess == null) {
                try {
                    progress("Connecting...");

                    currSSHSess = jsch.getSession(sshUser, sshAddr, sshPort);
                    currSSHSess.setPassword(sshPass);

                    Properties prop = new Properties();
                    prop.put("StrictHostKeyChecking", "no");
                    currSSHSess.setConfig(prop);
                    currSSHSess.connect();

                    channelSftp = (ChannelSftp) currSSHSess.openChannel("sftp");
                    channelSftp.connect();
                    channelSftp.cd(getRootDir());

                    //refreshDev();
                    connected();
                } catch (JSchException | SftpException e) {
                    e.printStackTrace();
                    currSSHSess = null;
                    errorState();
                }
            } else {
                if (currSSHSess.isConnected()) {
                    if (!lsRequests.isEmpty()) {
                        final SSHLs fDirName = lsRequests.poll();
                        try {
                            channelSftp.cd(fDirName.getDir());
                            final Vector<ChannelSftp.LsEntry> lsResult = channelSftp.ls(".");
                            final ArrayList<RemoteFile>
                                    listDir   = new ArrayList<>(),
                                    listFiles = new ArrayList<>();

                            final CopyOnWriteArrayList<RemoteFile>
                                    destList  = new CopyOnWriteArrayList<>();

                            for (ChannelSftp.LsEntry el : lsResult) {
                                if (el.getFilename().contentEquals(".") || el.getFilename().contentEquals("..")) continue;
                                if (el.getAttrs().isDir()) {
                                    final RemoteFile file = new RemoteFile();
                                    file.setSize(0);
                                    file.setDir(true);
                                    file.setFullName(el.getLongname());
                                    file.setShortName(el.getFilename());
                                    file.setRights(el.getAttrs().getPermissions());
                                    listDir.add(file);
                                }
                            }

                            if (!listDir.isEmpty()) {
                                Collections.sort(listDir, new Comparator<RemoteFile>() {
                                    @Override
                                    public int compare(RemoteFile s, RemoteFile t1) {
                                        return s.getShortName().compareTo(t1.getShortName());
                                    }
                                });
                            }

                            for (ChannelSftp.LsEntry el : lsResult) {
                                if (el.getAttrs().isReg() || el.getAttrs().isBlk()) {
                                    final RemoteFile file = new RemoteFile();
                                    file.setSize(el.getAttrs().getSize());
                                    file.setDir(false);
                                    file.setFullName(el.getLongname());
                                    file.setShortName(el.getFilename());
                                    file.setRights(el.getAttrs().getPermissions());
                                    listFiles.add(file);
                                }
                            }

                            if (!listFiles.isEmpty()) {
                                Collections.sort(listFiles, new Comparator<RemoteFile>() {
                                    @Override
                                    public int compare(RemoteFile s, RemoteFile t1) {
                                        return s.getShortName().compareTo(t1.getShortName());
                                    }
                                });
                            }

                            if (!channelSftp.pwd().contentEquals("/")) {
                                final RemoteFile rfBack = new RemoteFile();
                                rfBack.setShortName(null);
                                rfBack.setFullName(null);
                                destList.add(rfBack);
                            }

                            destList.addAll(listDir);
                            destList.addAll(listFiles);

                            fDirName.setResult(destList);
                            fragmentActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    fDirName.getEventListener().OnLsResult(fDirName);
                                }
                            });
                            //lsResult(fDirName);
                        } catch (SftpException e) {
                            e.printStackTrace();
                            errorState(e);
                        }
                    }

                    if (!cmdExecRequests.isEmpty()) {
                        final SSHCommand cmdExecText = cmdExecRequests.poll();
                        try {
                            final SSHCommand cmdExecRetVal = execCmd(cmdExecText);
                            fragmentActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    cmdExecRetVal.getResultListener().OnCmdExecResult(cmdExecRetVal);
                                }
                            });
                            //cmdExecResult(cmdExecRetVal);
                        } catch (JSchException | IOException e) {
                            e.printStackTrace();
                            errorState(e);
                        }
                    }

                    if (!cmdTasksRequests.isEmpty()) {
                        final TaskCommand tc = cmdTasksRequests.poll();

                        try {
                            channelSftp.mkdir("/tmp/sshfm");
                        } catch (SftpException e) { }

                        try {
                            final Vector<ChannelSftp.LsEntry> lsResult = channelSftp.ls("/tmp/sshfm");
                            final ArrayList<BackgroundCopyTask> tasks = new ArrayList<>();
                            final Gson gson = new Gson();

                            for (ChannelSftp.LsEntry item : lsResult) {
                                try {
                                    if (item.getAttrs().isReg() && item.getFilename().contains("sshfm_") && item.getFilename().contains(".json") && (item.getAttrs().getSize() > 0)) {
                                        final String fileName = "/tmp/sshfm/" + item.getFilename();
                                        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                        channelSftp.get(fileName, baos);

                                        try {
                                            final BackgroundCopyTask bgTask = gson.fromJson(baos.toString(), BackgroundCopyTask.class);

                                            if (bgTask.getTaskName() != null) {
                                                final ByteArrayOutputStream baos_log = new ByteArrayOutputStream();
                                                channelSftp.get("/tmp/sshfm/sshfm_log_" + bgTask.getTaskName(), baos_log);
                                                bgTask.setCompleted(baos_log.toString().contains("SSH_FM_TASK_OK"));
                                            }

                                            tasks.add(bgTask);
                                        } catch (JsonSyntaxException e) {}
                                    }
                                } catch (SftpException | JsonSyntaxException e) {
                                    e.printStackTrace();
                                }
                            }
                            fragmentActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tc.getEventListener().OnTaskResult(tasks, tc);
                                }
                            });
                        } catch (SftpException | JsonSyntaxException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    currSSHSess = null;
                    errorState();
                }
            }
        }

        if (currSSHSess != null) currSSHSess.disconnect();
        if (jsch != null) jsch = null;

        return null;
    }

    public String getCurrentDir() {
        try {
            return channelSftp.pwd();
        } catch (SftpException e) {
            e.printStackTrace();
            return "/";
        }
    }

    public String getSshAddr() {
        return sshAddr;
    }

    public void setSshAddr(String sshAddr) {
        this.sshAddr = sshAddr;
    }

    public String getSshUser() {
        return sshUser;
    }

    public void setSshUser(String sshUser) {
        this.sshUser = sshUser;
    }

    public String getSshPass() {
        return sshPass;
    }

    public void setSshPass(String sshPass) {
        this.sshPass = sshPass;
    }

    public boolean isConnectDisabled() {
        return connectDisabled;
    }

    public void setConnectDisabled(boolean connectDisabled) {
        this.connectDisabled = connectDisabled;
    }

    public String getRootDir() {
        return rootDir;
    }

    public void setRootDir(String rootDir) {
        this.rootDir = rootDir;
    }

    public String getMntDir() {
        return mntDir;
    }

    public void setMntDir(String mntDir) {
        this.mntDir = mntDir;
    }

    public Map<String, SSHHelperEventListener> getEventsListeners() {
        return eventsListeners;
    }

    public void setEventsListeners(Map<String, SSHHelperEventListener> eventsListeners) {
        this.eventsListeners = eventsListeners;
    }

    public void setSshPort(String port) {
        try {
            sshPort = Integer.parseInt(port);
        } catch (NumberFormatException e) {
            sshPort = 22;
            return;
        }
    }
}
