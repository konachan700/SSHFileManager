package ru.mew_hpm.sshfilemanager.ui.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;

import com.jcraft.jsch.SftpException;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import ru.mew_hpm.sshfilemanager.R;
import ru.mew_hpm.sshfilemanager.dao.BackgroundCopyTask;
import ru.mew_hpm.sshfilemanager.dao.MountPoint;
import ru.mew_hpm.sshfilemanager.dao.RemoteFile;
import ru.mew_hpm.sshfilemanager.dao.SSHCommand;
import ru.mew_hpm.sshfilemanager.dao.SSHCommandEventListener;
import ru.mew_hpm.sshfilemanager.dao.SSHLs;
import ru.mew_hpm.sshfilemanager.dao.SSHLsEventListener;
import ru.mew_hpm.sshfilemanager.dao.SSHServerData;
import ru.mew_hpm.sshfilemanager.ssh.MountHelper;
import ru.mew_hpm.sshfilemanager.ssh.MountHelperEventListener;
import ru.mew_hpm.sshfilemanager.ssh.SSHHelper;
import ru.mew_hpm.sshfilemanager.ssh.SSHHelperEventListener;
import ru.mew_hpm.sshfilemanager.ui.adapters.FileManagerAdapter;
import ru.mew_hpm.sshfilemanager.ui.adapters.FileManagerAdapterActionListener;
import ru.mew_hpm.sshfilemanager.ui.adapters.MountPointsAdapter;
import ru.mew_hpm.sshfilemanager.ui.adapters.MountPointsAdapterActionListener;
import ru.mew_hpm.sshfilemanager.ui.dialogs.MountListDialog;
import ru.mew_hpm.sshfilemanager.ui.dialogs.MountListDialogItem;
import ru.mew_hpm.sshfilemanager.ui.dialogs.MountListDialogItemActionListener;

@EFragment(R.layout.fragment_file_manager_list)
public class FileManagerFragment extends Fragment implements FileManagerAdapterActionListener, SSHHelperEventListener, SSHLsEventListener, MountHelperEventListener {
    private enum OperationType {
        COPY, CUT, BG_COPY, NOP
    }

    private FileManagerFragment THIS = this;

    private SSHHelper
            ssh = null;

    private FileManagerAdapter
            remoteAdapter;

    private MountPointsAdapter
            mountPointsAdapter;

    private RemoteFile
            selectedFile = null;

    private String
            selectedFileDirectory = "";

    private OperationType
            opType = OperationType.NOP;

    private FileManagerFragmentEventListener
            actionListener = null;

    private SSHServerData
            currentConnectionInfo = null;

    private MountHelper
            mountHelper = new MountHelper();

    private int selectedTab = 0;
    private final String[] tabPath = new String[2];

    @ViewById
    ListView fmListRemote1;

    @ViewById
    ListView fmListRemote2;

    @ViewById
    ListView fmListMPAvaible;

    @ViewById
    TabHost fmTabs;

    @ViewById
    FloatingActionButton floatingMenuButton;

    @AfterViews
    void initThis() {
        fmTabs.setup();

        TabHost.TabSpec tabSpec;
        tabSpec = fmTabs.newTabSpec("tab1");
        tabSpec.setIndicator("DIR 1");
        tabSpec.setContent(R.id.fmTabRemote1);
        fmTabs.addTab(tabSpec);

        tabSpec = fmTabs.newTabSpec("tab2");
        tabSpec.setIndicator("DIR 2");
        tabSpec.setContent(R.id.fmTabRemote2);
        fmTabs.addTab(tabSpec);

        tabSpec = fmTabs.newTabSpec("tab3");
        tabSpec.setIndicator("Mount");
        tabSpec.setContent(R.id.fmTabMount);
        fmTabs.addTab(tabSpec);

        tabSpec = fmTabs.newTabSpec("tab4");
        tabSpec.setIndicator("Tasks");
        tabSpec.setContent(R.id.fmTabTasks);
        fmTabs.addTab(tabSpec);

        fmTabs.setCurrentTabByTag("tab1");
        fmTabs.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) {
                if (currentConnectionInfo != null) {
                    switch (s) {
                        case "tab1":
                            selectedTab = 0;
                            ssh.ls(new SSHLs(tabPath[selectedTab] == null ? "/" : tabPath[selectedTab], THIS));
                            floatingMenuButton.show();
                            break;
                        case "tab2":
                            selectedTab = 1;
                            ssh.ls(new SSHLs(tabPath[selectedTab] == null ? "/" : tabPath[selectedTab], THIS));
                            floatingMenuButton.show();
                            break;
                        case "tab3":
                            selectedTab = 2;
                            mountHelper.lsMount();
                            floatingMenuButton.hide();
                            break;
                    }
                }
            }
        });

        mountHelper.create(ssh, this);

        remoteAdapter = new FileManagerAdapter(this.getActivity(), this);
        fmListRemote1.setAdapter(remoteAdapter);
        fmListRemote2.setAdapter(remoteAdapter);

        mountPointsAdapter = new MountPointsAdapter(this.getActivity(), new MountPointsAdapterActionListener() {
            @Override
            public void OnLongClick(final MountPoint item) {
                final ArrayList<MountListDialogItem> arr = new ArrayList<MountListDialogItem>();
                if (item.isRootDevice()) {
                    /*arr.add(new MountListDialogItem("Auto partitoning", new MountListDialogItemActionListener() {
                        @Override
                        public void OnDialogItemClick(MountListDialogItem di) {

                        }
                    }));
                    arr.add(new MountListDialogItem("Manual partitoning...", new MountListDialogItemActionListener() {
                        @Override
                        public void OnDialogItemClick(MountListDialogItem di) {

                        }
                    }));*/
                } else {
                    if (item.isSystem()) return;
                    if (item.isSwap()) return;

                    if (item.isMounted()) {
                        arr.add(new MountListDialogItem("Open", new MountListDialogItemActionListener() {
                            @Override
                            public void OnDialogItemClick(MountListDialogItem di) {
                                ssh.ls(new SSHLs(item.getFolder(), THIS));
                            }
                        }));
                        arr.add(new MountListDialogItem("Dismount", new MountListDialogItemActionListener() {
                            @Override
                            public void OnDialogItemClick(MountListDialogItem di) {
                                actionListener.OnWaitStart();
                                mountHelper.dismount(item);
                            }
                        }));
                    } else {
                        arr.add(new MountListDialogItem("Fast mount", new MountListDialogItemActionListener() {
                            @Override
                            public void OnDialogItemClick(MountListDialogItem di) {
                                actionListener.OnWaitStart();
                                mountHelper.fastMount(item);
                            }
                        }));
                        /*arr.add(new MountListDialogItem("Manual mount", new MountListDialogItemActionListener() {
                            @Override
                            public void OnDialogItemClick(MountListDialogItem di) {

                            }
                        }));
                        arr.add(new MountListDialogItem("Format to FAT", new MountListDialogItemActionListener() {
                            @Override
                            public void OnDialogItemClick(MountListDialogItem di) {

                            }
                        }));
                        arr.add(new MountListDialogItem("Format to ext4", new MountListDialogItemActionListener() {
                            @Override
                            public void OnDialogItemClick(MountListDialogItem di) {

                            }
                        }));*/
                    }
                }

                if (arr.size() > 0) MountListDialog.show("Mount/dismount", THIS.getContext(), arr);
            }

            @Override
            public void OnClick(MountPoint item) {

            }
        });
        fmListMPAvaible.setAdapter(mountPointsAdapter);

        floatingMenuButton.setImageDrawable(new IconicsDrawable(this.getContext()).icon(GoogleMaterial.Icon.gmd_menu).colorRes(R.color.colorFloatIcon).sizeDp(48));
    }

    public void create(Context c, Activity a) {
        ssh = SSHHelper.add(c, a, "ssh_main_connection");
    }

    public void connect(SSHServerData data) {
        SSHHelper.addEventsListener("ssh_main_connection", "FileManagerFragment", this);
        ssh.setRootDir(data.getInitDir());
        ssh.setSshPass(data.getPassword());
        ssh.setSshUser(data.getUsername());
        ssh.setSshAddr(data.getHost());
        ssh.setSshPort(data.getPort());
        ssh.setConnectDisabled(false);
        currentConnectionInfo = data;
    }

    private void moveCopyRemote() {
        if (selectedFile == null) return;
        final String pwd = ssh.getCurrentDir();

        if (selectedFileDirectory.contentEquals(pwd)) {
            Toast.makeText(THIS.getContext(), "Cannot copy/move it, src dir is equal dest!", Toast.LENGTH_LONG).show();
            return;
        }

        if (pwd.contains(selectedFile.getFullName()) && selectedFile.isDir()) {
            Toast.makeText(THIS.getContext(), "Cannot copy/move directory!", Toast.LENGTH_LONG).show();
            return;
        }

        switch (opType) {
            case COPY:
                final SSHCommand cpCmd = new SSHCommand(
                        "cp -Rv \""+selectedFileDirectory+"/"+selectedFile.getShortName()+"\" \""+pwd+"/\"",
                        new SSHCommandEventListener() {
                            @Override
                            public void OnCmdExecResult(SSHCommand cmd) {
                                actionListener.OnWaitEnd();
                                if (cmd.getErrorOut().contains("Permission denied")) {
                                    Toast.makeText(THIS.getContext(), "Cannot copy! Permission denied.", Toast.LENGTH_LONG).show();
                                }
                                ssh.ls(new SSHLs("./", THIS));
                            }
                        });
                ssh.exec(cpCmd);
                actionListener.OnWaitStart();
                break;
            case CUT:
                final SSHCommand mvCmd = new SSHCommand(
                        "mv \""+selectedFileDirectory+"/"+selectedFile.getShortName()+"\" \""+pwd+"/\"",
                        new SSHCommandEventListener() {
                            @Override
                            public void OnCmdExecResult(SSHCommand cmd) {
                                actionListener.OnWaitEnd();
                                if (cmd.getErrorOut().contains("Permission denied")) {
                                    Toast.makeText(THIS.getContext(), "Cannot move! Permission denied.", Toast.LENGTH_LONG).show();
                                }
                                ssh.ls(new SSHLs("./", THIS));
                            }
                        });
                ssh.exec(mvCmd);
                actionListener.OnWaitStart();
                break;
            case BG_COPY:
                final BackgroundCopyTask bgTask = new BackgroundCopyTask(selectedFileDirectory+"/"+selectedFile.getShortName(), pwd, md5(selectedFile.getShortName() + selectedFile.getSize()));
                bgTask.setFileShortName(selectedFile.getShortName());
                ssh.backgroundCopy(bgTask);
                break;
        }
    }

    @Click(R.id.floatingMenuButton)
    void OnFloatButtonClick() {
        final AlertDialog.Builder builder2 = new AlertDialog.Builder(this.getContext());
        builder2.setTitle("Main menu")
                .setItems(new String[] {"Paste", "Disconnect"}, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                moveCopyRemote();
                                break;
                            case 1:
                                actionListener.OnWaitStart();
                                ssh.setConnectDisabled(true);
                                break;
                        }
                        dialog.dismiss();
                    }
                });
        builder2.create().show();
    }

    @Override
    public void OnLongClick(RemoteFile item) {
        final RemoteFile fi = item;
        if (fi.getShortName() == null) return;

        final AlertDialog.Builder builder2 = new AlertDialog.Builder(this.getContext());
        builder2.setTitle(fi.getShortName())
                .setItems(new String[] {"Copy", "Cut", "Background copy", "Rename", "Delete", "Download"}, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                selectedFile = fi;
                                opType = OperationType.COPY;
                                selectedFileDirectory = ssh.getCurrentDir();
                                break;
                            case 1:
                                selectedFile = fi;
                                opType = OperationType.CUT;
                                selectedFileDirectory = ssh.getCurrentDir();
                                break;
                            case 2:
                                selectedFile = fi;
                                opType = OperationType.BG_COPY;
                                selectedFileDirectory = ssh.getCurrentDir();
                                break;
                            case 3:

                                break;
                            case 4:

                                break;
                        }



                        dialog.dismiss();
                    }
                });
        builder2.create().show();
    }

    @Override
    public void OnClick(RemoteFile item) {
        if (item.getShortName() == null)
            ssh.ls(new SSHLs("../", this));
        else {
            if (item.isDir()) {
                ssh.ls(new SSHLs("./" + item.getShortName() + "/", this));
            } else {

            }
        }
    }

    @Override
    public void OnConnect(String name) {
        if (currentConnectionInfo != null) {
            ssh.ls(new SSHLs(currentConnectionInfo.getInitDir(), this));
        }

        if (getActionListener() != null)
            getActionListener().OnSSHConnect(name);
    }

    @Override
    public void OnError(String threadName, String elName, Exception e) {
        if (e != null) {
            if (e instanceof SftpException) {
                if (((SftpException) e).id == 3) { //3: Permission denied
                    ssh.ls(new SSHLs("../", this));
                    Toast.makeText(this.getContext(), "Permission denied", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

        ssh.setConnectDisabled(true);
        if (getActionListener() != null) getActionListener().OnSSHError(threadName, elName);
    }

    @Override
    public void OnDisconnect(String name) {
        actionListener.OnDisconnectClick();
    }

    @Override
    public void OnProgress(String text) {

    }

    public FileManagerFragmentEventListener getActionListener() {
        return actionListener;
    }

    public void setActionListener(FileManagerFragmentEventListener actionListener) {
        this.actionListener = actionListener;
    }

    @Override
    public void OnLsResult(SSHLs ls) {
        remoteAdapter.refresh(ls.getResult());
        tabPath[selectedTab] = ssh.getCurrentDir();
    }

    @Override
    public void OnLsMountComplete(ArrayList<MountPoint> out) {
        mountPointsAdapter.refresh(out);
    }

    @Override
    public void OnFastMountComplete(MountPoint mp) {
        actionListener.OnWaitEnd();
        mountHelper.lsMount();
    }

    @Override
    public void OnUmountComplete(MountPoint mp) {
        actionListener.OnWaitEnd();
        mountHelper.lsMount();
    }

    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            MessageDigest digest = java.security.MessageDigest.getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
