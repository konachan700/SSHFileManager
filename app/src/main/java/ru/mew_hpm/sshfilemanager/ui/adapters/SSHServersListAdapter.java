package ru.mew_hpm.sshfilemanager.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.ArrayList;

import ru.mew_hpm.sshfilemanager.R;
import ru.mew_hpm.sshfilemanager.dao.SSHServerData;

public class SSHServersListAdapter extends BaseAdapter {
    private final SSHServersListAdapterActionListener
            actionListener;

    private final ArrayList<SSHServerData>
            sshServers;

    private final LayoutInflater
            lInflater;

    public SSHServersListAdapter(Context c, ArrayList<SSHServerData> data, SSHServersListAdapterActionListener al) {
        lInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        sshServers = data;
        actionListener = al;
    }

    @Override
    public int getCount() {
        return sshServers.size();
    }

    @Override
    public Object getItem(int i) {
        return sshServers.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final int id = i;
        if (view == null)
            view = lInflater.inflate(R.layout.fragment_ssh_server_item, viewGroup, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (actionListener != null) actionListener.OnClick(sshServers.get(id));
            }
        });

        ((TextView) view.findViewById(R.id.itemTitle)).setText(sshServers.get(i).getUserText());
        ((TextView) view.findViewById(R.id.itemText)).setText("ssh://"+sshServers.get(i).getUsername()+"@"+sshServers.get(i).getHost()+":"+sshServers.get(i).getPort()+"/");

        ((ImageView) view.findViewById(R.id.itemIcon))
                .setImageDrawable(new IconicsDrawable(view.getContext())
                        .icon(GoogleMaterial.Icon.gmd_cloud).colorRes(R.color.colorIconGray).sizeDp(48));

        ((ImageButton) view.findViewById(R.id.itemAction))
                .setImageDrawable(new IconicsDrawable(view.getContext())
                        .icon(GoogleMaterial.Icon.gmd_edit).colorRes(R.color.colorIconGray).sizeDp(24));
        ((ImageButton) view.findViewById(R.id.itemAction))
                .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (actionListener != null) actionListener.OnMoreButtonClick(sshServers.get(id));
            }
        });

        return view;
    }
}
