package ru.mew_hpm.sshfilemanager.dao;

import java.util.ArrayList;

public interface TaskCommandEventListener {
    public void OnTaskResult(ArrayList<BackgroundCopyTask> list, TaskCommand tc);
}
