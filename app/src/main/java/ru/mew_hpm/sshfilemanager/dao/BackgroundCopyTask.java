package ru.mew_hpm.sshfilemanager.dao;

import java.io.Serializable;

public class BackgroundCopyTask implements Serializable {
    private String copyFrom;
    private String copyTo;
    private String taskName;
    private String fileShortName;
    private String extra;
    private boolean completed;
    private long startTime;

    public BackgroundCopyTask() {}

    public BackgroundCopyTask(String from, String to, String name) {
        copyFrom = from;
        copyTo = to;
        taskName = name;
        completed = false;
        startTime = System.currentTimeMillis();
    }

    public String getCopyFrom() {
        return copyFrom;
    }

    public void setCopyFrom(String copyFrom) {
        this.copyFrom = copyFrom;
    }

    public String getCopyTo() {
        return copyTo;
    }

    public void setCopyTo(String copyTo) {
        this.copyTo = copyTo;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getFileShortName() {
        return fileShortName;
    }

    public void setFileShortName(String fileShortName) {
        this.fileShortName = fileShortName;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
}
