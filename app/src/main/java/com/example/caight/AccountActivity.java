package com.example.caight;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class AccountActivity extends AppCompatActivity
{
    private ScrollView scrollView = null;
    private TextView nameTextView = null;
    private EditText nameEditText = null;
    private TextView emailTextView = null;
    private ImageView nameEditImageView = null;
    private FrameLayout logoutItem = null;
    private FrameLayout resetPwItem = null;
    private FrameLayout delAccountItem = null;
    private ProgressBar progressBar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        Resources resources = getResources();
        Resources.Theme theme = getTheme();

        /*
         * Action Bar
         */
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.menu_account);
        actionBar.setBackgroundDrawable(new ColorDrawable(ResourcesCompat.getColor(resources, R.color.colorPrimaryDark, theme)));

        /*
         * Initialize GUI Components
         */
        scrollView = findViewById(R.id.scrollView);
        nameTextView = findViewById(R.id.nameTextView);
        nameEditText = findViewById(R.id.nameEditText);
        emailTextView = findViewById(R.id.emailTextView);
        nameEditImageView = findViewById(R.id.nameEditImageView);
        logoutItem = findViewById(R.id.logoutItem);
        resetPwItem = findViewById(R.id.resetPwItem);
        delAccountItem = findViewById(R.id.delAccountItem);
        progressBar = findViewById(R.id.progressBar);

        // nameTextView
        nameTextView.setText(StaticResources.Account.getName(this));

        // nameEditImageView
        nameEditImageView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                nameTextView.setVisibility(View.INVISIBLE);
                nameEditImageView.setVisibility(View.GONE);
                nameEditText.setVisibility(View.VISIBLE);
                nameEditText.setEnabled(true);
                return false;
            }
        });

        // nameEditText
        nameEditText.setText(StaticResources.Account.getName(this));
        nameEditText.setOnKeyListener(new View.OnKeyListener()
        {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER)
                {
                    nameTextView.setVisibility(View.VISIBLE);
                    nameEditImageView.setVisibility(View.VISIBLE);
                    nameEditText.setVisibility(View.INVISIBLE);
                    nameEditText.setEnabled(false);

                    String newName = nameEditText.getText().toString().trim();
                    if (newName.length() != 0 && !newName.equals(StaticResources.Account.getName(AccountActivity.this)))
                    {
                        scrollView.setEnabled(false);
                        progressBar.setVisibility(View.VISIBLE);

                        try
                        {
                            new WebSocketConnection(StaticResources.__WS_ADDRESS__)
                                    .setRequestAdapter(new WebSocketConnection.RequestAdapter()
                                    {
                                        ResponseId response;
                                        String name;

                                        @Override
                                        public void onRequest(WebSocketConnection conn)
                                        {
                                            name = nameEditText.getText().toString().trim();

                                            conn.send(Methods.intToByteArray(RequestId.CHANGE_NAME.getId()), true);
                                            conn.send(StaticResources.Account.getId(AccountActivity.this), true);
                                            conn.send(StaticResources.Account.getAuthenticationToken(AccountActivity.this), true);

                                            conn.send(name, true);
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
                                            runOnUiThread(new Runnable()
                                            {
                                                @Override
                                                public void run()
                                                {
                                                    if (response == ResponseId.CHANGE_NAME_OK)
                                                    {
                                                        StaticResources.Account.setName(AccountActivity.this, name);
                                                        nameTextView.setText(name);
                                                    }
                                                    else
                                                    {
                                                        nameEditText.setText(StaticResources.Account.getName(AccountActivity.this));
                                                        Toast.makeText(AccountActivity.this, R.string.err_occurred, Toast.LENGTH_SHORT).show();
                                                    }

                                                    scrollView.setEnabled(true);
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
                        nameEditText.setText(StaticResources.Account.getName(AccountActivity.this));
                    }
                }

                return false;
            }
        });

        // emailTextView
        emailTextView.setText(StaticResources.Account.getEmail(AccountActivity.this));

        // logoutItem
        View.inflate(this, R.layout.view_icon_item, logoutItem);
        ((ImageView)logoutItem.findViewById(R.id.iconImageView)).setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_logout_circle));
        ((TextView)logoutItem.findViewById(R.id.nameTextView)).setText(resources.getText(R.string.menu_account_logout));
        ((TextView)logoutItem.findViewById(R.id.descriptionTextView)).setText(resources.getText(R.string.desc_account_logout));
        logoutItem.setOnTouchListener(new View.OnTouchListener()
        {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                try
                {
                    if (event.getAction() == 1)
                    {
                        scrollView.setEnabled(false);
                        progressBar.setVisibility(View.VISIBLE);

                        new WebSocketConnection(StaticResources.__WS_ADDRESS__)
                                .setRequestAdapter(new WebSocketConnection.RequestAdapter()
                                {
                                    ResponseId response;

                                    @Override
                                    public void onRequest(WebSocketConnection conn)
                                    {
                                        conn.send(Methods.intToByteArray(RequestId.LOGOUT.getId()), true);
                                        conn.send(StaticResources.Account.getId(AccountActivity.this), true);
                                        conn.send(StaticResources.Account.getAuthenticationToken(AccountActivity.this), true);
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
                                        if (response == ResponseId.LOGOUT_OK)
                                        {
                                            StaticResources.AutoLogin.clear(AccountActivity.this);

                                            Intent intent = new Intent(getApplicationContext(), LoginEntryActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                        }
                                        else
                                        {
                                            runOnUiThread(new Runnable()
                                            {
                                                @Override
                                                public void run()
                                                {
                                                    scrollView.setEnabled(true);
                                                    progressBar.setVisibility(View.GONE);

                                                    Toast.makeText(AccountActivity.this, R.string.err_occurred, Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }
                                }).connect();
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                return false;
            }
        });

        // resetPwItem
        View.inflate(this, R.layout.view_icon_item, resetPwItem);
        ((ImageView)resetPwItem.findViewById(R.id.iconImageView)).setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_reset_pw_circle));
        ((TextView)resetPwItem.findViewById(R.id.nameTextView)).setText(resources.getText(R.string.menu_account_reset_pw));
        ((TextView)resetPwItem.findViewById(R.id.descriptionTextView)).setText(resources.getText(R.string.desc_account_reset_pw));
        resetPwItem.setOnTouchListener(new View.OnTouchListener()
        {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == 1)
                {
                    try
                    {
                        progressBar.setVisibility(View.VISIBLE);
                        scrollView.setEnabled(false);

                        new WebSocketConnection(StaticResources.__WS_ADDRESS__)
                                .setRequestAdapter(new WebSocketConnection.RequestAdapter()
                                {
                                    ResponseId response;

                                    @Override
                                    public void onRequest(WebSocketConnection conn)
                                    {
                                        conn.send(Methods.intToByteArray(RequestId.REQUEST_RESET_PASSWORD_uri.getId()), true);
                                        conn.send(StaticResources.Account.getEmail(AccountActivity.this), true);
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
                                            case RESET_PASSWORD_URI_CREATED:
                                            {
                                                runOnUiThread(new Runnable()
                                                {
                                                    @Override
                                                    public void run()
                                                    {
                                                        Toast.makeText(getApplicationContext(), R.string.msg_reset_mail_sent, Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                                break;
                                            }

                                            case RESET_PASSWORD_URI_ERROR:
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
                                                progressBar.setVisibility(View.GONE);
                                                scrollView.setEnabled(true);
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

        // delAccountItem
        View.inflate(this, R.layout.view_icon_item, delAccountItem);
        ((ImageView)delAccountItem.findViewById(R.id.iconImageView)).setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_delete_circle));
        ((ImageView)delAccountItem.findViewById(R.id.iconImageView)).setColorFilter(ContextCompat.getColor(this, R.color.warning_red));
        ((TextView)delAccountItem.findViewById(R.id.nameTextView)).setText(resources.getText(R.string.menu_account_delete_account));
        ((TextView)delAccountItem.findViewById(R.id.nameTextView)).setTextColor(resources.getColor(R.color.warning_red, theme));
        ((TextView)delAccountItem.findViewById(R.id.descriptionTextView)).setText(resources.getText(R.string.desc_account_delete_account));
        ((TextView)delAccountItem.findViewById(R.id.descriptionTextView)).setTextColor(resources.getColor(R.color.warning_red, theme));
        delAccountItem.setOnTouchListener(new View.OnTouchListener()
        {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == 1)
                {
                    String accountEmail = StaticResources.Account.getEmail(AccountActivity.this);
                    for (CatGroup group : StaticResources.Entity.getGroups(AccountActivity.this))
                    {
                        if (group.getOwnerEmail().equals(accountEmail))
                        {
                            Toast.makeText(AccountActivity.this, R.string.err_hand_over_group, Toast.LENGTH_LONG).show();
                            return false;
                        }
                    }

                    Intent intent = new Intent(AccountActivity.this, DeleteAccountActivity.class);
                    startActivity(intent);
                }

                return false;
            }
        });
    }
}
