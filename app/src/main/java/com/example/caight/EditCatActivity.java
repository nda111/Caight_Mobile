package com.example.caight;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class EditCatActivity extends AppCompatActivity implements ColorPickerDialogListener
{
    private static final String __JSON_KEY_ID__ = "id";
    private static final String __JSON_KEY_COLOR__ = "color";
    private static final String __JSON_KEY_NAME__ = "name";
    private static final String __JSON_KEY_BIRTHDAY__ = "birthday";
    private static final String __JSON_KEY_GENDER__ = "gender";
    private static final String __JSON_KEY_SPECIES__ = "species";

    private Cat cat = null;

    private ConstraintLayout colorViewer = null;
    private TextView rgbTextView = null;
    private EditText nameEditText = null;
    private EditText birthdayEditText = null;
    private RadioButton maleRadioButton = null;
    private RadioButton femaleRadioButton = null;
    private CheckBox isNeuteredCheckBox = null;
    private Spinner speciesSpinner = null;
    private Button saveButton = null;
    private ProgressBar progressBar = null;

    private int selectedSpecies = -1;
    private int selectedColor = -1;
    private long selectedBirthday;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_cat);
        Resources resources = getResources();
        Resources.Theme theme = getTheme();

        /*
         * Listeners
         */
        View.OnTouchListener colorPickerTrigger = new View.OnTouchListener()
        {
            private ColorPickerDialog picker = ColorPickerDialog
                    .newBuilder()
                    .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                    .setColor(Color.WHITE)
                    .create();

            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                picker.show(getSupportFragmentManager(), null);
                return false;
            }
        };

        /*
         * Intent
         */
        Intent intent = getIntent();
        int groupId = intent.getIntExtra(MainActivity.__EXTRA_GROUP_ID__, -1);
        int catId = intent.getIntExtra(MainActivity.__EXTRA_CAT_ID__, -1);
        if (groupId == -1 || catId == -1)
        {
            Toast.makeText(getApplicationContext(), R.string.err_occurred, Toast.LENGTH_SHORT).show();
            finish();
        }
        for (CatGroup group : StaticResources.Entity.getGroups(EditCatActivity.this))
        {
            if (group.getId() == groupId)
            {
                HashMap<CatGroup, List<Cat>> entries = StaticResources.Entity.getEntries(EditCatActivity.this);
                for (Cat cat : entries.get(group))
                {
                    if (cat.getId() == catId)
                    {
                        this.cat = cat;
                        break;
                    }
                }
                break;
            }
        }
        if (cat == null)
        {
            Toast.makeText(getApplicationContext(), R.string.err_occurred, Toast.LENGTH_SHORT).show();
            finish();
        }

        /*
         * Action Bar
         */
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(cat.getName());
        actionBar.setBackgroundDrawable(new ColorDrawable(ResourcesCompat.getColor(resources, R.color.colorPrimaryDark, theme)));

        /*
         * Initialize GUI Components
         */
        colorViewer = findViewById(R.id.colorViewer);
        rgbTextView = findViewById(R.id.rgbTextView);
        nameEditText = findViewById(R.id.nameEditText);
        birthdayEditText = findViewById(R.id.birthdayEditText);
        maleRadioButton = findViewById(R.id.maleRadioButton);
        femaleRadioButton = findViewById(R.id.femmaleRadioButton);
        isNeuteredCheckBox = findViewById(R.id.isNeuteredCheckBox);
        speciesSpinner = findViewById(R.id.speciesSpinner);
        saveButton = findViewById(R.id.saveButton);
        progressBar = findViewById(R.id.progressBar);

        // colorViewer
        selectedColor = cat.getColorInteger();
        colorViewer.setBackgroundColor(selectedColor);
        colorViewer.setOnTouchListener(colorPickerTrigger);

        // rgbTextView
        rgbTextView.setText(colorToHexString(cat.getColor()));
        rgbTextView.setOnTouchListener(colorPickerTrigger);

        // nameEditText
        nameEditText.setText(cat.getName());

        // birthdayEditText
        birthdayEditText.setText(Methods.DateFormatter.format(cat.getBirthday()));
        selectedBirthday = cat.getBirthday().getTimeInMillis();

        // maleRadioButton, femaleRadioButton, neuteredCheckBox
        (cat.isMale() ? maleRadioButton : femaleRadioButton).setChecked(true);
        isNeuteredCheckBox.setChecked(cat.isNeuteredOrSpayed());

        // speciesSpinner
        final String[] species = StaticResources.StringArrays.getSpecies(EditCatActivity.this);
        final String[] sortedSpecies = StaticResources.StringArrays.getSortedSpecies(EditCatActivity.this);
        final ArrayAdapter speciesAdapter = ArrayAdapter.createFromResource(this, R.array.species, android.R.layout.simple_spinner_item);
        speciesAdapter.sort(new Comparator()
        {
            @Override
            public int compare(Object o1, Object o2)
            {
                return ((String)o1).compareTo((String)o2);
            }
        });
        speciesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        speciesSpinner.setAdapter(speciesAdapter);
        speciesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String item = (String)speciesAdapter.getItem(position);
                for (int idx = 0; idx < species.length; idx++)
                {
                    if (item.equals(species[idx]))
                    {
                        selectedSpecies = idx;
                        return;
                    }
                }

                selectedSpecies = -1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
            }
        });
        selectedSpecies = Arrays.binarySearch(sortedSpecies, species[cat.getSpecies()]);
        speciesSpinner.setSelection(selectedSpecies);

        // saveButton
        saveButton.setOnTouchListener(new View.OnTouchListener()
        {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == 1)
                {
                    boolean changed = false;
                    final JSONObject json = new JSONObject();

                    int color = selectedColor | 0xFF000000;
                    String name = nameEditText.getText().toString().trim();
                    long birthday = selectedBirthday;
                    Gender gender = Gender.evaluate(maleRadioButton.isChecked(), isNeuteredCheckBox.isChecked());
                    int species = selectedSpecies;

                    try
                    {
                        json.put(__JSON_KEY_ID__, cat.getId());

                        if (cat.getColorInteger() != color)
                        {
                            changed = true;
                            json.put(__JSON_KEY_COLOR__, color);
                        }
                        if (!cat.getName().equals(name))
                        {
                            changed = true;
                            json.put(__JSON_KEY_NAME__, name);
                        }
                        if (cat.getBirthday().getTimeInMillis() != birthday)
                        {
                            changed = true;
                            json.put(__JSON_KEY_BIRTHDAY__, birthday);
                        }
                        if (cat.getGender() != gender)
                        {
                            changed = true;
                            json.put(__JSON_KEY_GENDER__, gender.getValue());
                        }
                        if (cat.getSpecies() != species)
                        {
                            changed = true;
                            json.put(__JSON_KEY_SPECIES__, species);
                        }
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }

                    if (changed)
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
                                            conn.send(Methods.intToByteArray(RequestId.EDIT_CAT.getId()), true);
                                            conn.send(StaticResources.Account.getId(EditCatActivity.this), true);
                                            conn.send(StaticResources.Account.getAuthenticationToken(EditCatActivity.this), true);

                                            conn.send(json.toString(), true);
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
                                                        case EDIT_CAT_OK:
                                                        {
                                                            StaticResources.Entity.setUpdateList(EditCatActivity.this, true);
                                                            finish();
                                                            break;
                                                        }

                                                        case EDIT_CAT_ERROR:
                                                        {
                                                            Toast.makeText(EditCatActivity.this, R.string.err_occurred, Toast.LENGTH_LONG).show();
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
                    else
                    {
                        finish();
                    }
                }

                return false;
            }
        });
    }

    @Override
    public void onColorSelected(int dialogId, int color)
    {
        selectedColor = color;

        rgbTextView.setText(colorToHexString(Color.valueOf(color)));
        colorViewer.setBackgroundColor(color);
    }

    @Override
    public void onDialogDismissed(int dialogId)
    {
    }

    public void setBirthDay(View view)
    {
        DatePickerDialog.OnDateSetListener DatePicker = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth)
            {
                Calendar cal = Calendar.getInstance();
                cal.set(year, month, dayOfMonth);

                selectedBirthday = cal.getTimeInMillis();

                String nowString = Methods.DateFormatter.format(cal);
                birthdayEditText.setText(nowString);
            }
        };

        Calendar now = Calendar.getInstance();
        new DatePickerDialog(this, DatePicker,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)).show();
    }

    private String colorToHexString(Color color)
    {
        int argb = color.toArgb();
        String hex = Integer.toString(argb, 16).substring(2).toUpperCase();
        while (hex.length() < 6)
        {
            hex = '0' + hex;
        }
        return '#' + hex;
    }

    private void setGuiEnabled(boolean enabled)
    {
        colorViewer.setEnabled(enabled);
        rgbTextView.setEnabled(enabled);
        nameEditText.setEnabled(enabled);
        birthdayEditText.setEnabled(enabled);
        maleRadioButton.setEnabled(enabled);
        femaleRadioButton.setEnabled(enabled);
        isNeuteredCheckBox.setEnabled(enabled);
        speciesSpinner.setEnabled(enabled);
        saveButton.setEnabled(enabled);
        progressBar.setVisibility(enabled ? View.GONE : View.VISIBLE);
    }
}
