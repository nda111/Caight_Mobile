package com.example.caight;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

public class AddNewActivity extends AppCompatActivity
{
    private final Activity This = this;

    private static final int __IMAGE_LOADING_REQUEST_CODE__ = 0x0000;

    private ImageView profileImageView = null;
    private EditText nameEditText = null;
    private EditText birthdayEditText = null;
    private ToggleButton genderToggleButton = null;
    private CheckBox isNeuteredCheckBox = null;
    private Spinner speciesSpinner = null;
    private EditText weightEditText = null;
    private Button registerButton = null;

    private Bitmap profileImage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new);
        getSupportActionBar().hide();

        profileImageView = findViewById(R.id.profileImageView);
        nameEditText = findViewById(R.id.nameEditText);
        birthdayEditText = findViewById(R.id.birthdayEditText);
        genderToggleButton = findViewById(R.id.genderToggleButton);
        isNeuteredCheckBox = findViewById(R.id.isNeuteredCheckBox);
        speciesSpinner = findViewById(R.id.speciesSpinner);
        weightEditText = findViewById(R.id.weightEditText);
        registerButton = findViewById(R.id.registerButton);

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
                if (nameEditText.getText().length() == 0 || weightEditText.getText().length() == 0)
                {
                    if (registerButton.isEnabled())
                    {
                        registerButton.startAnimation(disable);
                        registerButton.setEnabled(false);
                    }

                }
                else
                {
                    if (!registerButton.isEnabled())
                    {
                        registerButton.setEnabled(true);
                        registerButton.startAnimation(enable);
                    }

                }
            }
        };

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
        registerButton.setHeight(registerButton.getWidth());
    }

    // TODO: figure out why the image loaded doesn't shown on image view.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (resultCode)
        {
        case __IMAGE_LOADING_REQUEST_CODE__:
            if (resultCode == RESULT_OK)
            {
                try (InputStream in = getContentResolver().openInputStream(data.getData()))
                {
                    profileImage = BitmapFactory.decodeStream(in);
                    profileImageView.setImageBitmap(profileImage);

                    Toast.makeText(this, profileImage.getWidth() + ", " + profileImage.getWidth(), Toast.LENGTH_LONG).show();
                }
                catch (Exception e)
                {
                    Toast.makeText(this, "Exception", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
            else if (resultCode == RESULT_CANCELED)
            {
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show();
            }
            break;

        default:
            new Exception("Unhandled request code").printStackTrace();
            break;
        }
    }

    public void loadProfileImage(View view)
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, __IMAGE_LOADING_REQUEST_CODE__);
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
            return new Cat(
                    profileImage,
                    nameEditText.getText().toString().trim(),
                    StringResources.DateFormatter.parse(birthdayEditText.getText().toString()),
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
}
