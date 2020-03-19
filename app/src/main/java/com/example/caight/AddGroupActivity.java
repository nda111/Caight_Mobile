package com.example.caight;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class AddGroupActivity extends AppCompatActivity
{
    private EditText nameEditText = null;
    private CheckBox nameValidCheckBox = null;
    private EditText pwEditText = null;
    private CheckBox pwValidCheckBox = null;
    private Button registerButton = null;
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
        nameEditText = findViewById(R.id.nameEditText);
        nameValidCheckBox = findViewById(R.id.nameValidCheckBox);
        pwEditText = findViewById(R.id.pwEditText);
        pwValidCheckBox = findViewById(R.id.pwValidCheckBox);
        registerButton = findViewById(R.id.registerButton);
        progressBar = findViewById(R.id.progressBar);

        // nameEditText
        nameEditText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
            }

            @Override
            public void afterTextChanged(Editable s)
            {
                nameValidCheckBox.setChecked(s.length() >=2);
            }
        });

        // pwEditText
        pwEditText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
            }

            @Override
            public void afterTextChanged(Editable s)
            {
                pwValidCheckBox.setChecked(s.length() > 0);
            }
        });

        // registerButton
        registerButton.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (nameValidCheckBox.isChecked() && pwValidCheckBox.isChecked())
                {
                    nameEditText.setEnabled(false);
                    registerButton.setEnabled(false);
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
                                        String groupName = nameEditText.getText().toString();
                                        String passwd = pwEditText.getText().toString();

                                        conn.send(StaticMethods.intToByteArray(RequestId.NEW_GROUP.getId()), true);

                                        conn.send(StaticResources.accountId, true);
                                        conn.send(StaticResources.authToken, true);
                                        conn.send(groupName + '\0' + passwd, true);
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
                                                Toast.makeText(getApplication(), R.string.errmsg_other_device_logged_in, Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(getApplicationContext(), EntryActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                break;
                                            }

                                            case ADD_ENTITY_ERROR:
                                            {
                                                Toast.makeText(getApplication(), R.string.errmsg_error, Toast.LENGTH_SHORT).show();
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

                return false;
            }
        });
    }
}