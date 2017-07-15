package ru.mew_hpm.sshfilemanager.ui.activities;

import android.app.Activity;
import android.widget.ListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import ru.mew_hpm.sshfilemanager.R;

@EActivity(R.layout.activity_bgtasks)
public class BgTasksActivity extends Activity  {

    @ViewById
    ListView fmListBgTasks;

    @AfterViews
    void initThis() {

    }


}
