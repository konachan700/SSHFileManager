package ru.mew_hpm.sshfilemanager.ssh;

import java.util.ArrayList;

import ru.mew_hpm.sshfilemanager.dao.MountPoint;

public interface MountHelperEventListener {
    public void OnLsMountComplete(ArrayList<MountPoint> out);
    public void OnFastMountComplete(MountPoint mp);
    public void OnUmountComplete(MountPoint mp);
}
