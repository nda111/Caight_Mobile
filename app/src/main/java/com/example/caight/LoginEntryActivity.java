package com.example.caight;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginEntryActivity extends AppCompatActivity
{
    public static final String __KEY_EMAIL__ = "__EMAIL__";
    public static final String __KEY_AUTO_LOGIN__ = "__AUTO_LOGIN__";

    private static final Pattern EmailRegex = Pattern.compile("^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");

    private final LoginEntryActivity This = this;

    private Animation ShakeAnimation = null;

    private EditText emailEditText = null;
    private ImageView clearTextImageView= null;
    private TextView errorTextView = null;
    private CheckBox autoLoginCheckBox = null;
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
        clearTextImageView = findViewById(R.id.clearTextImageView);
        errorTextView = findViewById(R.id.errTextView);
        autoLoginCheckBox = findViewById(R.id.autoLoginCheckBox);
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
                        autoLoginCheckBox.setEnabled(false);
                        nextActivity();
                    }
                    else
                    {
                        errorTextView.setText(R.string.err_not_email);
                        errorTextView.setVisibility(View.VISIBLE);
                        errorTextView.startAnimation(ShakeAnimation);
                    }
                    return true;
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
                emailEditText.setText("");
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
                                    intent.putExtra(__KEY_EMAIL__, email);
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
                                            errorTextView.setText(R.string.err_cert_first);
                                            errorTextView.setVisibility(View.VISIBLE);
                                            errorTextView.startAnimation(ShakeAnimation);
                                        }
                                    });
                                    break;
                                }

                                case VERIFIED_EMAIL:
                                {
                                    Intent intent = new Intent(This, LoginPasswordActivity.class);
                                    intent.putExtra(__KEY_EMAIL__, email);
                                    intent.putExtra(__KEY_AUTO_LOGIN__, autoLoginCheckBox.isChecked());
                                    startActivity(intent);
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
                                    autoLoginCheckBox.setEnabled(true);
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
