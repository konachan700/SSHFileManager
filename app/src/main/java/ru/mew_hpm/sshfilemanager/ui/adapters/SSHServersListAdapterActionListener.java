package ru.mew_hpm.sshfilemanager.ui.adapters;

import ru.mew_hpm.sshfilemanager.dao.SSHServerData;

public interface SSHServersListAdapterActionListener {
    public void OnMoreButtonClick(SSHServerData item);
    public void OnClick(SSHServerData item);
}
