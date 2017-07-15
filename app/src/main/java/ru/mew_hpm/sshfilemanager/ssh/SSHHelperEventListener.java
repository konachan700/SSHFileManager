package ru.mew_hpm.sshfilemanager.ssh;

public interface SSHHelperEventListener {
    void OnConnect(String name);
    void OnError(String threadName, String elName, Exception e);
    void OnDisconnect(String name);
    void OnProgress(String text);
}
