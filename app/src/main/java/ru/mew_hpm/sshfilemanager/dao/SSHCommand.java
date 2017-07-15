package ru.mew_hpm.sshfilemanager.dao;

public class SSHCommand {
    private String command;
    private String errorOut;
    private String cmdOut;
    private String extra;
    private SSHCommandEventListener resultListener;

    public SSHCommand() {}

    public SSHCommand(String cmd, SSHCommandEventListener al) {
        resultListener = al;
        command = cmd;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getErrorOut() {
        return errorOut;
    }

    public void setErrorOut(String errorOut) {
        this.errorOut = errorOut;
    }

    public String getCmdOut() {
        return cmdOut;
    }

    public void setCmdOut(String cmdOut) {
        this.cmdOut = cmdOut;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public SSHCommandEventListener getResultListener() {
        return resultListener;
    }

    public void setResultListener(SSHCommandEventListener resultListener) {
        this.resultListener = resultListener;
    }
}
