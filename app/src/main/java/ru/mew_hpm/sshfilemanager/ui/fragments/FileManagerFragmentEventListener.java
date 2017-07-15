package ru.mew_hpm.sshfilemanager.ui.fragments;

public interface FileManagerFragmentEventListener {
    public void OnSSHConnect(String name);
    public void OnSSHError(String threadName, String elName);
    public void OnWaitStart();
    public void OnWaitEnd();
    public void OnDisconnectClick();
}
