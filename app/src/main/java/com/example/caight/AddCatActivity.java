package com.example.caight;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.icu.util.Calendar;

import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;
import com.neovisionaries.ws.client.WebSocket;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;

public class AddCatActivity extends AppCompatActivity implements ColorPickerDialogListener
{
    private final Activity This = this;

    private ConstraintLayout wrapperLayout = null;
    private EditText pwEditText = null;
    private CheckBox pwValidCheckBox = null;
    private Spinner groupSpinner = null;
    private ConstraintLayout colorViewer = null;
    private TextView rgbTextView = null;
    private EditText nameEditText = null;
    private CheckBox nameValidCheckBox = null;
    private EditText birthdayEditText = null;
    private ToggleButton genderToggleButton = null;
    private CheckBox isNeuteredCheckBox = null;
    private Spinner speciesSpinner = null;
    private EditText weightEditText = null;
    private CheckBox weightValidCheckBox = null;
    private Button registerButton = null;
    private ProgressBar progressBar = null;

    private CatGroup selectedGroup = null;
    private int selectedColor = 0;
    private long selectedBirthday = 0;
    private int selectedSpecies = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cat);
        getSupportActionBar().hide();

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
         * Initialize GUI Components
         */
        wrapperLayout = findViewById(R.id.wrapperLayout);
        pwEditText = findViewById(R.id.pwEditText);
        pwValidCheckBox = findViewById(R.id.pwValidCheckBox);
        groupSpinner = findViewById(R.id.groupSpinner);
        colorViewer = findViewById(R.id.colorViewer);
        rgbTextView = findViewById(R.id.rgbTextView);
        nameEditText = findViewById(R.id.nameEditText);
        nameValidCheckBox = findViewById(R.id.nameValidCheckBox);
        birthdayEditText = findViewById(R.id.birthdayEditText);
        genderToggleButton = findViewById(R.id.genderToggleButton);
        isNeuteredCheckBox = findViewById(R.id.isNeuteredCheckBox);
        speciesSpinner = findViewById(R.id.speciesSpinner);
        weightEditText = findViewById(R.id.weightEditText);
        weightValidCheckBox = findViewById(R.id.weightValidCheckBox);
        registerButton = findViewById(R.id.registerButton);
        progressBar = findViewById(R.id.progressBar);

        // pwEditText
        pwEditText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
            }

            @Override
            public void afterTextChanged(Editable s)
            {
                pwValidCheckBox.setChecked(s.length() > 0);
            }
        });

        // groupSpinner
        GroupSpinnerAdapter adapter = new GroupSpinnerAdapter(this, R.id.groupTextView, StaticResources.groups);
        groupSpinner.setAdapter(adapter);
        groupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                selectedGroup = StaticResources.groups.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
            }
        });

        // colorViewer
        colorViewer.setOnTouchListener(colorPickerTrigger);

        // rgbTextView
        rgbTextView.setOnTouchListener(colorPickerTrigger);

        // nameEditText
        nameEditText.setHint(StringResources.NameExamples[(int)(Math.random() * StringResources.NameExamples.length)]);
        nameEditText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
            }

            @Override
            public void afterTextChanged(Editable s)
            {
                nameValidCheckBox.setChecked(s.length() > 0);
            }
        });

        // birthdayEditText
        Calendar today = Calendar.getInstance();
        today.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));
        birthdayEditText.setHint(StringResources.DateFormatter.format(today));
        selectedBirthday = today.getTimeInMillis();

        // speciesSpinner
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
                for (int idx = 0; idx < StringResources.Species.length; idx++)
                {
                    if (item.equals(StringResources.Species[idx]))
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

        // weightEditText
        weightEditText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
            }

            @Override
            public void afterTextChanged(Editable s)
            {
                weightValidCheckBox.setChecked(s.length() > 0);
            }
        });

        // registerButton
        registerButton.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == 1 && pwValidCheckBox.isChecked() && nameValidCheckBox.isChecked() && weightValidCheckBox.isChecked())
                {
                    wrapperLayout.setEnabled(false);
                    progressBar.setVisibility(View.VISIBLE);

                    try
                    {
                        WebSocketConnection conn = new WebSocketConnection(StringResources.__WS_ADDRESS__)
                                .setRequestAdapter(new WebSocketConnection.RequestAdapter()
                                {
                                    ResponseId response;

                                    @Override
                                    public void onRequest(WebSocketConnection conn)
                                    {
                                        Calendar today = Calendar.getInstance();
                                        today.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));

                                        int group = selectedGroup.getId();
                                        String pw = pwEditText.getText().toString();
                                        int color = selectedColor;
                                        String name = nameEditText.getText().toString();
                                        long birthday = selectedBirthday;
                                        short gender = (short)Gender.evaluate(!genderToggleButton.isChecked(), isNeuteredCheckBox.isChecked()).getValue();
                                        int species = selectedSpecies;
                                        long todayMillis = today.getTimeInMillis();
                                        float weight = Float.parseFloat(weightEditText.getText().toString());

                                        StringBuilder builder = new StringBuilder();
                                        builder.append(group); builder.append('\0');
                                        builder.append(pw); builder.append('\0');
                                        builder.append(color); builder.append('\0');
                                        builder.append(name); builder.append('\0');
                                        builder.append(birthday); builder.append('\0');
                                        builder.append(gender); builder.append('\0');
                                        builder.append(species); builder.append('\0');
                                        builder.append(todayMillis); builder.append('\0');
                                        builder.append(weight);

                                        conn.send(StaticMethods.intToByteArray(RequestId.NEW_CAT.getId()), true);

                                        conn.send(StaticResources.accountId, true);
                                        conn.send(StaticResources.authToken, true);
                                        conn.send(builder.toString(), true);
                                    }

                                    @Override
                                    public void onResponse(WebSocketConnection conn, WebSocketConnection.Message message)
                                    {
                                        response = ResponseId.fromId(StaticMethods.byteArrayToInt(message.getBinary()));
                                        conn.close();
                                    }

                                    @Override
                                    public void onClosed()
                                    {
                                        switch (response)
                                        {
                                            case ADD_ENTITY_OK:
                                            {
                                                StaticResources.updateEntityList = true;
                                                break;
                                            }

                                            case ADD_ENTITY_NO:
                                            {
                                                Toast.makeText(getApplication(), R.string.errmsg_other_device_logged_in, Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(getApplicationContext(), EntryActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                break;
                                            }

                                            case ADD_ENTITY_NOT_PW:
                                            {
                                                Toast.makeText(getApplication(), R.string.errmsg_wrong_pw, Toast.LENGTH_SHORT).show();
                                                break;
                                            }

                                            case ADD_ENTITY_ERROR:
                                            {
                                                Toast.makeText(getApplication(), R.string.errmsg_error, Toast.LENGTH_SHORT).show();
                                                break;
                                            }

                                            default:
                                                break;
                                        }

                                        if (response != ResponseId.ADD_ENTITY_NOT_PW)
                                        {
                                            runOnUiThread(new Runnable()
                                            {
                                                @Override
                                                public void run()
                                                {
                                                    finish();
                                                }
                                            });
                                        }
                                        else
                                        {
                                            runOnUiThread(new Runnable()
                                            {
                                                @Override
                                                public void run()
                                                {
                                                    wrapperLayout.setEnabled(true);
                                                    progressBar.setVisibility(View.GONE);
                                                }
                                            });
                                        }
                                    }
                                }).connect();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
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

        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;

        StringBuilder hex = new StringBuilder("#");
        hex.append(padLeft(Integer.toHexString(r).toUpperCase(), '0', 2));
        hex.append(padLeft(Integer.toHexString(g).toUpperCase(), '0', 2));
        hex.append(padLeft(Integer.toHexString(b).toUpperCase(), '0', 2));

        rgbTextView.setText(hex);
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
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
            {
                Calendar cal = Calendar.getInstance();
                cal.set(year, month, dayOfMonth);

                selectedBirthday = cal.getTimeInMillis();

                String nowString = StringResources.DateFormatter.format(cal);
                birthdayEditText.setText(nowString);
            }
        };

        Calendar now = Calendar.getInstance();
        new DatePickerDialog(this, DatePicker,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)).show();
    }

    private String padLeft(String origin, char pad, int totalLength)
    {
        StringBuilder builder = new StringBuilder();
        for (int length = origin.length(); length < totalLength; length++)
        {
            builder.append(pad);
        }
        builder.append(origin);
        return builder.toString();
    }
}
