package com.example.caight;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;

import java.util.List;
import java.util.Map;
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
        String email = emailEditText.getText().toString();
        ResponseId response = ResponseId.UNKNOWN_EMAIL;


        try
        {
            WebSocketConnection conn = new WebSocketConnection(StringResources.__WS_ADDRESS__)
                    .setRequestAdapter(new WebSocketConnection.RequestAdapter()
                    {
                        @Override
                        public void onRequest(WebSocketConnection conn)
                        {
                            System.out.println("Connected");
                            conn.send("Hello", true);
                            conn.send(new byte[]{ 0, 1, 2 }, true);
                            conn.send("quit", true);
                        }

                        @Override
                        public void onResponse(WebSocketConnection conn, WebSocketConnection.Message message)
                        {
                            String text = message.toString();
                            if (text.equalsIgnoreCase("quit"))
                            {
                                conn.close();
                            }
                            System.out.println(text);
                        }

                        @Override
                        public void onClosed()
                        {
                            System.out.println("Closed");
                        }
                    })
                    .connect();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        switch (response)
        {
        case UNKNOWN_EMAIL:
        {
            Intent intent = new Intent(this, RegisterActivity.class);
            intent.putExtra(__KEY_REGISTER_EMAIL__, email);
            startActivity(intent);
            break;
        }

        case REGISTERED_EMAIL:
        {
            errorTextView.setText(R.string.errmsg_cert_first);
            errorTextView.setVisibility(View.VISIBLE);
            errorTextView.startAnimation(ShakeAnimation);
            break;
        }

        case CERTIFIED_EMAIL:
        {
            // TODO
            break;
        }

        default:
            break;
        }
    }
}
