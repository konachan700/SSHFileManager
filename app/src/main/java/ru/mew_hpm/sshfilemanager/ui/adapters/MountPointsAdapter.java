package ru.mew_hpm.sshfilemanager.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.ArrayList;
import java.util.Collection;

import ru.mew_hpm.sshfilemanager.R;
import ru.mew_hpm.sshfilemanager.dao.MountPoint;

public class MountPointsAdapter extends BaseAdapter {
    private final MountPointsAdapterActionListener
            actionListener;

    private final LayoutInflater
            lInflater;

    private final ArrayList<MountPoint>
            fileArrayList = new ArrayList<>();

    public MountPointsAdapter(Context c, MountPointsAdapterActionListener al) {
        lInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        actionListener = al;
    }

    public void refresh(Collection data) {
        fileArrayList.clear();
        fileArrayList.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return fileArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return fileArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final MountPoint mp = fileArrayList.get(i);

        if (view == null)
            view = lInflater.inflate(R.layout.fragment_file_manager_item, viewGroup, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (actionListener != null) actionListener.OnClick(mp);
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (actionListener != null) actionListener.OnLongClick(mp);
                return false;
            }
        });

        ((TextView) view.findViewById(R.id.itemTitle)).setText(mp.getDrive());
        ((TextView) view.findViewById(R.id.itemSubtext)).setText(mp.getSize());

        if (mp.isSwap())
            ((TextView) view.findViewById(R.id.itemText)).setText("Swap partition");
        else if (mp.isEmpty()) {
            ((TextView) view.findViewById(R.id.itemText)).setText((mp.isRootDevice() ? "Disk" : "Not contain a valid FS"));
        } else {
            if (mp.isMounted())
                ((TextView) view.findViewById(R.id.itemText)).setText("FS type: " + mp.getFstype() + "; " + (mp.isMounted() ? mp.getFolder() : "none"));
            else
                ((TextView) view.findViewById(R.id.itemText)).setText("FS type: " + mp.getFstype() + "; not mounted");
        }

        if (!mp.isMounted()) {
            ((ImageView) view.findViewById(R.id.itemIcon))
                    .setImageDrawable(new IconicsDrawable(view.getContext())
                            .icon(GoogleMaterial.Icon.gmd_folder).colorRes(
                                    (!(mp.isSwap() || mp.isEmpty() || mp.isSystem() || mp.isRootDevice())) ? R.color.colorMounted : R.color.colorIconGray).sizeDp(48));
        } else
            ((ImageView) view.findViewById(R.id.itemIcon))
                    .setImageDrawable(new IconicsDrawable(view.getContext())
                            .icon(GoogleMaterial.Icon.gmd_folder).colorRes(
                                    (!(mp.isSwap() || mp.isEmpty() || mp.isSystem() || mp.isRootDevice())) ? R.color.colorDismounted : R.color.colorIconGray).sizeDp(48));

        return view;
    }
}
