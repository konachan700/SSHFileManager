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
import ru.mew_hpm.sshfilemanager.dao.RemoteFile;

public class FileManagerAdapter extends BaseAdapter {
    private final FileManagerAdapterActionListener
            actionListener;

    private final LayoutInflater
            lInflater;

    private final ArrayList<RemoteFile>
            fileArrayList = new ArrayList<>();

    public FileManagerAdapter(Context c, FileManagerAdapterActionListener al) {
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
        final RemoteFile rf = fileArrayList.get(i);

        if (view == null)
            view = lInflater.inflate(R.layout.fragment_file_manager_item, viewGroup, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (actionListener != null) actionListener.OnClick(rf);
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (actionListener != null) actionListener.OnLongClick(rf);
                return false;
            }
        });

        if (rf.getShortName() == null) {
            ((TextView) view.findViewById(R.id.itemTitle)).setText("..");
            ((TextView) view.findViewById(R.id.itemSubtext)).setText("");
            ((TextView) view.findViewById(R.id.itemText)).setText("Go to parent directory");
            ((ImageView) view.findViewById(R.id.itemIcon))
                    .setImageDrawable(new IconicsDrawable(view.getContext())
                            .icon(GoogleMaterial.Icon.gmd_arrow_back).colorRes(R.color.colorIconGray).sizeDp(48));
        } else {
            final String name = (rf.getShortName().length() > 30) ?
                    rf.getShortName().substring(0, 17) + " ... " + rf.getShortName().substring(rf.getShortName().length() - 7) : rf.getShortName();
            ((TextView) view.findViewById(R.id.itemTitle)).setText(name);

            ((TextView) view.findViewById(R.id.itemSubtext)).setText(Long.toOctalString(rf.getRights()));

            if (rf.isDir()) {
                ((TextView) view.findViewById(R.id.itemText)).setText("Folder");
                ((ImageView) view.findViewById(R.id.itemIcon))
                        .setImageDrawable(new IconicsDrawable(view.getContext())
                                .icon(GoogleMaterial.Icon.gmd_folder).colorRes(R.color.colorIconGray).sizeDp(48));
            } else {
                String fileSize;
                if (rf.getSize() > (1024*1024*1024)) fileSize = (rf.getSize()/(1024*1024*1024)) + "G";
                else if (rf.getSize() > (1024*1024)) fileSize = (rf.getSize()/(1024*1024)) + "M";
                else if (rf.getSize() > 1024) fileSize = (rf.getSize()/1024) + "K";
                else fileSize = rf.getSize() + "B";

                ((TextView) view.findViewById(R.id.itemText)).setText("File size: " + fileSize);
                ((ImageView) view.findViewById(R.id.itemIcon))
                        .setImageDrawable(new IconicsDrawable(view.getContext())
                                .icon(GoogleMaterial.Icon.gmd_description).colorRes(R.color.colorIconGray).sizeDp(48));
            }
        }

        return view;
    }
}
