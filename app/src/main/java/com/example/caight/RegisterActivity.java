package com.example.caight;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity<EditTExt> extends AppCompatActivity
{
    private final RegisterActivity This = this;

    private final Pattern StrongPasswordPattern = Pattern.compile("((?=.*[a-z])(?=.*[0-9])(?=.*[!@#$%^&*])(?=.*[A-Z]).{8,})");
    private final Pattern[] PasswordConstraints = new Pattern[] {
            Pattern.compile("(.*[a-z].*)"),
            Pattern.compile("(.*[A-Z].*)"),
            Pattern.compile("(.*[0-9].*)"),
            Pattern.compile("(.*[!@#$%^&*].*)"),
            Pattern.compile("(.{8,})"),
    };

    private Animation ShakeAnimation = null;

    private AnimatedVectorDrawable RegisterOkAnimation = null;
    private AnimatedVectorDrawable RegisterNoAnimation = null;

    private TextView emailTextView = null;
    private EditText pwEditText = null;
    private CheckBox[] pwConstraintCheckBoxes = null;
    private EditText nameEditText = null;
    private TextView nameErrTextView = null;
    private ImageView registerButton = null;
    private ProgressBar progressBar = null;

    private boolean passwordValid = false;
    private boolean nameValid = false;
    private boolean buttonValid = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();

        /*
         * Resources
         */
        // Animations
        ShakeAnimation = AnimationUtils.loadAnimation(This, R.anim.shake_twice);
        RegisterOkAnimation = (AnimatedVectorDrawable)getResources().getDrawable(R.drawable.ic_anim_register_ok, getTheme());
        RegisterNoAnimation = (AnimatedVectorDrawable)getResources().getDrawable(R.drawable.ic_anim_register_no, getTheme());

        /*
         * Initialize GUI Components
         */
        Intent intent = getIntent();
        emailTextView = findViewById(R.id.emailTextView);
        pwEditText = findViewById(R.id.pwEditText);
        pwConstraintCheckBoxes = new CheckBox[] {
                findViewById(R.id.pwConstraintCheckBox1),
                findViewById(R.id.pwConstraintCheckBox2),
                findViewById(R.id.pwConstraintCheckBox3),
                findViewById(R.id.pwConstraintCheckBox4),
                findViewById(R.id.pwConstraintCheckBox5),
        };
        nameEditText = findViewById(R.id.nameEditText);
        nameErrTextView = findViewById(R.id.nameErrTextView);
        registerButton = findViewById(R.id.registerButton);
        progressBar = findViewById(R.id.progressBar);

        // emailTextView
        emailTextView.setText(intent.getStringExtra(LoginEntryActivity.__KEY_EMAIL__));

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
                String password = pwEditText.getText().toString();
                Matcher matcher = StrongPasswordPattern.matcher(password);
                if (matcher.matches())
                {
                    for (int i = 0; i < 5; i++)
                    {
                        pwConstraintCheckBoxes[i].setChecked(true);
                    }
                    passwordValid = true;
                }
                else
                {
                    for (int i = 0; i < 5; i++)
                    {
                        boolean checked = PasswordConstraints[i].matcher(password).matches();
                        if (pwConstraintCheckBoxes[i].isChecked() != checked)
                        {
                            pwConstraintCheckBoxes[i].setChecked(checked);
                            if (!checked)
                            {
                                pwConstraintCheckBoxes[i].startAnimation(ShakeAnimation);
                            }
                        }
                    }
                    passwordValid = false;
                }

                setRegisterButtonEnabled();
            }
        });

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
                String name = nameEditText.getText().toString();
                int length = name.length();
                if (length < 2 || 15 < length)
                {
                    nameErrTextView.setText(R.string.name_constraint_number);
                    nameErrTextView.setVisibility(View.VISIBLE);
                    nameErrTextView.startAnimation(ShakeAnimation);
                    nameValid = false;
                }
                else
                {
                    nameErrTextView.setVisibility(View.GONE);
                    nameValid = true;
                }

                setRegisterButtonEnabled();
            }
        });

        // registerButton
        registerButton.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (passwordValid && nameValid)
                {
                    pwEditText.setEnabled(false);
                    nameEditText.setEnabled(false);
                    registerButton.setEnabled(false);
                    progressBar.setVisibility(View.VISIBLE);

                    try
                    {
                        WebSocketConnection conn = new WebSocketConnection(StaticResources.__WS_ADDRESS__)
                                .setRequestAdapter(new WebSocketConnection.RequestAdapter()
                                {
                                    private ResponseId response = ResponseId.UNKNOWN;

                                    @Override
                                    public void onRequest(WebSocketConnection conn)
                                    {
                                        conn.send(Methods.intToByteArray(RequestId.REGISTER_EMAIL.getId()), true);

                                        StringBuilder builder = new StringBuilder();
                                        builder.append(emailTextView.getText().toString());
                                        builder.append('\0');
                                        builder.append(pwEditText.getText().toString());
                                        builder.append('\0');
                                        builder.append(nameEditText.getText().toString());

                                        conn.send(builder.toString(), true);
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
                                            case REGISTER_OK:
                                            {
                                                Intent intent = new Intent(This, VerifyingGuideActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                break;
                                            }

                                            case REGISTER_NO:
                                            {
                                                runOnUiThread(new Runnable()
                                                {
                                                    @Override
                                                    public void run()
                                                    {
                                                        Toast.makeText(getApplicationContext(), R.string.err_occurred, Toast.LENGTH_LONG).show();
                                                        This.finish();
                                                    }
                                                });
                                                break;
                                            }

                                            default:
                                                break;
                                        }
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

    private void setRegisterButtonEnabled()
    {
        boolean valid = passwordValid && nameValid;
        if (buttonValid != valid)
        {
            buttonValid = valid;

            if (valid)
            {
                registerButton.setImageDrawable(RegisterOkAnimation);
                RegisterOkAnimation.start();
            }
            else
            {
                registerButton.setImageDrawable(RegisterNoAnimation);
                RegisterNoAnimation.start();
            }
        }
    }
}
