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
import ru.mew_hpm.sshfilemanager.dao.BackgroundCopyTask;

public class BackgroundCopyTasksAdapter extends BaseAdapter {
    private final BackgroundCopyTasksAdapterActionListener
            actionListener;

    private final LayoutInflater
            lInflater;

    private final ArrayList<BackgroundCopyTask>
            fileArrayList = new ArrayList<>();

    public BackgroundCopyTasksAdapter(Context c, BackgroundCopyTasksAdapterActionListener al) {
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
        final BackgroundCopyTask rf = fileArrayList.get(i);

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

        ((TextView) view.findViewById(R.id.itemTitle)).setText(rf.getFileShortName());
        ((TextView) view.findViewById(R.id.itemSubtext)).setText((rf.isCompleted()) ? "Completed." : "Running...");
        ((TextView) view.findViewById(R.id.itemText)).setText(rf.getTaskName());

        if (rf.isCompleted())
            ((ImageView) view.findViewById(R.id.itemIcon))
                    .setImageDrawable(new IconicsDrawable(view.getContext())
                            .icon(GoogleMaterial.Icon.gmd_done).colorRes(R.color.colorDismounted).sizeDp(48));
        else
            ((ImageView) view.findViewById(R.id.itemIcon))
                    .setImageDrawable(new IconicsDrawable(view.getContext())
                            .icon(GoogleMaterial.Icon.gmd_schedule).colorRes(R.color.colorMounted).sizeDp(48));

        return view;
    }
}
