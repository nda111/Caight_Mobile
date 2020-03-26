package com.example.caight;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class EditGroupActivity extends AppCompatActivity
{
    private static final String __JSON_KEY_ID__ = "id";
    private static final String __JSON_KEY_NAME__ = "name";
    private static final String __JSON_KEY_PASSWORD__ = "password";
    private static final String __JSON_KEY_LOCKED__ = "locked";
    private static final String __JSON_KEY_MANAGER__ = "manager";

    private CatGroup group = null;

    private EditText nameEditText = null;
    private EditText pwEditText = null;
    private Switch lockSwitch = null;
    private Spinner managerSpinner = null;
    private Button deleteButton = null;
    private Button saveButton = null;
    private ProgressBar progressBar;

    private ArrayList<MemberArrayAdapter.Item> memberList = null;

    private String name = null;
    private String password = null;
    private Boolean locked = null;
    private MemberArrayAdapter.Item manager = null;

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);
        Resources resources = getResources();
        Resources.Theme theme = getTheme();

        /*
         * Intent
         */
        Intent intent = getIntent();
        int groupId = intent.getIntExtra(MainActivity.__EXTRA_GROUP_ID__, -1);

        for (CatGroup group : StaticResources.Entity.getGroups(EditGroupActivity.this))
        {
            if (groupId == group.getId())
            {
                this.group = group;
                break;
            }
        }
        if (group == null)
        {
            StaticResources.Entity.setUpdateList(EditGroupActivity.this, true);
            Toast.makeText(getApplicationContext(), R.string.err_occurred, Toast.LENGTH_SHORT).show();
            finish();
        }

        /*
         * Action Bar
         */
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(group.getName());
        actionBar.setBackgroundDrawable(new ColorDrawable(ResourcesCompat.getColor(resources, R.color.colorPrimaryDark, theme)));

        /*
         * Initialize GUI Components
         */
        nameEditText = findViewById(R.id.nameEditText);
        pwEditText = findViewById(R.id.passwordEditText);
        lockSwitch = findViewById(R.id.lockSwitch);
        managerSpinner = findViewById(R.id.managerSpinner);
        deleteButton = findViewById(R.id.deleteButton);
        saveButton = findViewById(R.id.saveButton);
        progressBar = findViewById(R.id.progressBar);

        // nameEditText
        nameEditText.setText(group.getName());

        // lockSwitch
        lockSwitch.setChecked(group.isLocked());

        // managerSpinner
        setGuiEnabled(false);
        memberList = new ArrayList<MemberArrayAdapter.Item>();
        try
        {
            new WebSocketConnection(StaticResources.__WS_ADDRESS__)
                    .setRequestAdapter(new WebSocketConnection.RequestAdapter()
                    {
                        ResponseId response;
                        int managerPosition = -1;

                        @Override
                        public void onRequest(WebSocketConnection conn)
                        {
                            conn.send(Methods.intToByteArray(RequestId.DOWNLOAD_MEMBER.getId()), true);
                            conn.send(Methods.intToByteArray(group.getId()), true);
                        }

                        @Override
                        public void onResponse(WebSocketConnection conn, WebSocketConnection.Message message)
                        {
                            if (message.isTextMessage())
                            {
                                String[] args = message.getText().split("\0");

                                if (args[1].equals(StaticResources.Account.getEmail(EditGroupActivity.this)))
                                {
                                    managerPosition = memberList.size();
                                }
                                memberList.add(new MemberArrayAdapter.Item(args[0], args[1]));
                            }
                            else
                            {
                                response = ResponseId.fromId(Methods.byteArrayToInt(message.getBinary()));

                                switch (response)
                                {
                                    case DOWNLOAD_MEMBER_ERROR:
                                    {
                                        Toast.makeText(getApplicationContext(), R.string.err_occurred, Toast.LENGTH_SHORT).show();
                                        break;
                                    }

                                    default:
                                        break;
                                }
                                conn.close();
                            }
                        }

                        @Override
                        public void onClosed()
                        {
                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    System.out.println(response.toString());

                                    if (response == ResponseId.END_OF_MEMBER)
                                    {
                                        managerSpinner.setAdapter(new MemberArrayAdapter(EditGroupActivity.this, R.layout.item_group_spinner, memberList));
                                        managerSpinner.setSelection(managerPosition);

                                        setGuiEnabled(true);
                                    }
                                    else if (response == ResponseId.DOWNLOAD_MEMBER_ERROR)
                                    {
                                        Toast.makeText(getApplicationContext(), R.string.err_occurred, Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }
                            });
                        }
                    }).connect();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        // deleteButton
        deleteButton.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == 1)
                {
                    DeletionConfirmDialog dialog = new DeletionConfirmDialog(R.string.word_delete, group.getName());
                    dialog.setListener(new DeletionConfirmDialog.OnDeletionConfirmListener()
                    {
                        @Override
                        public void onConfirm()
                        {
                            setGuiEnabled(false);

                            try
                            {
                                new WebSocketConnection(StaticResources.__WS_ADDRESS__)
                                        .setRequestAdapter(new WebSocketConnection.RequestAdapter()
                                        {
                                            ResponseId response;

                                            @Override
                                            public void onRequest(WebSocketConnection conn)
                                            {
                                                conn.send(Methods.intToByteArray(RequestId.DROP_GROUP.getId()), true);
                                                conn.send(StaticResources.Account.getId(EditGroupActivity.this), true);
                                                conn.send(StaticResources.Account.getAuthenticationToken(EditGroupActivity.this), true);

                                                conn.send(Methods.intToByteArray(group.getId()), true);
                                            }

                                            @Override
                                            public void onResponse(WebSocketConnection conn, WebSocketConnection.Message message)
                                            {
                                                response = ResponseId.fromId(Methods.byteArrayToInt(message.getBinary()));
                                                conn.close();
                                            }

                                            @Override
                                            public void onClosed()
                                            {
                                                switch (response)
                                                {
                                                    case DROP_GROUP_OK:
                                                    {
                                                        runOnUiThread(new Runnable()
                                                        {
                                                            @Override
                                                            public void run()
                                                            {
                                                                Toast.makeText(getApplicationContext(), R.string.msg_group_deleted, Toast.LENGTH_LONG).show();
                                                                StaticResources.Entity.setUpdateList(EditGroupActivity.this, true);
                                                                finish();
                                                            }
                                                        });
                                                        break;
                                                    }

                                                    case DROP_GROUP_MEMBER_EXISTS:
                                                    {
                                                        runOnUiThread(new Runnable()
                                                        {
                                                            @Override
                                                            public void run()
                                                            {
                                                                Toast.makeText(getApplicationContext(), R.string.err_del_group, Toast.LENGTH_LONG).show();
                                                            }
                                                        });
                                                        break;
                                                    }

                                                    case DROP_GROUP_ERROR:
                                                    {
                                                        runOnUiThread(new Runnable()
                                                        {
                                                            @Override
                                                            public void run()
                                                            {
                                                                Toast.makeText(getApplicationContext(), R.string.err_occurred, Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                        break;
                                                    }

                                                    default:
                                                        break;
                                                }

                                                runOnUiThread(new Runnable()
                                                {
                                                    @Override
                                                    public void run()
                                                    {
                                                        setGuiEnabled(true);
                                                    }
                                                });
                                            }
                                        }).connect();
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onCancel()
                        {
                        }
                    });
                    dialog.show(getSupportFragmentManager(), null);
                }
                return false;
            }
        });

        // saveButton
        saveButton.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == 1)
                {
                    name = nameEditText.getText().toString();
                    if (name.equals(group.getName()))
                    {
                        name = null;
                    }
                    password = pwEditText.getText().toString();
                    if (password.length() == 0)
                    {
                        password = null;
                    }
                    locked = lockSwitch.isChecked();
                    if (locked == group.isLocked())
                    {
                        locked = null;
                    }
                    manager = memberList.get(managerSpinner.getSelectedItemPosition());
                    if (manager.email.equals(StaticResources.Account.getEmail(EditGroupActivity.this)))
                    {
                        manager = null;
                    }

                    if (name != null || password != null || locked != null || manager != null)
                    {
                        setGuiEnabled(false);

                        try
                        {
                            new WebSocketConnection(StaticResources.__WS_ADDRESS__)
                                    .setRequestAdapter(new WebSocketConnection.RequestAdapter()
                                    {
                                        ResponseId response;

                                        @Override
                                        public void onRequest(WebSocketConnection conn)
                                        {
                                            JSONObject json = new JSONObject();
                                            try
                                            {
                                                json.put(__JSON_KEY_ID__, group.getId());

                                                if (name != null)
                                                {
                                                    json.put(__JSON_KEY_NAME__, name);
                                                }
                                                if (password != null)
                                                {
                                                    json.put(__JSON_KEY_PASSWORD__, password);
                                                }
                                                if (locked != null)
                                                {
                                                    json.put(__JSON_KEY_LOCKED__, locked);
                                                }
                                                if (manager != null)
                                                {
                                                    json.put(__JSON_KEY_MANAGER__, manager.email);
                                                }
                                            }
                                            catch (JSONException e)
                                            {
                                                e.printStackTrace();
                                            }

                                            conn.send(Methods.intToByteArray(RequestId.UPDATE_GROUP.getId()), true);
                                            conn.send(StaticResources.Account.getId(EditGroupActivity.this), true);
                                            conn.send(StaticResources.Account.getAuthenticationToken(EditGroupActivity.this), true);

                                            conn.send(json.toString(), true);
                                        }

                                        @Override
                                        public void onResponse(WebSocketConnection conn, WebSocketConnection.Message message)
                                        {
                                            response = ResponseId.fromId(Methods.byteArrayToInt(message.getBinary()));
                                            conn.close();
                                        }

                                        @Override
                                        public void onClosed()
                                        {
                                            switch (response)
                                            {
                                                case UPDATE_GROUP_OK:
                                                {
                                                    runOnUiThread(new Runnable()
                                                    {
                                                        @Override
                                                        public void run()
                                                        {
                                                            StaticResources.Entity.setUpdateList(EditGroupActivity.this, true);
                                                            finish();
                                                        }
                                                    });
                                                    break;
                                                }

                                                case UPDATE_GROUP_ERROR:
                                                {
                                                    Toast.makeText(EditGroupActivity.this, R.string.err_occurred, Toast.LENGTH_SHORT).show();
                                                    break;
                                                }

                                                default:
                                                    break;
                                            }

                                            runOnUiThread(new Runnable()
                                            {
                                                @Override
                                                public void run()
                                                {
                                                    setGuiEnabled(false);
                                                }
                                            });
                                        }
                                    }).connect();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        finish();
                    }
                }
                return false;
            }
        });
    }

    private void setGuiEnabled(boolean enabled)
    {
        nameEditText.setEnabled(enabled);
        pwEditText.setEnabled(enabled);
        lockSwitch.setEnabled(enabled);
        managerSpinner.setEnabled(enabled);
        deleteButton.setEnabled(enabled);
        saveButton.setEnabled(enabled);
        progressBar.setVisibility(enabled ? View.GONE : View.VISIBLE);
    }
}
