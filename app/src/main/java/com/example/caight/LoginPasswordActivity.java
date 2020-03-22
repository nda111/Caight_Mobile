package com.example.caight;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class LoginPasswordActivity extends AppCompatActivity
{
    private String email = null;
    private boolean doAutoLogin = false;

    private Animation ShakeAnimation = null;
    private AnimatedVectorDrawable ShowPasswordAnimation = null;
    private AnimatedVectorDrawable HidePasswordAnimation = null;

    private EditText pwEditText = null;
    private ImageView clearTextImageView = null;
    private ImageView revealImageButton = null;
    private TextView errTextView = null;
    private ProgressBar progressBar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_password);
        getSupportActionBar().hide();

        /*
         * Intent
         */
        Intent intent = getIntent();
        email = intent.getStringExtra(LoginEntryActivity.__KEY_EMAIL__);
        doAutoLogin = intent.getBooleanExtra(LoginEntryActivity.__KEY_AUTO_LOGIN__, false);

        /*
         * Resources
         */
        Resources resources = getResources();
        ShakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake_twice);
        ShowPasswordAnimation = (AnimatedVectorDrawable)resources.getDrawable(R.drawable.ic_anim_pw_show, getTheme());
        HidePasswordAnimation = (AnimatedVectorDrawable)resources.getDrawable(R.drawable.ic_anim_pw_hide, getTheme());

        /*
         * Initialize GUI Components
         */
        pwEditText = findViewById(R.id.pwEditText);
        clearTextImageView = findViewById(R.id.clearTextImageView);
        revealImageButton = findViewById(R.id.revealImageButton);
        errTextView = findViewById(R.id.errTextView);
        progressBar = findViewById(R.id.progressBar);
        // pwEditText
        pwEditText.setOnKeyListener(new View.OnKeyListener()
        {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER)
                {
                    String pw = pwEditText.getText().toString();
                    if (pw.length() == 0)
                    {
                        errTextView.setText(R.string.errmsg_enter_pw);
                        errTextView.setVisibility(View.VISIBLE);
                        errTextView.startAnimation(ShakeAnimation);
                    }
                    else
                    {
                        pwEditText.setEnabled(false);
                        revealImageButton.setEnabled(false);
                        errTextView.setVisibility(View.GONE);
                        progressBar.setVisibility(View.VISIBLE);

                        try
                        {
                            WebSocketConnection conn = new WebSocketConnection(StringResources.__WS_ADDRESS__)
                                    .setRequestAdapter(new WebSocketConnection.RequestAdapter()
                                    {
                                        ResponseId response;
                                        int cnt = 0;

                                        @Override
                                        public void onRequest(WebSocketConnection conn)
                                        {
                                            StringBuilder builder = new StringBuilder();
                                            builder.append(email);
                                            builder.append('\0');
                                            builder.append(pwEditText.getText().toString());

                                            conn.send(StaticMethods.intToByteArray(RequestId.SIGN_IN.getId()), true);
                                            conn.send(builder.toString(), true);
                                        }

                                        @Override
                                        public void onResponse(WebSocketConnection conn, WebSocketConnection.Message message)
                                        {
                                            boolean close = false;

                                            switch (++cnt)
                                            {
                                                case 1:
                                                    response = ResponseId.fromId(StaticMethods.byteArrayToInt(message.getBinary()));
                                                    close = response != ResponseId.SIGN_IN_OK;
                                                    break;

                                                case 2:
                                                    StaticResources.accountId = message.getBinary();
                                                    break;

                                                case 3:
                                                    StaticResources.authToken = message.getText();
                                                    break;

                                                case 4:
                                                    StaticResources.myName = message.getText();
                                                    close = true;
                                                    break;

                                                default:
                                                    break;
                                            }

                                            if (close)
                                            {
                                                conn.close();
                                            }
                                        }

                                        @Override
                                        public void onClosed()
                                        {
                                            switch (response)
                                            {
                                                case SIGN_IN_OK:
                                                {
                                                    if (doAutoLogin)
                                                    {
                                                        StaticResources.loginPreferences.edit().putBoolean(StaticResources.LoginPreferenceItemAutoLogin, true).apply();
                                                        StaticResources.loginPreferences.edit().putString(StaticResources.LogInPreferenceItemEmail, email).apply();
                                                        StaticResources.loginPreferences.edit().putString(StaticResources.LogInPreferenceItemPassword, pwEditText.getText().toString()).apply();
                                                    }

                                                    StaticResources.myEmail = email;

                                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                    intent.putExtra(LoginEntryActivity.__KEY_EMAIL__, email);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(intent);
                                                    break;
                                                }

                                                case SIGN_IN_WRONG_PW:
                                                {
                                                    runOnUiThread(new Runnable()
                                                    {
                                                        @Override
                                                        public void run()
                                                        {
                                                            errTextView.setText(R.string.errmsg_wrong_pw);
                                                            errTextView.setVisibility(View.VISIBLE);
                                                            errTextView.startAnimation(ShakeAnimation);
                                                        }
                                                    });
                                                    break;
                                                }

                                                case SING_IN_ERROR:
                                                {
                                                    runOnUiThread(new Runnable()
                                                    {
                                                        @Override
                                                        public void run()
                                                        {
                                                            Toast.makeText(LoginPasswordActivity.this, R.string.errmsg_error, Toast.LENGTH_LONG).show();
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
                                                    pwEditText.setEnabled(true);
                                                    revealImageButton.setEnabled(true);
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
                return false;
            }
        });

        // clearTextImageView
        clearTextImageView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                pwEditText.setText("");

                return false;
            }
        });

        //revealImageButton
        revealImageButton.setOnTouchListener(new View.OnTouchListener()
        {
            private boolean showPassword = false;

            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                showPassword = !showPassword;
                if (showPassword)
                {
                    pwEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                    revealImageButton.setImageDrawable(ShowPasswordAnimation);
                    ShowPasswordAnimation.start();
                }
                else
                {
                    pwEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    revealImageButton.setImageDrawable(HidePasswordAnimation);
                    HidePasswordAnimation.start();
                }

                return false;
            }
        });
    }
}
