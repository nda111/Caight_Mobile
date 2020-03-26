package com.example.caight;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.util.Objects;
import java.util.concurrent.Executors;

public class EntryActivity extends AppCompatActivity
{
    private String email, passwd;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
        Objects.requireNonNull(getSupportActionBar()).hide();

        /*
         * Initialize string resources
         */
        StaticResources.StringArrays.initializeIfNotExists(this);

        /*
         * Check auto login
         */
        if (StaticResources.AutoLogin.getDoAutoLogin(EntryActivity.this))
        {
            email = StaticResources.AutoLogin.getEmail(EntryActivity.this);
            passwd = StaticResources.AutoLogin.getPassword(EntryActivity.this);

            if (email == null || passwd == null)
            {
                Intent intent = new Intent(getApplicationContext(), LoginEntryActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
            else
            {
                try
                {
                    WebSocketConnection conn = new WebSocketConnection(StaticResources.__WS_ADDRESS__)
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
                                    builder.append(passwd);

                                    conn.send(Methods.intToByteArray(RequestId.SIGN_IN.getId()), true);
                                    conn.send(builder.toString(), true);
                                }

                                @Override
                                public void onResponse(WebSocketConnection conn, WebSocketConnection.Message message)
                                {
                                    boolean close = false;

                                    switch (++cnt)
                                    {
                                        case 1:
                                            response = ResponseId.fromId(Methods.byteArrayToInt(message.getBinary()));
                                            close = response != ResponseId.SIGN_IN_OK;
                                            break;

                                        case 2:
                                            StaticResources.Account.setId(EntryActivity.this, message.getBinary());
                                            break;

                                        case 3:
                                            StaticResources.Account.setAuthenticationToken(EntryActivity.this, message.getText());
                                            break;

                                        case 4:
                                            StaticResources.Account.setName(EntryActivity.this, message.getText());
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
                                    boolean failed = false;
                                    switch (response)
                                    {
                                        case SIGN_IN_OK:
                                        {
                                            StaticResources.Account.setEmail(EntryActivity.this, email);

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
                                                    Toast.makeText(getApplicationContext(), R.string.err_wrong_pw, Toast.LENGTH_LONG).show();
                                                }
                                            });
                                            failed = true;
                                            break;
                                        }

                                        case SING_IN_ERROR:
                                        {
                                            runOnUiThread(new Runnable()
                                            {
                                                @Override
                                                public void run()
                                                {
                                                    Toast.makeText(getApplicationContext(), R.string.err_occurred, Toast.LENGTH_LONG).show();
                                                }
                                            });
                                            failed = true;
                                            break;
                                        }

                                        default:
                                            break;
                                    }

                                    if (failed)
                                    {
                                        StaticResources.AutoLogin.set(EntryActivity.this, false, null, null);

                                        Intent intent = new Intent(getApplicationContext(), LoginEntryActivity.class);
                                        intent.putExtra(LoginEntryActivity.__KEY_EMAIL__, email);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }
                                }
                            }).connect();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        else
        {
            Executors.newSingleThreadExecutor().execute(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        Thread.sleep(1500);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }

                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Intent intent = new Intent(getApplicationContext(), LoginEntryActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    });
                }
            });
        }
    }
}
