package ru.mew_hpm.sshfilemanager.ui.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import ru.mew_hpm.sshfilemanager.R;

@EActivity(R.layout.activity_ssh_add_new)
public class SSHServerDataActivity extends AppCompatActivity {
    @ViewById
    EditText editHost;

    @ViewById
    EditText editPort;

    @ViewById
    EditText editUserName;

    @ViewById
    EditText editUserText;

    @ViewById
    EditText editPassword;

    @ViewById
    EditText editInitDir;

    @ViewById
    Button buttonDelete;

    private long id;

    public SSHServerDataActivity() { }

    @AfterViews
    void initThis() {
        final Intent intent = getIntent();
        id = intent.getLongExtra("id", Integer.MAX_VALUE);
        if (id == Integer.MAX_VALUE) {
            buttonDelete.setVisibility(View.INVISIBLE);
        } else {
            editHost.setText(intent.getStringExtra("host"));
            editPort.setText(intent.getStringExtra("port"));
            editUserName.setText(intent.getStringExtra("username"));
            editUserText.setText(intent.getStringExtra("userText"));
            editPassword.setText(intent.getStringExtra("password"));
            editInitDir.setText(intent.getStringExtra("initDir"));
        }
    }

    @Click(R.id.buttonSave)
    void OnSaveClick() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("id", id);
        resultIntent.putExtra("host", editHost.getText().toString().trim());
        resultIntent.putExtra("port", editPort.getText().toString().trim());
        resultIntent.putExtra("username", editUserName.getText().toString().trim());
        resultIntent.putExtra("userText", editUserText.getText().toString().trim());
        resultIntent.putExtra("password", editPassword.getText().toString());
        resultIntent.putExtra("initDir", editInitDir.getText().toString().trim());
        setResult(1, resultIntent);
        finish();
    }

    @Click(R.id.buttonDelete)
    void OnDeleteClick() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("id", id);
        setResult(2, resultIntent);
        finish();
    }

    @Click(R.id.buttonCancel)
    void OnCancelClick() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("id", id);
        setResult(0, resultIntent);
        finish();
    }
}
