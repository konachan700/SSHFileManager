package ru.mew_hpm.sshfilemanager.dao;

public class SSHServerData {
    private long id;
    private String username;
    private String password;
    private String host;
    private String port;
    private String initDir;
    private String userText;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getInitDir() {
        return initDir;
    }

    public void setInitDir(String initDir) {
        this.initDir = initDir;
    }

    public String getUserText() {
        return userText;
    }

    public void setUserText(String userText) {
        this.userText = userText;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
