package ru.mew_hpm.sshfilemanager.ssh;

import java.util.ArrayList;

import ru.mew_hpm.sshfilemanager.dao.MountPoint;
import ru.mew_hpm.sshfilemanager.dao.SSHCommand;
import ru.mew_hpm.sshfilemanager.dao.SSHCommandEventListener;

public class MountHelper {
    private final ArrayList<MountPoint>
            logicalDevices = new ArrayList<>();

    private SSHHelper ssh;
    private MountHelperEventListener eventListener;

    public void create(SSHHelper sshHelper, MountHelperEventListener el) {
        ssh = sshHelper;
        eventListener = el;
    }

    public void dismount(final MountPoint mp) {
        final SSHCommand cmd = new SSHCommand("umount /dev/"+mp.getDrive(),
                new SSHCommandEventListener() {
                    @Override
                    public void OnCmdExecResult(SSHCommand cmd) {
                        eventListener.OnUmountComplete(mp);
                    }
                });
        ssh.exec(cmd);
    }

    public void fastMount(final MountPoint mp) {
        if (!ssh.mkdir("/mnt/"+mp.getDrive())) return;
        final SSHCommand cmd = new SSHCommand("mount /dev/"+mp.getDrive()+" /mnt/"+mp.getDrive(),
                new SSHCommandEventListener() {
            @Override
            public void OnCmdExecResult(SSHCommand cmd) {
                eventListener.OnFastMountComplete(mp);
            }
        });
        ssh.exec(cmd);
    }

    public void lsMount() {
        logicalDevices.clear();
        final SSHCommand lsblkCmd = new SSHCommand("lsblk -o NAME,FSTYPE,SIZE,MOUNTPOINT -r", new SSHCommandEventListener() {
            @Override
            public void OnCmdExecResult(SSHCommand cmd) {
                final String lines[] = cmd.getCmdOut().split("\n");
                if (lines.length < 2) return;

                for (int i=1; i<lines.length; i++) {
                    final MountPoint mp = new MountPoint(lines[i]);
                    if (mp.isDisplayed()) logicalDevices.add(mp);
                }
                eventListener.OnLsMountComplete(logicalDevices);
            }
        });
        ssh.exec(lsblkCmd);
    }
}
