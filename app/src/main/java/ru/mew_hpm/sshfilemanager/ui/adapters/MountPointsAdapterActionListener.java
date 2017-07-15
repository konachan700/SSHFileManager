package ru.mew_hpm.sshfilemanager.ui.adapters;

import ru.mew_hpm.sshfilemanager.dao.MountPoint;

public interface MountPointsAdapterActionListener {
    public void OnLongClick(MountPoint item);
    public void OnClick(MountPoint item);
}
