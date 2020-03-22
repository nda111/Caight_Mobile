package com.example.caight;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class EntryActivity extends AppCompatActivity
{
    private String email, passwd;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
        getSupportActionBar().hide();

        // TODO: change logo

        /*
         * Check auto login
         */
        StaticResources.loginPreferences = getSharedPreferences(StaticResources.LoginPreferenceName, MODE_PRIVATE);
        boolean autoLogin = StaticResources.loginPreferences.getBoolean(StaticResources.LoginPreferenceItemAutoLogin, false);
        if (autoLogin)
        {
            email = StaticResources.loginPreferences.getString(StaticResources.LogInPreferenceItemEmail, null);
            passwd = StaticResources.loginPreferences.getString(StaticResources.LogInPreferenceItemPassword, null);

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
                                    builder.append(passwd);

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
                                    boolean failed = false;
                                    switch (response)
                                    {
                                        case SIGN_IN_OK:
                                        {
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
                                                    Toast.makeText(getApplicationContext(), R.string.errmsg_wrong_pw, Toast.LENGTH_LONG).show();
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
                                                    Toast.makeText(getApplicationContext(), R.string.errmsg_error, Toast.LENGTH_LONG).show();
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
                                        StaticResources.loginPreferences.edit().putBoolean(StaticResources.LoginPreferenceItemAutoLogin, false).apply();
                                        StaticResources.loginPreferences.edit().remove(StaticResources.LogInPreferenceItemEmail).apply();
                                        StaticResources.loginPreferences.edit().remove(StaticResources.LogInPreferenceItemPassword).apply();

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
            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }

            Intent intent = new Intent(getApplicationContext(), LoginEntryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}
