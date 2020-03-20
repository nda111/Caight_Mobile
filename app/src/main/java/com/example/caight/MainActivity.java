package com.example.caight;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;
import com.mindorks.placeholderview.ExpandablePlaceHolderView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    private final MainActivity This = this;

    private ExpandablePlaceHolderView entityListView = null;
    private SpeedDialView menuDial = null;
    private SpeedDialActionItem menuDialNewCatItem = null;
    private SpeedDialActionItem menuDialNewGroupItem = null;
    private SpeedDialActionItem menuDialAccountItem = null;
    private ProgressBar progressBar = null;

    private String Email = null;

    private final EntityListItemViewBase.OnEntityListItemTouchListener onViewTouchedListener = new EntityListItemViewBase.OnEntityListItemTouchListener()
    {
        @Override
        public boolean onTouch(EntityListItemViewBase sender, MotionEvent e)
        {
            String name = null;
            if (sender instanceof CatView)
            {
                CatView view = (CatView)sender;
                name = Integer.toString(view.getCat().getColorInteger());
            }
            else
            {
                //CatGroupView view = (CatGroupView)sender;
                //name = view.getGroup().getName();
            }

            //Toast.makeText(This, name, Toast.LENGTH_SHORT).show();

            return false;
        }
    };

    private final EntityListItemViewBase.OnEntityListItemTouchListener onGroupTouchedListener = new EntityListItemViewBase.OnEntityListItemTouchListener()
    {
        @Override
        public boolean onTouch(EntityListItemViewBase sender, MotionEvent e)
        {
            // TODO
            return false;
        }
    };

    private final EntityListItemViewBase.OnEntityListItemTouchListener onCatTouchedListener = new EntityListItemViewBase.OnEntityListItemTouchListener()
    {
        @Override
        public boolean onTouch(EntityListItemViewBase sender, MotionEvent e)
        {
            // TODO;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        //
        // Intent
        //
        Intent intent = getIntent();
        Email = intent.getStringExtra(LoginEntryActivity.__KEY_EMAIL__);

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
        progressBar = findViewById(R.id.progressBar);

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
                Intent intent = null;

                switch (actionItem.getId())
                {
                    case R.id.sdItemAddGroup:
                        intent = new Intent(This, AddGroupActivity.class);
                        startActivity(intent);
                        break;

                    case R.id.sdItemAddCat:
                        if (StaticResources.groups.size() > 0)
                        {
                            intent = new Intent(This, AddCatActivity.class);
                            startActivity(intent);
                        }
                        else
                        {
                            Toast.makeText(This, R.string.errmsg_no_group, Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case R.id.sdItemAccount:
                        // TODO
                        break;

                    default:
                        break;
                }

                return false;
            }
        });

        downloadEntities();
        //useCatExample();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (StaticResources.updateEntityList)
        {
            StaticResources.updateEntityList = false;
            downloadEntities();
        }
    }

    private void downloadEntities()
    {
        entityListView.setEnabled(false);
        menuDial.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        try
        {
            WebSocketConnection conn = new WebSocketConnection(StringResources.__WS_ADDRESS__)
                    .setRequestAdapter(new WebSocketConnection.RequestAdapter()
                    {
                        private ResponseId response = null;

                        private CatGroup group = null;
                        private ArrayList<Cat> cats = new ArrayList<Cat>();

                        private ArrayList<CatGroup> groups = new ArrayList<CatGroup>();
                        private HashMap<CatGroup, List<Cat>> entries = new HashMap<CatGroup, List<Cat>>();

                        @Override
                        public void onRequest(WebSocketConnection conn)
                        {
                            conn.send(StaticMethods.intToByteArray(RequestId.DOWNLOAD_ENTITY.getId()), true);

                            conn.send(StaticResources.accountId, true);
                            conn.send(StaticResources.authToken, true);
                        }

                        @Override
                        public void onResponse(WebSocketConnection conn, WebSocketConnection.Message message)
                        {
                            if (message.isBinaryMessage())
                            {
                                response = ResponseId.fromId(StaticMethods.byteArrayToInt(message.getBinary()));

                                if (response == ResponseId.END_OF_ENTITY)
                                {
                                    if (group != null)
                                    {
                                        groups.add(group);
                                        entries.put(group, cats);
                                    }
                                    conn.close();
                                }
                            }
                            else // if (message.isTextMessage())
                            {
                                String data = message.getText();

                                switch (response)
                                {
                                    case ENTITY_GROUP:
                                    {
                                        if (group != null)
                                        {
                                            groups.add(group);
                                            entries.put(group, cats);
                                        }

                                        try
                                        {
                                            JSONObject json = new JSONObject(data);

                                            group = CatGroup.parseJson(json);
                                            cats = new ArrayList<Cat>();
                                        }
                                        catch (JSONException e)
                                        {
                                            e.printStackTrace();
                                        }
                                        break;
                                    }

                                    case ENTITY_CAT:
                                    {
                                        try
                                        {
                                            JSONObject json = new JSONObject(data);
                                            Cat cat = Cat.parseJson(json);
                                            cats.add(cat);
                                        }
                                        catch (JSONException e)
                                        {
                                            e.printStackTrace();
                                        }
                                        break;
                                    }

                                    default:
                                        break;
                                }
                            }
                        }

                        @Override
                        public void onClosed()
                        {
                            StaticResources.groups = groups;
                            StaticResources.entries = entries;

                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    Context context = MainActivity.this;

                                    for (CatGroup group : groups)
                                    {
                                        CatGroupView groupView = new CatGroupView(context, group);
                                        groupView.setOnTouchListener(onGroupTouchedListener);
                                        entityListView.addView(groupView);

                                        ArrayList<Cat> cats = (ArrayList<Cat>)entries.get(group);
                                        for (Cat cat : cats)
                                        {
                                            CatView catView = new CatView(context, cat);
                                            catView.setOnTouchListener(onCatTouchedListener);
                                            entityListView.addView(groupView);
                                        }
                                    }
                                }
                            });

                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    entityListView.setEnabled(true);
                                    menuDial.setEnabled(true);
                                    progressBar.setVisibility(View.GONE);

                                    Toast.makeText(MainActivity.this, R.string.msg_list_updated, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).connect();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
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


        CatGroupView soView = new CatGroupView(context, new CatGroup(20, "동탄", "소진"));

        birthday = Calendar.getInstance();
        birthday.set(2014, 7, 5, 0, 0, 0);
        CatView tiniView = new CatView(context, new Cat(0xFFC69C6D, "티니", birthday, Gender.NEUTERED, 84, 6.1F));

        birthday = Calendar.getInstance();
        birthday.set(2018, 11, 13, 0, 0, 0);
        CatView roniView = new CatView(context, new Cat(0xFF6D634F, "로니", birthday, Gender.SPAYED, 3, 2.7F));

        birthday = Calendar.getInstance();
        birthday.set(2019, 3, 23, 0, 0, 0);
        CatView nikoView = new CatView(context, new Cat(0xFFEFC146, "니코", birthday, Gender.NEUTERED, 3, 3.4F));


        entityListView.addView(yeView);
        yeView.setOnTouchListener(onViewTouchedListener);

        entityListView.addView(peroView);
        peroView.setOnTouchListener(onViewTouchedListener);

        entityListView.addView(surView);
        surView.setOnTouchListener(onViewTouchedListener);

        entityListView.addView(raonView);
        raonView.setOnTouchListener(onViewTouchedListener);

        entityListView.addView(soView);
        soView.setOnTouchListener(onViewTouchedListener);

        entityListView.addView(tiniView);
        tiniView.setOnTouchListener(onViewTouchedListener);

        entityListView.addView(roniView);
        roniView.setOnTouchListener(onViewTouchedListener);

        entityListView.addView(nikoView);
        nikoView.setOnTouchListener(onViewTouchedListener);


        CatGroup yeGroup = yeView.getGroup();
        ArrayList<Cat> yeCats = new ArrayList<Cat>();
        StaticResources.groups.add(yeGroup);
        yeCats.add(peroView.getCat());
        yeCats.add(surView.getCat());
        yeCats.add(raonView.getCat());
        StaticResources.entries.put(yeGroup, yeCats);

        CatGroup soGroup = soView.getGroup();
        ArrayList<Cat> soCats = new ArrayList<Cat>();
        StaticResources.groups.add(soGroup);
        soCats.add(tiniView.getCat());
        soCats.add(roniView.getCat());
        soCats.add(nikoView.getCat());
        StaticResources.entries.put(soGroup, soCats);
    }
}
