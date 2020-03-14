package com.example.caight;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;
import com.mindorks.placeholderview.ExpandablePlaceHolderView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    private final MainActivity This = this;

    private ExpandablePlaceHolderView entityListView = null;
    private SpeedDialView menuDial = null;
    private SpeedDialActionItem menuDialNewCatItem = null;
    private SpeedDialActionItem menuDialNewGroupItem = null;
    private SpeedDialActionItem menuDialAccountItem = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        //
        // Initialize string resources
        //
        Resources resources = getResources();
        StringResources.NameExamples = resources.getStringArray(R.array.name_examples);
        StringResources.Species = resources.getStringArray(R.array.species);

        //
        // Initialize GUI Components
        //
        entityListView = findViewById(R.id.entityListView);
        menuDial = findViewById(R.id.menuDial);

        // menuDialNewGroupItem
        menuDialNewGroupItem = new SpeedDialActionItem.Builder(R.id.sdItemAddGroup, R.drawable.ic_new_group)
                .setFabBackgroundColor(Color.WHITE)
                .setFabImageTintColor(resources.getColor(R.color.colorAccent, getTheme()))
                .setLabel(R.string.act_new_group)
                .setLabelClickable(false)
                .create();

        // menuDialNewCatItem
        menuDialNewCatItem = new SpeedDialActionItem.Builder(R.id.sdItemAddCat, R.drawable.ic_new_cat)
                .setFabBackgroundColor(resources.getColor(R.color.colorAccent, getTheme()))
                .setLabel(R.string.act_new_cat)
                .setLabelClickable(false)
                .create();

        // menuDialAccountItem
        menuDialAccountItem = new SpeedDialActionItem.Builder(R.id.sdItemAccount, R.drawable.ic_account)
                .setFabBackgroundColor(Color.WHITE)
                .setFabImageTintColor(resources.getColor(R.color.colorAccent, getTheme()))
                .setLabel(R.string.act_account)
                .setLabelClickable(false)
                .create();

        // menuDial
        menuDial.addActionItem(menuDialNewGroupItem);
        menuDial.addActionItem(menuDialNewCatItem);
        menuDial.addActionItem(menuDialAccountItem);
        menuDial.setOnActionSelectedListener(new SpeedDialView.OnActionSelectedListener()
        {
            @Override
            public boolean onActionSelected(SpeedDialActionItem actionItem)
            {
                switch (actionItem.getId())
                {
                case R.id.sdItemAddGroup:
                    break;

                case R.id.sdItemAddCat:
                    Intent intent = new Intent(This, AddNewActivity.class);
                    startActivity(intent);
                    break;

                case R.id.sdItemAccount:
                    break;

                default:
                    break;
                }

                return false;
            }
        });

        useCatExample();
    }

    private void useCatExample()
    {
        final Context context = getApplicationContext();
        Calendar birthday = null;

        CatGroupView yeView = new CatGroupView(context, new CatGroup(15, "오산", "예진"));

        birthday = Calendar.getInstance();
        birthday.set(2014, 7, 5, 0, 0, 0);
        CatView peroView = new CatView(context, new Cat(0xFFC69C6D, "페로", birthday, Gender.NEUTERED, 84, 6.1F));

        birthday = Calendar.getInstance();
        birthday.set(2018, 11, 13, 0, 0, 0);
        CatView surView = new CatView(context, new Cat(0xFF6D634F, "수르", birthday, Gender.SPAYED, 3, 2.7F));

        birthday = Calendar.getInstance();
        birthday.set(2019, 3, 23, 0, 0, 0);
        CatView raonView = new CatView(context, new Cat(0xFFEFC146, "라온", birthday, Gender.NEUTERED, 3, 3.4F));

        entityListView.addView(yeView);
        entityListView.addView(peroView);
        entityListView.addView(surView);
        entityListView.addView(raonView);
    }
}
