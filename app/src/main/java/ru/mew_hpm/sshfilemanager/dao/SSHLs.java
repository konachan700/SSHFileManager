package ru.mew_hpm.sshfilemanager.dao;

import java.util.concurrent.CopyOnWriteArrayList;

public class SSHLs {
    private String dir;
    private String extra;
    private SSHLsEventListener eventListener;
    private CopyOnWriteArrayList<RemoteFile> result;

    public SSHLs() {}

    public SSHLs(String directory, SSHLsEventListener el) {
        eventListener = el;
        dir = directory;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public SSHLsEventListener getEventListener() {
        return eventListener;
    }

    public void setEventListener(SSHLsEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public CopyOnWriteArrayList<RemoteFile> getResult() {
        return result;
    }

    public void setResult(CopyOnWriteArrayList<RemoteFile> result) {
        this.result = result;
    }
}
