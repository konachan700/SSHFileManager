package ru.mew_hpm.sshfilemanager.ui.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import java.util.ArrayList;
import java.util.Arrays;

public class MountListDialog {
    public static void show(String title, Context c, final ArrayList<MountListDialogItem> items) {
        final String[] data = new String[items.size()];
        for (int i=0; i<items.size(); i++)
            data[i] = items.get(i).getTitle();

        final AlertDialog.Builder builder2 = new AlertDialog.Builder(c);
        builder2.setTitle(title)
                .setItems(data, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        final MountListDialogItemActionListener al = items.get(which).getActionListener();
                        if (al != null) al.OnDialogItemClick(items.get(which));
                        dialog.dismiss();
                    }
                });
        builder2.create().show();
    }

    public static void show(String title, Context c, final MountListDialogItem ... items) {
        final ArrayList<MountListDialogItem> itemsAL = new ArrayList<MountListDialogItem>(Arrays.asList(items));
        show(title, c, itemsAL);
    }
}
