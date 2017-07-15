package ru.mew_hpm.sshfilemanager.ui.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

import java.util.HashMap;
import java.util.Map;

import ru.mew_hpm.sshfilemanager.R;
import ru.mew_hpm.sshfilemanager.dao.SSHServerData;
import ru.mew_hpm.sshfilemanager.ui.fragments.FileManagerFragment;
import ru.mew_hpm.sshfilemanager.ui.fragments.FileManagerFragmentEventListener;
import ru.mew_hpm.sshfilemanager.ui.fragments.FileManagerFragment_;
import ru.mew_hpm.sshfilemanager.ui.fragments.SSHServersFragment;
import ru.mew_hpm.sshfilemanager.ui.fragments.SSHServersFragmentEventListener;
import ru.mew_hpm.sshfilemanager.ui.fragments.SSHServersFragment_;
import ru.mew_hpm.sshfilemanager.ui.fragments.WaiterFragment;
import ru.mew_hpm.sshfilemanager.ui.fragments.WaiterFragment_;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity implements FileManagerFragmentEventListener, SSHServersFragmentEventListener {
    boolean runFirst = true;

    private final SSHServersFragment
            sshServersSelector = new SSHServersFragment_();

    private final WaiterFragment
            waiterFragment = new WaiterFragment_();

    private final FileManagerFragment
            fileManagerFragment = new FileManagerFragment_();

    private String
            currentFragment = null,
            lastFragment = null;

    private final Map<String, Fragment> fragments = new HashMap<String, Fragment>() {{
        put("server_list", sshServersSelector);
        put("wait", waiterFragment);
        put("fileManager", fileManagerFragment);
    }};

    private void showFragment(String name) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        for (Fragment f : fragments.values()) {
            fragmentTransaction.hide(f);
        }

        if (fragments.containsKey(name)) {
            fragmentTransaction.show(fragments.get(name));
            currentFragment = name;
        }

        fragmentTransaction.commit();
    }

    private void showWait(boolean wait) {
        if (wait) {
            lastFragment = currentFragment;
            showFragment("wait");
        } else {
            showFragment(lastFragment);
        }
    }

    @AfterViews
    void initThis() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!runFirst) return;

        if (savedInstanceState == null) {
            fileManagerFragment.create(this.getApplicationContext(), this);
            waiterFragment.create();

            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            for (Fragment f : fragments.values()) {
                fragmentTransaction.add(R.id.rootContainer, f);
                fragmentTransaction.hide(f);
            }
            fragmentTransaction.commit();

            showFragment("server_list");

            sshServersSelector.setActionListener(this);
            fileManagerFragment.setActionListener(this);

            runFirst = false;
        }
    }

    @Override
    public void OnSSHConnect(String name) {
        showFragment("fileManager");
    }

    @Override
    public void OnSSHError(String threadName, String elName) {
        showFragment("server_list");
    }

    @Override
    public void OnWaitStart() {
        showWait(true);
    }

    @Override
    public void OnWaitEnd() {
        showWait(false);
    }

    @Override
    public void OnDisconnectClick() {
        showFragment("server_list");
    }

    @Override
    public void OnItemClick(SSHServerData sshd) {
        fileManagerFragment.connect(sshd);
        showWait(true);
    }
}
