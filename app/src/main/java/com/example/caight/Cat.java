package com.example.caight;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

public final class Cat
{
    //
    // public static final Fields
    //
    // Json keys
    public static final String __JSON_KEY_NAME__ = "name";
    public static final String __JSON_KEY_BIRTHDAY__ = "birthday";
    public static final String __JSON_KEY_GENDER__ = "gender";
    public static final String __JSON_KEY_SPECIES__ = "species";
    public static final String __JSON_KEY_WEIGHTS__ = "weights";
    public static final String __JSON_KEY_WEIGHTS_WHEN__ = "when";
    public static final String __JSON_KEY_WEIGHTS_VALUE__ = "weight";

    //
    // private Fields
    //
    @Nullable
    private Bitmap profile = null;
    @NonNull
    private String name = null;
    @NonNull
    private Date birthday = null;
    @NonNull
    private Gender gender = null;
    @NonNull
    private String species = null;
    @NonNull
    private TreeMap<Date, Float> weights = null;

    //
    // Constructor
    //
    private Cat()
    {
    }
    public Cat(@Nullable Bitmap profile, @NonNull String name, @NonNull Date birthday, @NonNull Gender gender, @NonNull String species, @NonNull Float weight)
    {
        this.profile = profile;
        this.name = name;
        this.birthday = birthday;
        this.gender = gender;
        this.species = species;
        this.weights = new TreeMap<Date, Float>();
    }

    //
    // public Methods
    //
    public JSONObject toJsonObject()
    {
        try
        {
            JSONArray weightArray = new JSONArray();
            for (Map.Entry<Date, Float> entry : new TreeMap<Date, Float>(weights).entrySet())
            {
                JSONObject weight = new JSONObject();
                weight.put(__JSON_KEY_WEIGHTS_WHEN__, entry.getKey().getTime());
                weight.put(__JSON_KEY_WEIGHTS_VALUE__, entry.getValue());

                weightArray.put(weight);
            }

            JSONObject json = new JSONObject();
            json.put(__JSON_KEY_NAME__, name);
            json.put(__JSON_KEY_BIRTHDAY__, birthday.getTime());
            json.put(__JSON_KEY_GENDER__, gender.toString());
            json.put(__JSON_KEY_SPECIES__, species);
            json.put(__JSON_KEY_WEIGHTS__, weightArray);

            return json;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    //
    // Getter / Setter
    //
    public Bitmap getProfile()
    {
        return profile;
    }
    public boolean setProfile(@Nullable Bitmap profile)
    {
        if (profile.getWidth() != profile.getHeight())
        {
            return false;
        }
        else
        {
            this.profile = profile;
            return true;
        }
    }
    public boolean hasProfile()
    {
        return profile != null;
    }

    public String getName()
    {
        return name;
    }
    public boolean setName(@NonNull String name)
    {
        name = name.trim();

        if (name.length() == 0)
        {
            return false;
        }
        else
        {
            this.name = name;
            return true;
        }
    }

    public Date getBirthday()
    {
        return birthday;
    }
    public boolean setBirthday(@NonNull Date birthday)
    {
        if (birthday.getTime() > new Date().getTime())
        {
            return false;
        }
        else
        {
            this.birthday = birthday;
            return true;
        }
    }
    public int getAge()
    {
        return new Date(new Date().getTime() - birthday.getTime()).getYear();
    }

    public Gender getGender()
    {
        return gender;
    }
    public void setGender(@NonNull Gender gender)
    {
        this.gender = gender;
    }
    public boolean isMale()
    {
        return gender.isMale();
    }
    public boolean isFemale()
    {
        return gender.isFemale();
    }
    public boolean isNeuteredOrSpayed()
    {
        return gender.isNeuteredOrSpayed();
    }

    public String getSpecies()
    {
        return species;
    }
    public boolean setSpecies(@NonNull String species)
    {
        species = species.trim();

        if (species.length() == 0)
        {
            return false;
        }
        else
        {
            this.species = species;
            return true;
        }
    }

    public Float getWeightOrNull(Date date)
    {
        if (weights.containsKey(date))
        {
            return weights.get(date);
        }
        else
        {
            return null;
        }
    }
    public boolean setWeight(@NonNull Date date, @NonNull Float weight)
    {
        if (weights.containsKey(date))
        {
            return false;
        }
        else if (weight <= 0)
        {
            return false;
        }
        else
        {
            weights.put(date, weight);
            return true;
        }
    }
    public boolean replaceWeight(@NonNull Date date, @NonNull Float weight)
    {
        if (!weights.containsKey(date))
        {
            return false;
        }
        else if (weight <= 0)
        {
            return false;
        }
        else
        {
            weights.replace(date, weight);
            return true;
        }
    }

    //
    // public static Methods
    //
    public static Cat parseJson(JSONObject json)
    {
        try
        {
            JSONArray weightArray = json.getJSONArray(__JSON_KEY_WEIGHTS__);
            TreeMap<Date, Float> weights = new TreeMap<Date, Float>();
            int length = weightArray.length();
            for (int i = 0; i < length; i++)
            {
                JSONObject weight = weightArray.getJSONObject(i);
                weights.put(
                        new Date(weight.getLong(__JSON_KEY_WEIGHTS_WHEN__)),
                        new Float(weight.getDouble(__JSON_KEY_WEIGHTS_VALUE__))
                );
            }

            Cat cat = new Cat();
            cat.name = json.getString(__JSON_KEY_NAME__);
            cat.birthday = new Date(json.getLong(__JSON_KEY_BIRTHDAY__));
            cat.gender = Gender.parse(json.getString(__JSON_KEY_GENDER__));
            cat.species = json.getString(__JSON_KEY_SPECIES__);
            cat.weights = weights;

            return cat;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
