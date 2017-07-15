package ru.mew_hpm.sshfilemanager.dao;

public interface SSHCommandEventListener {
    void OnCmdExecResult(SSHCommand cmd);
}
