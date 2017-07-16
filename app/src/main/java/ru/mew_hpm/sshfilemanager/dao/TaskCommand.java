package ru.mew_hpm.sshfilemanager.dao;

public class TaskCommand {
    private TaskCommandEventListener eventListener;
    private String extra;

    public TaskCommand(TaskCommandEventListener al) {
        eventListener = al;
    }

    public TaskCommandEventListener getEventListener() {
        return eventListener;
    }

    public void setEventListener(TaskCommandEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }
}
