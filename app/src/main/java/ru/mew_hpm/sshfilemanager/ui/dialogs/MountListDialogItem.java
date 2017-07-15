package ru.mew_hpm.sshfilemanager.ui.dialogs;

public class MountListDialogItem {
    private String title;
    private MountListDialogItemActionListener actionListener;

    public MountListDialogItem(String dTitle, MountListDialogItemActionListener al) {
        actionListener = al;
        title = dTitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public MountListDialogItemActionListener getActionListener() {
        return actionListener;
    }

    public void setActionListener(MountListDialogItemActionListener actionListener) {
        this.actionListener = actionListener;
    }
}
