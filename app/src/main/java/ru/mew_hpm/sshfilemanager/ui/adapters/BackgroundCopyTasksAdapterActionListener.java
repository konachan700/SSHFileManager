package ru.mew_hpm.sshfilemanager.ui.adapters;

import ru.mew_hpm.sshfilemanager.dao.BackgroundCopyTask;

public interface BackgroundCopyTasksAdapterActionListener {
    public void OnLongClick(BackgroundCopyTask item);
    public void OnClick(BackgroundCopyTask item);
}
