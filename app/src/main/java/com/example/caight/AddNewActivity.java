package com.example.caight;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

public class AddNewActivity extends AppCompatActivity implements ColorPickerDialogListener
{
    private final Activity This = this;

    private static final int __IMAGE_LOADING_REQUEST_CODE__ = 0x0000;

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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new);
        getSupportActionBar().hide();

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

        //
        // Listeners
        //
        TextWatcher textWatcher = new TextWatcher()
        {
            private Animation enable = AnimationUtils.loadAnimation(This, R.anim.register_btn_enable);
            private Animation disable = AnimationUtils.loadAnimation(This, R.anim.register_btn_disable);

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
                nameValidCheckBox.setChecked(nameEditText.getText().length() != 0);
                weightValidCheckBox.setChecked(weightEditText.getText().length() != 0);

                if (weightValidCheckBox.isChecked() && nameValidCheckBox.isChecked())
                {
                    if (!registerButton.isEnabled())
                    {
                        registerButton.setEnabled(true);
                        registerButton.startAnimation(enable);
                    }

                }
                else
                {
                    if (registerButton.isEnabled())
                    {
                        registerButton.startAnimation(disable);
                        registerButton.setEnabled(false);
                    }
                }
            }
        };

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
                if (event.getAction() == 1) // Finger up
                {
                    picker.show(getSupportFragmentManager(), "ABC");
                }
                return false;
            }
        };

        //
        // Initializing Components
        //
        // colorViewer
        colorViewer.setOnTouchListener(colorPickerTrigger);

        // rgbTextView
        rgbTextView.setOnTouchListener(colorPickerTrigger);

        // nameEditText
        nameEditText.setHint(StringResources.NameExamples[(int)(Math.random() * StringResources.NameExamples.length)]);
        nameEditText.addTextChangedListener(textWatcher);

        // birthdayEditText
        String nowString = StringResources.DateFormatter.format(new Date());
        birthdayEditText.setHint(nowString);

        // speciesSpinner
        ArrayAdapter speciesAdapter = ArrayAdapter.createFromResource(this, R.array.species, android.R.layout.simple_spinner_item);
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

        // weightEditText
        weightEditText.addTextChangedListener(textWatcher);

        // registerButton
        registerButton.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == 1) // Touch up
                {

                }

                return true;
            }
        });
    }

    @Override
    public void onColorSelected(int dialogId, int color)
    {
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
                String nowString = StringResources.DateFormatter.format(new Date(year - 1900, month, dayOfMonth));
                birthdayEditText.setText(nowString);
            }
        };

        Calendar now = Calendar.getInstance();
        new DatePickerDialog(this, DatePicker,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)).show();
    }

    public Cat getCatInfo()
    {
        try
        {
            String dateString = birthdayEditText.getText().toString();
            if (dateString.isEmpty())
            {
                dateString = birthdayEditText.getHint().toString();
            }

            return new Cat(
                    ((ColorDrawable)colorViewer.getBackground()).getColor(),
                    nameEditText.getText().toString().trim(),
                    StringResources.DateFormatter.parse(dateString),
                    Gender.evaluate(genderToggleButton.isChecked(), isNeuteredCheckBox.isChecked()),
                    speciesSpinner.getSelectedItem().toString(),
                    Float.parseFloat(weightEditText.getText().toString())
            );
        }
        catch (ParseException e)
        {
            e.printStackTrace();
            return null;
        }
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
