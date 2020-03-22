package com.example.caight;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AccountActivity extends AppCompatActivity
{
    private TextView nameTextView = null;
    private EditText nameEditText = null;
    private TextView emailTextView = null;
    private ImageView nameEditImageView = null;
    private FrameLayout logoutItem = null;
    private FrameLayout resetPwItem = null;
    private FrameLayout delAccountItem = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        Resources resources = getResources();
        Resources.Theme theme = getTheme();

        /*
         * Action Bar
         */
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.menu_account);
        actionBar.setBackgroundDrawable(new ColorDrawable(ResourcesCompat.getColor(resources, R.color.colorPrimaryDark, theme)));

        /*
         * Initialize GUI Components
         */
        nameTextView = findViewById(R.id.nameTextView);
        nameEditText = findViewById(R.id.nameEditText);
        emailTextView = findViewById(R.id.emailTextView);
        nameEditImageView = findViewById(R.id.nameEditImageView);
        logoutItem = findViewById(R.id.logoutItem);
        resetPwItem = findViewById(R.id.resetPwItem);
        delAccountItem = findViewById(R.id.delAccountItem);

        // nameTextView
        nameTextView.setText(StaticResources.myName);

        // nameEditText
        nameEditText.setText(StaticResources.myName);

        // nameEditImageView
        nameEditImageView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                Toast.makeText(AccountActivity.this, "Edit name", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        // emailTextView
        emailTextView.setText(StaticResources.myEmail);

        // logoutItem
        View.inflate(this, R.layout.view_icon_item, logoutItem);
        ((ImageView)logoutItem.findViewById(R.id.iconImageView)).setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_logout_circle));
        ((TextView)logoutItem.findViewById(R.id.nameTextView)).setText(resources.getText(R.string.menu_account_logout));
        ((TextView)logoutItem.findViewById(R.id.descriptionTextView)).setText(resources.getText(R.string.desc_account_logout));

        // resetPwItem
        View.inflate(this, R.layout.view_icon_item, resetPwItem);
        ((ImageView)resetPwItem.findViewById(R.id.iconImageView)).setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_reset_pw_circle));
        ((TextView)resetPwItem.findViewById(R.id.nameTextView)).setText(resources.getText(R.string.menu_account_reset_pw));
        ((TextView)resetPwItem.findViewById(R.id.descriptionTextView)).setText(resources.getText(R.string.desc_account_reset_pw));

        // delAccountItem
        View.inflate(this, R.layout.view_icon_item, delAccountItem);
        ((ImageView)delAccountItem.findViewById(R.id.iconImageView)).setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_delete_circle));
        ((ImageView)delAccountItem.findViewById(R.id.iconImageView)).setColorFilter(ContextCompat.getColor(this, R.color.warning_red));
        ((TextView)delAccountItem.findViewById(R.id.nameTextView)).setText(resources.getText(R.string.menu_account_delete_account));
        ((TextView)delAccountItem.findViewById(R.id.nameTextView)).setTextColor(resources.getColor(R.color.warning_red, theme));
        ((TextView)delAccountItem.findViewById(R.id.descriptionTextView)).setText(resources.getText(R.string.desc_account_delete_account));
        ((TextView)delAccountItem.findViewById(R.id.descriptionTextView)).setTextColor(resources.getColor(R.color.warning_red, theme));
    }
}
