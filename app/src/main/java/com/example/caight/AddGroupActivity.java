package com.example.caight;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mindorks.placeholderview.ExpandablePlaceHolderView;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class AddGroupActivity extends AppCompatActivity
{
    private String[] addArgument = null;

    private ExpandablePlaceHolderView listView = null;
    private AddGroupHeaderView createHeader = null;
    private AddGroupFormView createGroupView = null;
    private AddGroupHeaderView joinHeader = null;
    private AddGroupFormView joinGroupView = null;
    private ProgressBar progressBar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        getSupportActionBar().hide();

        /*
         * Initialize GUI Components
         */
        listView = findViewById(R.id.listView);
        progressBar = findViewById(R.id.progressBar);

        // listView
        listView.addView(createHeader = new AddGroupHeaderView(this, R.drawable.ic_account_circle, R.string.menu_create_group, R.string.desc_create_group));
        listView.addView(createGroupView = new AddGroupFormView(this, R.string.attr_name, InputType.TYPE_TEXT_VARIATION_PERSON_NAME, R.string.act_create_group));
        listView.addView(joinHeader = new AddGroupHeaderView(this, R.drawable.ic_account_circle, R.string.menu_join_group, R.string.desc_join_group));
        listView.addView(joinGroupView = new AddGroupFormView(this, R.string.attr_id, InputType.TYPE_NUMBER_VARIATION_NORMAL, R.string.act_join_group));

        // createGroupView
        createGroupView.onTouchListener = new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == 1)
                {
                    if (createGroupView.isValid())
                    {
                        addArgument = createGroupView.getResult();
                        createGroup();
                    }
                    else
                    {
                        Toast.makeText(AddGroupActivity.this, R.string.msg_fill_form, Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        };

        // joinGroupView
        joinGroupView.onTouchListener = new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == 1)
                {
                    if (joinGroupView.isValid())
                    {
                        addArgument = joinGroupView.getResult();

                        for (CatGroup group : StaticResources.groups)
                        {
                            if (addArgument[0].equals(String.valueOf(group.getId())))
                            {
                                Toast.makeText(AddGroupActivity.this, R.string.err_join_joined, Toast.LENGTH_SHORT).show();
                                return false;
                            }
                        }

                        joinGroup();
                    }
                    else
                    {
                        Toast.makeText(AddGroupActivity.this, R.string.msg_fill_form, Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        };
    }

    private void createGroup()
    {
        listView.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        try
        {
            WebSocketConnection conn = new WebSocketConnection(StringResources.__WS_ADDRESS__)
                    .setRequestAdapter(new WebSocketConnection.RequestAdapter()
                    {
                        ResponseId response;

                        @Override
                        public void onRequest(WebSocketConnection conn)
                        {
                            String groupName = addArgument[0];
                            String password = addArgument[1];

                            conn.send(StaticMethods.intToByteArray(RequestId.NEW_GROUP.getId()), true);

                            conn.send(StaticResources.accountId, true);
                            conn.send(StaticResources.authToken, true);
                            conn.send(groupName + '\0' + password, true);
                        }

                        @Override
                        public void onResponse(WebSocketConnection conn, WebSocketConnection.Message message)
                        {
                            response = ResponseId.fromId(StaticMethods.byteArrayToInt(message.getBinary()));
                            conn.close();
                        }

                        @Override
                        public void onClosed()
                        {
                            switch (response)
                            {
                                case ADD_ENTITY_OK:
                                {
                                    StaticResources.updateEntityList = true;
                                    break;
                                }

                                case ADD_ENTITY_NO:
                                {
                                    Toast.makeText(getApplication(), R.string.err_other_device_logged_in, Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), EntryActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    break;
                                }

                                case ADD_ENTITY_ERROR:
                                {
                                    Toast.makeText(getApplication(), R.string.err_occurred, Toast.LENGTH_SHORT).show();
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
                                    finish();
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

    private void joinGroup()
    {
        listView.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        try
        {
            new WebSocketConnection(StringResources.__WS_ADDRESS__)
                    .setRequestAdapter(new WebSocketConnection.RequestAdapter()
                    {
                        ResponseId response;

                        @Override
                        public void onRequest(WebSocketConnection conn)
                        {
                            String groupId = addArgument[0];
                            String password = addArgument[1];

                            conn.send(StaticMethods.intToByteArray(RequestId.JOIN_GROUP.getId()), true);

                            conn.send(StaticResources.accountId, true);
                            conn.send(StaticResources.authToken, true);
                            conn.send(groupId + '\0' + password, true);
                        }

                        @Override
                        public void onResponse(WebSocketConnection conn, WebSocketConnection.Message message)
                        {
                            response = ResponseId.fromId(StaticMethods.byteArrayToInt(message.getBinary()));
                            conn.close();
                        }

                        @Override
                        public void onClosed()
                        {
                            switch (response)
                            {
                                case JOIN_GROUP_OK:
                                {
                                    StaticResources.updateEntityList = true;
                                    runOnUiThread(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            finish();
                                        }
                                    });

                                    break;
                                }

                                case JOIN_GROUP_NOT_EXISTS:
                                {
                                    runOnUiThread(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            Toast.makeText(AddGroupActivity.this, R.string.err_join_not_exists, Toast.LENGTH_LONG).show();
                                        }
                                    });
                                    break;
                                }

                                case JOIN_GROUP_REJECTED:
                                {
                                    runOnUiThread(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            Toast.makeText(AddGroupActivity.this, R.string.err_join_rejected, Toast.LENGTH_LONG).show();
                                        }
                                    });
                                    break;
                                }

                                case JOIN_GROUP_WRONG_PASSWORD:
                                {
                                    runOnUiThread(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            Toast.makeText(AddGroupActivity.this, R.string.err_join_wrong_password, Toast.LENGTH_LONG).show();
                                        }
                                    });
                                    break;
                                }

                                case JOIN_GROUP_ERROR:
                                {
                                    runOnUiThread(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            Toast.makeText(AddGroupActivity.this, R.string.err_occurred, Toast.LENGTH_SHORT).show();
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
                                    listView.setEnabled(true);
                                    progressBar.setVisibility(View.GONE);
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
}