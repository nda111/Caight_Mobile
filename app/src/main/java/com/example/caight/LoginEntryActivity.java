package com.example.caight;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginEntryActivity extends AppCompatActivity
{
    public static final String __KEY_REGISTER_EMAIL__ = "__REGISTER_EMAIL__";

    private static final Pattern EmailRegex = Pattern.compile("^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");

    private final LoginEntryActivity This = this;

    private Animation ShakeAnimation = null;

    private EditText emailEditText = null;
    private TextView errorTextView = null;
    private ProgressBar progressBar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_entry);
        getSupportActionBar().hide();

        /*
         * Resources
         */
        ShakeAnimation = AnimationUtils.loadAnimation(This, R.anim.shake_twice);

        /*
         * Initialize GUI Components
         */
        emailEditText = findViewById(R.id.emailEditText);
        errorTextView = findViewById(R.id.errTextView);
        progressBar = findViewById(R.id.progressBar);

        // emailEditText
        emailEditText.setOnKeyListener(new View.OnKeyListener()
        {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER)
                {
                    Matcher matcher = EmailRegex.matcher(emailEditText.getText());
                    if (matcher.matches())
                    {
                        errorTextView.setVisibility(View.GONE);
                        emailEditText.setEnabled(false);
                        progressBar.setVisibility(View.VISIBLE);
                        nextActivity();
                    }
                    else
                    {
                        errorTextView.setText(R.string.errmsg_not_email);
                        errorTextView.setVisibility(View.VISIBLE);
                        errorTextView.startAnimation(ShakeAnimation);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void nextActivity()
    {
        ResponseId response = ResponseId.UNKNOWN_EMAIL;

        try
        {
            WebSocketConnection conn = new WebSocketConnection(StringResources.__WS_ADDRESS__)
                    .setRequestAdapter(new WebSocketConnection.RequestAdapter()
                    {
                        private String email = null;
                        private ResponseId response = ResponseId.UNKNOWN;

                        @Override
                        public void onRequest(WebSocketConnection conn)
                        {
                            byte[] id = StaticMethods.intToByteArray(RequestId.EVALUATE_EMAIL.getId());
                            conn.send(id, true);

                            email = emailEditText.getText().toString();
                            conn.send(email, true);
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
                                case UNKNOWN_EMAIL:
                                {
                                    Intent intent = new Intent(This, RegisterActivity.class);
                                    intent.putExtra(__KEY_REGISTER_EMAIL__, email);
                                    startActivity(intent);
                                    break;
                                }

                                case REGISTERED_EMAIL:
                                {
                                    runOnUiThread(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            errorTextView.setText(R.string.errmsg_cert_first);
                                            errorTextView.setVisibility(View.VISIBLE);
                                            errorTextView.startAnimation(ShakeAnimation);
                                        }
                                    });
                                    break;
                                }

                                case VERIFIED_EMAIL:
                                {
                                    // TODO
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
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                        }
                    })
                    .connect();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
