package ru.mew_hpm.sshfilemanager.ui.fragments;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.widget.ListView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

import ru.mew_hpm.sshfilemanager.R;
import ru.mew_hpm.sshfilemanager.dao.SSHServerData;
import ru.mew_hpm.sshfilemanager.tools.DBHelper;
import ru.mew_hpm.sshfilemanager.ui.activities.SSHServerDataActivity_;
import ru.mew_hpm.sshfilemanager.ui.adapters.SSHServersListAdapter;
import ru.mew_hpm.sshfilemanager.ui.adapters.SSHServersListAdapterActionListener;

@EFragment(R.layout.fragment_ssh_servers_list)
public class SSHServersFragment extends Fragment implements SSHServersListAdapterActionListener {
    private final ArrayList<SSHServerData>
            sshServers = new ArrayList<>();

    private SSHServersListAdapter
            adapter;

    private SSHServersFragmentEventListener
            actionListener = null;

    @ViewById
    ListView
            sshServersList;

    @ViewById
    FloatingActionButton
            floatingActionButton;

    public SSHServersFragment() { }

    private void refresh() {
        sshServers.clear();
        final DBHelper dbHelper = new DBHelper(this.getContext());
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final Cursor cursor = db.query("sshServers", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                final SSHServerData sshd = new SSHServerData();
                sshd.setId(cursor.getLong(cursor.getColumnIndex("id")));
                sshd.setHost(cursor.getString(cursor.getColumnIndex("host")));
                sshd.setUsername(cursor.getString(cursor.getColumnIndex("username")));
                sshd.setPassword(cursor.getString(cursor.getColumnIndex("password")));
                sshd.setPort(cursor.getString(cursor.getColumnIndex("port")));
                sshd.setInitDir(cursor.getString(cursor.getColumnIndex("initDir")));
                sshd.setUserText(cursor.getString(cursor.getColumnIndex("userText")));
                sshServers.add(sshd);
            } while (cursor.moveToNext());
        }
        dbHelper.close();
        adapter.notifyDataSetChanged();
    }

    @AfterViews
    void initThis() {
        adapter = new SSHServersListAdapter(this.getContext(), sshServers, this);
        sshServersList.setAdapter(adapter);
        floatingActionButton.setImageDrawable(new IconicsDrawable(this.getContext()).icon(GoogleMaterial.Icon.gmd_add).colorRes(R.color.colorFloatIcon).sizeDp(48));
        refresh();
    }

    @Click(R.id.floatingActionButton)
    void OnFloatButtonClick() {
        final Intent intent = new Intent(this.getContext(), SSHServerDataActivity_.class);
        intent.putExtra("id", Integer.MAX_VALUE);
        startActivityForResult(intent, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        final DBHelper dbHelper = new DBHelper(this.getContext());
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final long id;

        if (requestCode == 0) {
            switch (resultCode) {
                case 0:

                    break;
                case 1:
                    final ContentValues cv = new ContentValues();
                    cv.put("host", data.getStringExtra("host"));
                    cv.put("username", data.getStringExtra("username"));
                    cv.put("password", data.getStringExtra("password"));
                    cv.put("port", data.getStringExtra("port"));
                    cv.put("initDir", data.getStringExtra("initDir"));
                    cv.put("userText", data.getStringExtra("userText"));

                    id = data.getLongExtra("id", Integer.MAX_VALUE);
                    if (id == Integer.MAX_VALUE) {
                        db.insert("sshServers", null, cv);
                    } else {
                        db.update("sshServers", cv, "id=?", new String[] { (id+"") });
                    }

                    break;
                case 2:
                    id = data.getLongExtra("id", Integer.MAX_VALUE);
                    if (id != Integer.MAX_VALUE) {
                        db.delete("sshServers", "id=?", new String[] { (id+"") });
                    }
                    break;
            }

            dbHelper.close();
            refresh();
        }
    }

    @Override
    public void OnMoreButtonClick(SSHServerData item) {
        final Intent intent = new Intent(this.getContext(), SSHServerDataActivity_.class);
        intent.putExtra("id", item.getId());
        intent.putExtra("host", item.getHost());
        intent.putExtra("username", item.getUsername());
        intent.putExtra("password", item.getPassword());
        intent.putExtra("port", item.getPort());
        intent.putExtra("initDir", item.getInitDir());
        intent.putExtra("userText", item.getUserText());
        startActivityForResult(intent, 0);
    }

    @Override
    public void OnClick(SSHServerData item) {
        if (actionListener != null) actionListener.OnItemClick(item);
    }

    public SSHServersFragmentEventListener getActionListener() {
        return actionListener;
    }

    public void setActionListener(SSHServersFragmentEventListener actionListener) {
        this.actionListener = actionListener;
    }
}
