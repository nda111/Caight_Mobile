package com.example.caight;

import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.neovisionaries.ws.client.WebSocket;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class DeleteAccountActivity extends AppCompatActivity
{
    private EditText emailEditText = null;
    private Button deleteButton = null;
    private ProgressBar progressBar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);
        Resources resources = getResources();
        Resources.Theme theme = getTheme();

        /*
         * Action Bar
         */
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.title_del_account);
        actionBar.setBackgroundDrawable(new ColorDrawable(ResourcesCompat.getColor(resources, R.color.colorPrimaryDark, theme)));

        /*
         * Initialize GUi Components
         */
        emailEditText = findViewById(R.id.emailEditText);
        deleteButton = findViewById(R.id.deleteButton);
        progressBar = findViewById(R.id.progressBar);

        // deleteButton
        deleteButton.setOnTouchListener(new View.OnTouchListener()
        {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == 1)
                {
                    if (emailEditText.getText().toString().equals(StaticResources.myEmail))
                    {
                        emailEditText.setEnabled(false);
                        deleteButton.setEnabled(false);
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
                                            conn.send(StaticMethods.intToByteArray(RequestId.DELETE_ACCOUNT.getId()), true);
                                            conn.send(StaticResources.accountId, true);
                                            conn.send(StaticResources.authToken, true);
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
                                                case DELETE_ACCOUNT_OK:
                                                {
                                                    getSharedPreferences(StaticResources.LoginPreferenceName, MODE_PRIVATE).edit().clear().apply();

                                                    Toast.makeText(getApplicationContext(), R.string.msg_deleted, Toast.LENGTH_SHORT).show();

                                                    Intent intent = new Intent(getApplicationContext(), LoginEntryActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(intent);
                                                    break;
                                                }

                                                case DELETE_ACCOUNT_NO:
                                                {
                                                    runOnUiThread(new Runnable()
                                                    {
                                                        @Override
                                                        public void run()
                                                        {
                                                            Toast.makeText(DeleteAccountActivity.this, R.string.err_occurred, Toast.LENGTH_SHORT).show();
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
                                                    emailEditText.setEnabled(true);
                                                    deleteButton.setEnabled(true);
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
                    else
                    {
                        Toast.makeText(DeleteAccountActivity.this, R.string.err_enter_email, Toast.LENGTH_SHORT).show();
                    }
                }

                return false;
            }
        });

    }
}
