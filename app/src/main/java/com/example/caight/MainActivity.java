package com.example.caight;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //
        // Initialize string resources
        //
        Resources resources = getResources();
        StringResources.NameExamples = resources.getStringArray(R.array.name_examples); 
        StringResources.Species = resources.getStringArray(R.array.species);

        getSupportActionBar().hide();
    }

    public void showAddNewActivity(View view)
    {
        Intent intent = new Intent(this, AddNewActivity.class);
        startActivity(intent);
    }
}
