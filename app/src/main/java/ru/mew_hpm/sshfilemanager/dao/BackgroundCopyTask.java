package ru.mew_hpm.sshfilemanager.dao;

public class BackgroundCopyTask {
    private String copyFrom;
    private String copyTo;
    private String taskName;
    private String fileShortName;
    private String extra;

    public BackgroundCopyTask() {}

    public BackgroundCopyTask(String from, String to, String name) {
        copyFrom = from;
        copyTo = to;
        taskName = name;
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
}
