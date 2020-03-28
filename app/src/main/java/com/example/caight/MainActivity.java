package com.example.caight;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;
import com.mindorks.placeholderview.ExpandablePlaceHolderView;
import com.scwang.smartrefresh.header.BezierCircleHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    public static final String __EXTRA_GROUP_ID__ = "__GROUP_ID__";
    public static final String __EXTRA_CAT_ID__ = "__CAT_ID__";

    private final MainActivity This = this;

    private ConstraintLayout rootLayout = null;
    private SmartRefreshLayout refreshLayout = null;
    private ExpandablePlaceHolderView entityListView = null;
    private SpeedDialView menuDial = null;
    private SpeedDialActionItem menuDialNewCatItem = null;
    private SpeedDialActionItem menuDialNewGroupItem = null;
    private SpeedDialActionItem menuDialAccountItem = null;
    private ProgressBar progressBar = null;

    private String Email = null;
    private int groupId = -1;

    private final EntityListItemViewBase.OnEntityListItemTouchListener onGroupTouchedListener = new EntityListItemViewBase.OnEntityListItemTouchListener()
    {
        @Override
        public boolean onTouch(EntityListItemViewBase sender, MotionEvent e)
        {
            // NOTHING
            return false;
        }
    };

    private final EntityListItemViewBase.OnEntityListItemTouchListener onGroupDeleteListener = new EntityListItemViewBase.OnEntityListItemTouchListener()
    {
        @Override
        public boolean onTouch(EntityListItemViewBase sender, MotionEvent e)
        {
            if (e.getAction() == 1)
            {
                CatGroup group = ((CatGroupView)sender).getGroup();
                if (!group.getOwnerEmail().equals(StaticResources.Account.getEmail(MainActivity.this)))
                {
                    groupId = group.getId();
                    progressBar.setVisibility(View.VISIBLE);
                    rootLayout.setEnabled(false);

                    try
                    {
                        new WebSocketConnection(StaticResources.__WS_ADDRESS__)
                                .setRequestAdapter(new WebSocketConnection.RequestAdapter()
                                {
                                    ResponseId response;

                                    @Override
                                    public void onRequest(WebSocketConnection conn)
                                    {
                                        conn.send(Methods.intToByteArray(RequestId.WITHDRAW_GROUP.getId()), true);
                                        conn.send(StaticResources.Account.getId(MainActivity.this), true);
                                        conn.send(StaticResources.Account.getAuthenticationToken(MainActivity.this), true);

                                        conn.send(Methods.intToByteArray(groupId), true);
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
                                        runOnUiThread(new Runnable()
                                        {
                                            @Override
                                            public void run()
                                            {
                                                switch (response)
                                                {
                                                    case WITHDRAW_GROUP_OK:
                                                    {
                                                        downloadEntities(false);
                                                        break;
                                                    }

                                                    case WITHDRAW_GROUP_ERROR:
                                                    {
                                                        Toast.makeText(MainActivity.this, R.string.err_occurred, Toast.LENGTH_SHORT).show();
                                                        break;
                                                    }

                                                    default:
                                                        break;
                                                }

                                                progressBar.setVisibility(View.GONE);
                                                rootLayout.setEnabled(true);
                                            }
                                        });
                                    }
                                }).connect();
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }
                else
                {
                    Toast.makeText(MainActivity.this, R.string.msg_manager_cannot, Toast.LENGTH_SHORT).show();
                }
            }
            return false;
        }
    };

    private final EntityListItemViewBase.OnEntityListItemTouchListener onGroupEditListener = new EntityListItemViewBase.OnEntityListItemTouchListener()
    {
        @Override
        public boolean onTouch(EntityListItemViewBase sender, MotionEvent e)
        {
            if (e.getAction() == 1)
            {
                CatGroup group = ((CatGroupView)sender).getGroup();
                if (group.getOwnerEmail().equals(StaticResources.Account.getEmail(MainActivity.this)))
                {
                    Intent intent = new Intent(MainActivity.this, EditGroupActivity.class);
                    intent.putExtra(__EXTRA_GROUP_ID__, group.getId());
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(MainActivity.this, R.string.msg_only_manager, Toast.LENGTH_SHORT).show();
                }
            }
            return false;
        }
    };

    private final EntityListItemViewBase.OnEntityListItemTouchListener onGroupShowQRListener = new EntityListItemViewBase.OnEntityListItemTouchListener()
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
            if (e.getAction() == 1)
            {
                final Cat cat = ((CatView)sender).getCat();
                final CatGroup group = ((CatView)sender).getGroup();

                Intent intent = new Intent(MainActivity.this, CatDetailActivity.class);
                intent.putExtra(__EXTRA_GROUP_ID__, group.getId());
                intent.putExtra(__EXTRA_CAT_ID__, cat.getId());
                startActivity(intent);
            }
            return false;
        }
    };

    private final EntityListItemViewBase.OnEntityListItemTouchListener onCatDeleteListener = new EntityListItemViewBase.OnEntityListItemTouchListener()
    {
        @Override
        public boolean onTouch(EntityListItemViewBase sender, MotionEvent e)
        {
            if (e.getAction() == 1)
            {
                final Cat cat = ((CatView)sender).getCat();
                final CatGroup group = ((CatView)sender).getGroup();

                if (group.getOwnerEmail().equals(StaticResources.Account.getEmail(MainActivity.this)))
                {
                    DeletionConfirmDialog dialog = new DeletionConfirmDialog(R.string.word_delete, cat.getName());
                    dialog.setListener(new DeletionConfirmDialog.OnDeletionConfirmListener()
                    {
                        @Override
                        public void onConfirm()
                        {
                            try
                            {
                                progressBar.setVisibility(View.VISIBLE);
                                rootLayout.setEnabled(false);

                                new WebSocketConnection(StaticResources.__WS_ADDRESS__)
                                        .setRequestAdapter(new WebSocketConnection.RequestAdapter()
                                        {
                                            ResponseId response;

                                            @Override
                                            public void onRequest(WebSocketConnection conn)
                                            {
                                                conn.send(Methods.intToByteArray(RequestId.DROP_CAT.getId()), true);
                                                conn.send(StaticResources.Account.getId(MainActivity.this), true);
                                                conn.send(StaticResources.Account.getAuthenticationToken(MainActivity.this), true);

                                                conn.send(Methods.intToByteArray(cat.getId()), true);
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
                                                runOnUiThread(new Runnable()
                                                {
                                                    @Override
                                                    public void run()
                                                    {
                                                        switch (response)
                                                        {
                                                            case DROP_CAT_OK:
                                                            {
                                                                downloadEntities(false);
                                                                break;
                                                            }

                                                            case DROP_CAT_ERROR:
                                                            {
                                                                Toast.makeText(MainActivity.this, R.string.err_occurred, Toast.LENGTH_SHORT).show();
                                                                break;
                                                            }

                                                            default:
                                                                break;
                                                        }

                                                        progressBar.setVisibility(View.GONE);
                                                        rootLayout.setEnabled(true);
                                                    }
                                                });
                                            }
                                        }).connect();
                            }
                            catch (Exception ex)
                            {
                                ex.printStackTrace();
                            }
                        }

                        @Override
                        public void onCancel()
                        {
                        }
                    });
                    dialog.show(getSupportFragmentManager(), null);
                }
                else
                {
                    Toast.makeText(MainActivity.this, R.string.msg_only_manager, Toast.LENGTH_LONG).show();
                }
            }

            return false;
        }
    };

    private final EntityListItemViewBase.OnEntityListItemTouchListener onCatEditListener = new EntityListItemViewBase.OnEntityListItemTouchListener()
    {
        @Override
        public boolean onTouch(EntityListItemViewBase sender, MotionEvent e)
        {
            if (e.getAction() == 1)
            {
                final Cat cat = ((CatView)sender).getCat();
                final CatGroup group = ((CatView)sender).getGroup();

                Intent intent = new Intent(MainActivity.this, EditCatActivity.class);
                intent.putExtra(__EXTRA_GROUP_ID__, group.getId());
                intent.putExtra(__EXTRA_CAT_ID__, cat.getId());

                startActivity(intent);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        Resources resources = getResources();

        /*
         * Intent
         */
        Intent intent = getIntent();
        Email = intent.getStringExtra(LoginEntryActivity.__KEY_EMAIL__);

        /*
         * Initialize GUI Components
         */
        rootLayout = findViewById(R.id.rootLayout);
        refreshLayout = findViewById(R.id.refreshLayout);
        entityListView = findViewById(R.id.entityListView);
        menuDial = findViewById(R.id.menuDial);
        progressBar = findViewById(R.id.progressBar);

        // refreshLayout
        refreshLayout.setOnRefreshListener(new OnRefreshListener()
        {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout)
            {
                downloadEntities(true);
            }
        });
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator()
        {
            @NonNull
            @Override
            public RefreshHeader createRefreshHeader(@NonNull Context context, @NonNull RefreshLayout layout)
            {
                layout.setPrimaryColorsId(R.color.colorPrimary, R.color.knownWhite);
                return new BezierCircleHeader(context);
            }
        });

        // menuDialNewGroupItem
        menuDialNewGroupItem = new SpeedDialActionItem.Builder(R.id.sdItemAddGroup, R.drawable.ic_new_group)
                .setFabBackgroundColor(Color.WHITE)
                .setFabImageTintColor(resources.getColor(R.color.colorPrimary, getTheme()))
                .setLabel(R.string.act_add_group)
                .setLabelClickable(false)
                .create();

        // menuDialNewCatItem
        menuDialNewCatItem = new SpeedDialActionItem.Builder(R.id.sdItemAddCat, R.drawable.ic_new_cat)
                .setFabBackgroundColor(resources.getColor(R.color.colorPrimary, getTheme()))
                .setLabel(R.string.act_add_cat)
                .setLabelClickable(false)
                .create();

        // menuDialAccountItem
        menuDialAccountItem = new SpeedDialActionItem.Builder(R.id.sdItemAccount, R.drawable.ic_setting)
                .setFabBackgroundColor(Color.WHITE)
                .setFabImageTintColor(resources.getColor(R.color.colorPrimary, getTheme()))
                .setLabel(R.string.act_settings)
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
                    {
                        Intent intent = new Intent(This, AddGroupActivity.class);
                        startActivity(intent);
                        break;
                    }

                    case R.id.sdItemAddCat:
                    {
                        if (StaticResources.Entity.getGroups(MainActivity.this).size() > 0)
                        {
                            Intent intent = new Intent(This, AddCatActivity.class);
                            startActivity(intent);
                        }
                        else
                        {
                            Toast.makeText(This, R.string.err_no_group, Toast.LENGTH_SHORT).show();
                        }
                        break;
                    }

                    case R.id.sdItemAccount:
                    {
                        Intent intent = new Intent(This, SettingsActivity.class);
                        startActivity(intent);
                        break;
                    }

                    default:
                        break;
                }

                return false;
            }
        });

        downloadEntities(false);
        //useCatExample();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (StaticResources.Entity.getUpdateList(this))
        {
            StaticResources.Entity.setUpdateList(this, false);
            downloadEntities(false);
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        System.out.println("MAIN_ACTIVITY_ON_DESTROY");

        StaticResources.Account.clear(this);
        StaticResources.Entity.clear(this);
    }

    private void downloadEntities(final boolean isRefresh)
    {
        rootLayout.setEnabled(false);
        if (!isRefresh)
        {
            progressBar.setVisibility(View.VISIBLE);
        }

        try
        {
            new WebSocketConnection(StaticResources.__WS_ADDRESS__)
                    .setRequestAdapter(new WebSocketConnection.RequestAdapter()
                    {
                        private ResponseId response = null;

                        private CatGroup group = null;
                        private ArrayList<Cat> cats = null;

                        private ArrayList<CatGroup> groups = new ArrayList<CatGroup>();
                        private HashMap<CatGroup, List<Cat>> entries = new HashMap<CatGroup, List<Cat>>();

                        @Override
                        public void onRequest(WebSocketConnection conn)
                        {
                            conn.send(Methods.intToByteArray(RequestId.DOWNLOAD_ENTITY.getId()), true);
                            conn.send(StaticResources.Account.getId(MainActivity.this), true);
                            conn.send(StaticResources.Account.getAuthenticationToken(MainActivity.this), true);
                        }

                        @Override
                        public void onResponse(WebSocketConnection conn, WebSocketConnection.Message message)
                        {
                            if (message.isBinaryMessage())
                            {
                                response = ResponseId.fromId(Methods.byteArrayToInt(message.getBinary()));

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
                            StaticResources.Entity.setEntries(MainActivity.this, groups, entries);

                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    Context context = MainActivity.this;

                                    entityListView.removeAllViews();

                                    for (CatGroup group : groups)
                                    {
                                        CatGroupView groupView = new CatGroupView(context, group);
                                        groupView.setOnTouchListener(onGroupTouchedListener);
                                        groupView.setOnDeleteListener(onGroupDeleteListener);
                                        groupView.setOnEditListener(onGroupEditListener);
                                        groupView.setOnShowQrListener(onGroupShowQRListener);
                                        entityListView.addView(groupView);

                                        ArrayList<Cat> cats = (ArrayList<Cat>)entries.get(group);
                                        for (Cat cat : cats)
                                        {
                                            CatView catView = new CatView(context, cat, group);
                                            catView.setOnTouchListener(onCatTouchedListener);
                                            catView.setOnDeleteListener(onCatDeleteListener);
                                            catView.setOnEditListener(onCatEditListener);
                                            entityListView.addView(catView);
                                        }
                                    }
                                }
                            });

                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    rootLayout.setEnabled(true);
                                    if (isRefresh)
                                    {
                                        refreshLayout.finishRefresh();
                                    }
                                    else
                                    {
                                        progressBar.setVisibility(View.GONE);
                                    }

                                    entityListView.invalidate();

                                    // Toast.makeText(MainActivity.this, R.string.msg_list_updated, Toast.LENGTH_SHORT).show();
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

    /*
    private void useCatExample()
    {
        EntityListItemViewBase.OnEntityListItemTouchListener onViewTouchedListener = new EntityListItemViewBase.OnEntityListItemTouchListener()
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

        final Context context = getApplicationContext();
        Calendar birthday = null;

        CatGroupView yeView = new CatGroupView(context, new CatGroup(15, "오산", "예진", false));

        birthday = Calendar.getInstance();
        birthday.set(2014, 7, 5, 0, 0, 0);
        CatView peroView = new CatView(context, new Cat(0xFFC69C6D, "페로", birthday, Gender.NEUTERED, 84, 6.1F), );

        birthday = Calendar.getInstance();
        birthday.set(2018, 11, 13, 0, 0, 0);
        CatView surView = new CatView(context, new Cat(0xFF6D634F, "수르", birthday, Gender.SPAYED, 3, 2.7F));

        birthday = Calendar.getInstance();
        birthday.set(2019, 3, 23, 0, 0, 0);
        CatView raonView = new CatView(context, new Cat(0xFFEFC146, "라온", birthday, Gender.NEUTERED, 3, 3.4F));


        CatGroupView soView = new CatGroupView(context, new CatGroup(20, "동탄", "소진", false));

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
     */
}
