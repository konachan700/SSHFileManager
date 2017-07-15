package ru.mew_hpm.sshfilemanager.ui.adapters;

import ru.mew_hpm.sshfilemanager.dao.RemoteFile;

public interface FileManagerAdapterActionListener {
    public void OnLongClick(RemoteFile item);
    public void OnClick(RemoteFile item);
}
