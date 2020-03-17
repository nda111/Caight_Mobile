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
import android.widget.Toast;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;

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

        switch (response)
        {
        case UNKNOWN_EMAIL:
        {
            Executors.newSingleThreadExecutor().execute(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        WebSocketFactory factory = new WebSocketFactory().setConnectionTimeout(5000);
                        SSLContext context = SSLContext.getInstance("TLS");
                        context.init(null, null, null);
                        factory.setSSLContext(context);
                        WebSocket ws = factory.createSocket("wss://caight.herokuapp.com/ws");
                        ws.addListener(new WebSocketAdapter()
                        {
                            @Override
                            public void onError(WebSocket websocket, WebSocketException cause)
                            {
                                Toast.makeText(This, cause.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception
                            {
                                System.out.println("Connected");
                                websocket.sendText("Hello World!!!", true);
                            }

                            @Override
                            public void onTextMessage(WebSocket websocket, String text) throws Exception
                            {
                                System.out.println(text);

                                websocket.sendClose();
                            }

                            @Override
                            public void onBinaryMessage(WebSocket websocket, byte[] binary) throws Exception
                            {
                                StringBuilder builder = new StringBuilder();
                                for (byte b : binary)
                                {
                                    builder.append(b);
                                    builder.append(' ');
                                }
                                Toast.makeText(This, builder.toString(), Toast.LENGTH_LONG).show();
                            }
                        });
                        ws.connect();

                        System.out.println("CONNECTING..."); // TODO: checkout my stack overflow question
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });

            //Intent intent = new Intent(this, RegisterActivity.class);
            //intent.putExtra(__KEY_REGISTER_EMAIL__, email);
            //startActivity(intent);
            break;
        }

        case REGISTERED_EMAIL:
        {
            break;
        }

        case CERTIFIED_EMAIL:
        {
            break;
        }

        default:
            break;
        }
    }
}
