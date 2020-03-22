package com.example.caight;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

public class SettingsActivity extends AppCompatActivity
{
    private ListView settingListView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Resources resources = getResources();
        Resources.Theme theme = getTheme();

        /*
         * Action Bar
         */
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.title_settings);
        actionBar.setBackgroundDrawable(new ColorDrawable(ResourcesCompat.getColor(resources, R.color.colorPrimaryDark, theme)));

        /*
         * Initialize GUI Components
         */
        settingListView = findViewById(R.id.settingListView);

        // settingListView
        settingListView.setDivider(null);
        settingListView.setAdapter(new IconItemAdapter()
                .add(this, R.drawable.ic_account_circle, R.string.menu_account, R.string.desc_account)
                .add(this, R.drawable.ic_alert_circle, R.string.menu_notifications, R.string.desc_notifications));
        settingListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                switch (position)
                {
                    case 0: // Account
                    {
                        Intent intent = new Intent(SettingsActivity.this, AccountActivity.class);
                        startActivity(intent);
                        break;
                    }

                    case 1: // Notifications
                    {
                        break;
                    }

                    default:
                        break;
                }
            }
        });
    }
}