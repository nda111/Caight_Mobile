package com.example.caight;

import androidx.appcompat.app.AppCompatActivity;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.neovisionaries.ws.client.WebSocket;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;

public class LoginPasswordActivity extends AppCompatActivity
{
    private String email = null;

    private Animation ShakeAnimation = null;
    private AnimatedVectorDrawable ShowPasswordAnimation = null;
    private AnimatedVectorDrawable HidePasswordAnimation = null;

    private EditText pwEditText = null;
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
                                            response = ResponseId.fromId(StaticMethods.byteArrayToInt(message.getBinary()));
                                            conn.close();
                                        }

                                        @Override
                                        public void onClosed()
                                        {
                                            switch (response)
                                            {
                                                case SIGN_IN_OK:
                                                {
                                                    // TODO
                                                    runOnUiThread(new Runnable()
                                                    {
                                                        @Override
                                                        public void run()
                                                        {
                                                            Toast.makeText(LoginPasswordActivity.this, "SIGN_IN_OK", Toast.LENGTH_LONG).show();
                                                        }
                                                    });
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
