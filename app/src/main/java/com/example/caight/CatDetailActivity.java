package com.example.caight;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class CatDetailActivity extends AppCompatActivity
{
    private class SyncData
    {
        static final int SYNC_TYPE_INSERT = 0;
        static final int SYNC_TYPE_UPDATE = 1;
        static final int SYNC_TYPE_DELETE = 2;

        int type = -1;
        long date = -1;
        float oldValue = -1;
        float newValue = -1;

        SyncData(int type, long date)
        {
            this.type = type;
            this.date = date;
        }

        SyncData(int type, long date, float newValue)
        {
            this(type, date);
            this.newValue = newValue;
        }

        SyncData(int type, long date, float newValue, float oldValue)
        {
            this(type, date, newValue);
            this.oldValue = oldValue;
        }
    }

    private static final String __JSON_KEY_UPSERT__ = "upsert";
    private static final String __JSON_KEY_DELETE__ = "delete";
    private static final String __JSON_KEY_DATE__ = "date";
    private static final String __JSON_KEY_WEIGHT__ = "weight";

    private ConstraintLayout rootLayout;
    private ImageView backImageView;
    private ImageView nameWrapperImageView;
    private TextView nameTextView;
    private ChipGroup attrChipGroup;
    private LineChartView weightChart;
    private TextView dateFromTextView, dateToTextView;
    private ImageView uploadImageView;
    private ImageView revertImageView;
    private TextView newDateTextView;
    private EditText newWeightEditText;
    private RecyclerView weightList;
    private ProgressBar progressBar;

    private Cat cat = null;

    private Date nowFrom = null;
    private Date nowTo = null;

    private int signatureColor;
    private int darkerColor;
    private int textColor;

    private Date newDate = null;

    private HashMap<Long, SyncData> syncs = new HashMap<Long, SyncData>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cat_detail);

        nowFrom = Date.getToday();
        if (nowFrom.getMonth() <= 3)
        {
            nowFrom.setYear(nowFrom.getYear() - 1);
            nowFrom.setMonth(12 + nowFrom.getMonth() - 3);
        }
        nowTo = Date.getToday();

        /*
         * ActionBar
         */
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        /*
         * Initialize GUi Components
         */
        rootLayout = findViewById(R.id.rootLayout);
        backImageView = findViewById(R.id.backImageView);
        nameWrapperImageView = findViewById(R.id.nameWrapperImageView);
        nameTextView = findViewById(R.id.nameTextView);
        attrChipGroup = findViewById(R.id.attributeChipGroup);
        weightChart = findViewById(R.id.weightChart);
        dateFromTextView = findViewById(R.id.dateFromTextView);
        dateToTextView = findViewById(R.id.dateToTextView);
        uploadImageView = findViewById(R.id.uploadImageView);
        revertImageView = findViewById(R.id.revertImageView);
        newDateTextView = findViewById(R.id.newDateTextView);
        newWeightEditText = findViewById(R.id.newWeightEditText);
        weightList = findViewById(R.id.weightList);
        progressBar = findViewById(R.id.progressBar);

        // backImageView
        backImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onBackPressed();
            }
        });

        // dateFromTextView
        dateFromTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DatePickerDialog dialog = new DatePickerDialog(CatDetailActivity.this);
                dialog.getDatePicker().init(
                        nowFrom.getYear(),
                        nowFrom.getMonth(),
                        nowFrom.getDay(),
                        null);

                dialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
                    {
                        nowFrom = new Date(year, month, dayOfMonth);
                        updateChart(nowFrom, nowTo);
                    }
                });

                dialog.show();
            }
        });

        // dateToTextView
        dateToTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DatePickerDialog dialog = new DatePickerDialog(CatDetailActivity.this);
                dialog.getDatePicker().init(
                        nowTo.getYear(),
                        nowTo.getMonth(),
                        nowTo.getDay(),
                        null);

                dialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
                    {
                        nowTo = new Date(year, month, dayOfMonth);
                        updateChart(nowFrom, nowTo);
                    }
                });

                dialog.show();
            }
        });

        // uploadImageView
        uploadImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (syncs.size() != 0)
                {
                    setGuiEnabled(false);

                    try
                    {
                        new WebSocketConnection(StaticResources.__WS_ADDRESS__)
                                .setRequestAdapter(new WebSocketConnection.RequestAdapter()
                                {
                                    ResponseId response;

                                    @Override
                                    public void onRequest(WebSocketConnection conn)
                                    {
                                        JSONObject weights = new JSONObject();
                                        JSONArray upsert = new JSONArray();
                                        JSONArray delete = new JSONArray();

                                        try
                                        {
                                            for (SyncData data : syncs.values())
                                            {
                                                double dWeight = Double.parseDouble(Float.toString(data.newValue));

                                                switch (data.type)
                                                {
                                                    case SyncData.SYNC_TYPE_INSERT:
                                                    case SyncData.SYNC_TYPE_UPDATE:
                                                    {
                                                        JSONObject obj = new JSONObject();
                                                        obj.put(__JSON_KEY_DATE__, data.date);
                                                        obj.put(__JSON_KEY_WEIGHT__, dWeight);

                                                        upsert.put(obj);
                                                        break;
                                                    }

                                                    case SyncData.SYNC_TYPE_DELETE:
                                                    {
                                                        JSONObject obj = new JSONObject();
                                                        obj.put(__JSON_KEY_DATE__, data.date);
                                                        obj.put(__JSON_KEY_WEIGHT__, dWeight);

                                                        delete.put(obj);
                                                        break;
                                                    }

                                                    default:
                                                        break;
                                                }
                                            }

                                            weights.put(__JSON_KEY_UPSERT__, upsert);
                                            weights.put(__JSON_KEY_DELETE__, delete);

                                            conn.send(Methods.intToByteArray(RequestId.UPLOAD_WEIGHT.getId()), true);
                                            conn.send(StaticResources.Account.getId(CatDetailActivity.this), true);
                                            conn.send(StaticResources.Account.getAuthenticationToken(CatDetailActivity.this), true);

                                            conn.send(Methods.intToByteArray(cat.getId()), true);
                                            conn.send(weights.toString(0), true);

                                            System.out.println(weights.toString(4));
                                        }
                                        catch (JSONException e)
                                        {
                                            e.printStackTrace();
                                        }
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
                                                    case UPLOAD_WEIGHT_OK:
                                                    {
                                                        syncs.clear();

                                                        Toast.makeText(CatDetailActivity.this, R.string.msg_upload_complete, Toast.LENGTH_SHORT).show();
                                                        break;
                                                    }

                                                    case UPLOAD_WEIGHT_ERROR:
                                                    {
                                                        Toast.makeText(CatDetailActivity.this, R.string.err_occurred, Toast.LENGTH_SHORT).show();
                                                        break;
                                                    }

                                                    default:
                                                        break;
                                                }

                                                setGuiEnabled(true);
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
            }
        });

        // revertImageView
        revertImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                for (SyncData sync : syncs.values())
                {
                    final Date date = Date.fromBigInt(sync.date);

                    final int year = date.getYear();
                    final int month = date.getMonth();
                    final int day = date.getDay();

                    switch (sync.type)
                    {
                        case SyncData.SYNC_TYPE_INSERT:
                        {
                            cat.deleteWeight(year, month, day);
                            break;
                        }

                        case SyncData.SYNC_TYPE_UPDATE:
                        {
                            cat.replaceWeight(year, month, day, sync.oldValue);
                            break;
                        }

                        case SyncData.SYNC_TYPE_DELETE:
                        {
                            cat.setWeight(year, month, day, sync.oldValue);
                            break;
                        }

                        default:
                            break;
                    }
                }

                syncs.clear();

                updateChart(nowFrom, nowTo);
                writeTable();
            }
        });

        // newDateTextView
        newDate = Date.getToday();
        newDateTextView.setText(Methods.DateFormatter.format(Date.getToday()));
        newDateTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DatePickerDialog dialog = new DatePickerDialog(CatDetailActivity.this);
                dialog.getDatePicker().init(
                        newDate.getYear(),
                        newDate.getMonth(),
                        newDate.getDay(),
                        null);

                dialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
                    {
                        newDate = new Date(year, month, dayOfMonth);
                        newDateTextView.setText(Methods.DateFormatter.format(newDate));
                    }
                });
                dialog.show();
            }
        });

        // newWeightEditText
        newWeightEditText.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (actionId == EditorInfo.IME_ACTION_DONE)
                {
                    final int year = newDate.getYear();
                    final int month = newDate.getMonth();
                    final int day = newDate.getDay();

                    final Date date = new Date(year, month, day);

                    final long longDate = date.toLong();
                    final float weight = (int)(Float.parseFloat(newWeightEditText.getText().toString()) * 100) / 100.0F;

                    if (cat.getWeightOrNull(year, month, day) == null)
                    {
                        if (syncs.containsKey(longDate))
                        {
                            SyncData sync = syncs.get(longDate);
                            switch (sync.type)
                            {
                                case SyncData.SYNC_TYPE_INSERT: // won't happen
                                case SyncData.SYNC_TYPE_UPDATE: // won't happen
                                    break;

                                case SyncData.SYNC_TYPE_DELETE:
                                {
                                    if (sync.oldValue == weight)
                                    {
                                        syncs.remove(longDate);
                                    }
                                    else
                                    {
                                        sync.type = SyncData.SYNC_TYPE_UPDATE;
                                        sync.newValue = weight;
                                    }
                                    break;
                                }

                                default:
                                    break;
                            }
                        }
                        else
                        {
                            syncs.put(longDate, new SyncData(SyncData.SYNC_TYPE_INSERT, longDate, weight, -1));
                        }

                        cat.setWeight(year, month, day, weight);
                        newWeightEditText.setText(null);

                        if (nowFrom.toLong() <= longDate && longDate <= nowTo.toLong())
                        {
                            updateChart(nowFrom, nowTo);
                        }
                        writeTable();
                    }
                    else
                    {
                        AlertDialog dupConfirm = new AlertDialog.Builder(CatDetailActivity.this)
                                .setTitle(R.string.title_overwriting)
                                .setMessage(R.string.warn_overwriting)
                                .setCancelable(true)
                                .setPositiveButton(R.string.act_overwrite, new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        if (syncs.containsKey(longDate))
                                        {
                                            SyncData sync = syncs.get(longDate);
                                            switch (sync.type)
                                            {
                                                case SyncData.SYNC_TYPE_INSERT:
                                                    sync.newValue = weight;
                                                    break;

                                                case SyncData.SYNC_TYPE_UPDATE:
                                                    if (sync.oldValue == weight)
                                                    {
                                                        syncs.remove(longDate);
                                                    }
                                                    else
                                                    {
                                                        sync.newValue = weight;
                                                    }
                                                    break;

                                                case SyncData.SYNC_TYPE_DELETE: // won't happen
                                                default:
                                                    break;
                                            }
                                        }
                                        else
                                        {
                                            syncs.put(longDate, new SyncData(SyncData.SYNC_TYPE_UPDATE, longDate, weight, cat.getWeightOrNull(year, month, day)));
                                        }

                                        cat.replaceWeight(year, month, day, weight);
                                        newWeightEditText.setText(null);

                                        if (nowFrom.toLong() <= longDate && longDate <= nowTo.toLong())
                                        {
                                            updateChart(nowFrom, nowTo);
                                        }
                                        writeTable();
                                    }
                                }).setNegativeButton(R.string.act_cancel, null)
                                .create();

                        dupConfirm.show();
                    }
                }

                return false;
            }
        });

        // weightTable
        weightList.setLayoutManager(new LinearLayoutManager(this));

        /*
         * Intent
         */
        Intent intent = getIntent();
        int groupId = intent.getIntExtra(MainActivity.__EXTRA_GROUP_ID__, -1);
        int catId = intent.getIntExtra(MainActivity.__EXTRA_CAT_ID__, -1);
        ArrayList<CatGroup> groups = StaticResources.Entity.getGroups(CatDetailActivity.this);
        CatGroup group = null;
        for (CatGroup g : groups)
        {
            if (g.getId() == groupId)
            {
                group = g;
                break;
            }
        }
        HashMap<CatGroup, List<Cat>> entries = StaticResources.Entity.getEntries(CatDetailActivity.this);
        List<Cat> cats = entries.get(group);
        for (Cat c : cats)
        {
            if (c.getId() == catId)
            {
                cat = c;
                break;
            }
        }

        applyCat(cat);
    }

    @Override
    public void onBackPressed()
    {
        StaticResources.Entity.setUpdateList(this, true);
        super.onBackPressed();
    }

    private void applyCat(Cat cat)
    {
        //
        // Name
        //
        nameTextView.setText(cat.getName());

        //
        // Color
        //
        final Color color = cat.getColor();
        signatureColor = color.toArgb();

        float brightness = Math.max(Math.max(color.red(), color.green()), color.blue());
        float newBrightness = 255 * (brightness - 0.3F);
        if (newBrightness < 0)
        {
            newBrightness = 0;
        }
        int r = (int)(color.red() * newBrightness);
        int g = (int)(color.green() * newBrightness);
        int b = (int)(color.blue() * newBrightness);

        textColor = brightness >= 90
                ? Color.BLACK
                : Color.WHITE;
        darkerColor = 0xFF000000
                | r << 16
                | g << 8
                | b;

        // status bar
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(darkerColor);
        // background
        rootLayout.setBackgroundColor(darkerColor);
        // name, name wrapper
        nameWrapperImageView.setColorFilter(signatureColor, android.graphics.PorterDuff.Mode.SRC_IN);
        nameTextView.setTextColor(textColor);
        // progress bar
        progressBar.setIndeterminateTintList(ColorStateList.valueOf(signatureColor));

        //
        // Attribute Chips
        //
        // gender chip
        Chip genderChip = createChip();
        genderChip.setText(cat.isMale()
                ? R.string.attr_gender_male
                : R.string.attr_gender_female);
        attrChipGroup.addView(genderChip);

        // neutered/spayed chip
        Chip neuteredChip = createChip();
        neuteredChip.setText(cat.getGender().isMale()
                ? R.string.word_neutered
                : R.string.word_spayed);
        attrChipGroup.addView(neuteredChip);

        // species chip
        Chip speciesChip = createChip();
        speciesChip.setText(StaticResources.StringArrays.getSpecies(this)[cat.getSpecies()]);
        attrChipGroup.addView(speciesChip);

        // age chip
        Chip ageChip = createChip();
        StringBuilder ageBuilder = new StringBuilder();
        int[] age = cat.getAge();
        if (age[0] != 0)
        {
            ageBuilder.append(age[0]);
            ageBuilder.append(' ');
            ageBuilder.append(getResources().getString(R.string.unit_old_year));
        }
        else
        {
            ageBuilder.append(age[1]);
            ageBuilder.append(' ');
            ageBuilder.append(getResources().getString(R.string.unit_old_month));
        }
        ageChip.setText(ageBuilder.toString());

        // attribute chips
        if (cat.hasAttributes())
        {
            for (String attr : cat.getAttributes())
            {
                Chip chip = createChip();
                chip.setText(attr);
                attrChipGroup.addView(chip);
            }
        }

        //
        // Weights
        //
        updateChart(nowFrom, nowTo);
        writeTable();
    }

    private Chip createChip()
    {
        Chip chip = new Chip(this);
        chip.setChipBackgroundColor(ColorStateList.valueOf(signatureColor));
        chip.setTextColor(textColor);
        chip.setTextSize(16);
        chip.setClickable(true);
        return chip;
    }

    private void writeTable()
    {
        weightList.setAdapter(new WeightListAdapter(this, cat.getAllWeights(), new WeightListAdapter.ItemEventListener()
        {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(WeightListAdapter.ViewHolder viewHolder)
            {
                newDate = viewHolder.date;

                newWeightEditText.setText(viewHolder.weight.toString());
                newDateTextView.setText(Methods.DateFormatter.format(newDate));
            }

            @Override
            public void onDelete(WeightListAdapter.ViewHolder viewHolder)
            {
                final int year = viewHolder.date.getYear();
                final int month = viewHolder.date.getMonth();
                final int day = viewHolder.date.getDay();

                final long date = viewHolder.date.toLong();
                final float weight = viewHolder.weight;

                if (syncs.containsKey(date))
                {
                    SyncData sync = syncs.get(date);
                    switch (sync.type)
                    {
                        case SyncData.SYNC_TYPE_INSERT:
                        {
                            syncs.remove(date);
                            break;
                        }

                        case SyncData.SYNC_TYPE_UPDATE:
                        {
                            sync.type = SyncData.SYNC_TYPE_DELETE;
                            sync.newValue = -1;
                            break;
                        }

                        case SyncData.SYNC_TYPE_DELETE: // won't happen
                        default:
                            break;
                    }
                }
                else
                {
                    syncs.put(date, new SyncData(SyncData.SYNC_TYPE_DELETE, date, weight));
                }

                cat.deleteWeight(year, month, day);

                if (nowFrom.toLong() <= date && date <= nowTo.toLong())
                {
                    updateChart(nowFrom, nowTo);
                }
                writeTable();
            }
        }));
    }

    private void updateChart(Date from, Date to)
    {
        TreeMap<Date, Float> weightsMap = cat.getWeightsInRange(from, to);
        LineChartData data = createLineChartData(weightsMap,
                signatureColor, darkerColor,
                from, to);
        weightChart.setLineChartData(data);
        weightChart.setViewportCalculationEnabled(false);

        dateFromTextView.setText(Methods.DateFormatter.format(from));
        dateToTextView.setText(Methods.DateFormatter.format(to));
    }

    private LineChartData createLineChartData(TreeMap<Date, Float> map, int color, int pointColor, Date from, Date to)
    {
        LineChartData data = new LineChartData();
        List<Line> lines = new ArrayList<Line>(1);
        List<PointValue> points = new ArrayList<PointValue>(map.size());

        float max = Float.MIN_VALUE;
        for (Map.Entry<Date, Float> entry : map.entrySet())
        {
            points.add(new PointValue(entry.getKey().toLong(), entry.getValue()));

            if (max < entry.getValue())
            {
                max = entry.getValue();
            }
        }

        Line line = new Line(points)
                .setHasLines(true)
                .setHasPoints(points.size() < 2)
                .setFilled(true)
                .setColor(color)
                .setHasLabelsOnlyForSelected(true)
                .setPointColor(pointColor);
        lines.add(line);
        data.setLines(lines);
        data.setBaseValue(Float.NEGATIVE_INFINITY);

        final Viewport v = new Viewport(weightChart.getMaximumViewport());
        v.bottom = 0;
        v.top = max * 1.3F;
        v.left = from.toLong();
        v.right = to.toLong();

        weightChart.setMaximumViewport(v);
        weightChart.setCurrentViewportWithAnimation(v);

        return data;
    }

    private void setGuiEnabled(boolean enabled)
    {
        dateFromTextView.setEnabled(enabled);
        dateToTextView.setEnabled(enabled);

        newDateTextView.setEnabled(enabled);
        newWeightEditText.setEnabled(enabled);

        weightList.setEnabled(enabled);

        progressBar.setVisibility(enabled ? View.GONE : View.VISIBLE);
    }
}
