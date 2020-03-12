package com.example.caight;

import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Map;
import java.util.TreeMap;

public final class Cat
{
    //
    // public static final Fields
    //
    // Json keys
    public static final String __JSON_KEY_COLOR__ = "color";
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
    private Integer color = null;
    @NonNull
    private String name = null;
    @NonNull
    private Calendar birthday = null;
    @NonNull
    private Gender gender = null;
    @NonNull
    private Integer species = null;
    @NonNull
    private TreeMap<Calendar, Float> weights = null;

    //
    // Constructor
    //
    private Cat()
    {
    }

    public Cat(@Nullable Color color, @NonNull String name, @NonNull Calendar birthday, @NonNull Gender gender, @NonNull Integer species, @NonNull Float weight)
    {
        setColor(color);
        setName(name);
        setBirthday(birthday);
        setGender(gender);
        setSpecies(species);
        this.weights = new TreeMap<Calendar, Float>();
        setWeight(Calendar.getInstance(), weight);
    }

    public Cat(@Nullable Integer color, @NonNull String name, @NonNull Calendar birthday, @NonNull Gender gender, @NonNull Integer species, @NonNull Float weight)
    {
        this.color = color;
        setName(name);
        setBirthday(birthday);
        setGender(gender);
        setSpecies(species);
        this.weights = new TreeMap<Calendar, Float>();
        setWeight(Calendar.getInstance(), weight);
    }

    //
    // public Methods
    //
    public JSONObject toJsonObject()
    {
        try
        {
            JSONArray weightArray = new JSONArray();
            for (Map.Entry<Calendar, Float> entry : new TreeMap<Calendar, Float>(weights).entrySet())
            {
                JSONObject weight = new JSONObject();
                weight.put(__JSON_KEY_WEIGHTS_WHEN__, entry.getKey().getTimeInMillis());
                weight.put(__JSON_KEY_WEIGHTS_VALUE__, entry.getValue());

                weightArray.put(weight);
            }

            JSONObject json = new JSONObject();
            json.put(__JSON_KEY_COLOR__, color);
            json.put(__JSON_KEY_NAME__, name);
            json.put(__JSON_KEY_BIRTHDAY__, birthday.getTimeInMillis());
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
    public int getColorInteger()
    {
        return color;
    }

    public Color getColor()
    {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;

        return Color.valueOf(r, g, b);
    }

    public void setColor(@NonNull Color color)
    {
        int r = (int)color.red();
        int g = (int)color.green();
        int b = (int)color.blue();

        this.color = /*alpha*/0xFF000000 & (r << 16) & (g << 8) & b;
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

    public Calendar getBirthday()
    {
        return birthday;
    }

    public boolean setBirthday(@NonNull Calendar birthday)
    {
        if (birthday.getTimeInMillis() > Calendar.getInstance().getTimeInMillis())
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
        long ageInMs = Calendar.getInstance().getTimeInMillis() - birthday.getTimeInMillis();
        Calendar age = Calendar.getInstance();
        age.setTimeInMillis(ageInMs);

        return age.get(Calendar.YEAR);
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

    public String getSpeciesName()
    {
        return StringResources.Species[species];
    }

    public Integer getSpecies()
    {
        return species;
    }

    public boolean setSpecies(@NonNull Integer species)
    {
        if (species < 0 || StringResources.Species.length <= species)
        {
            return false;
        }
        else
        {
            this.species = species;

            return true;
        }
    }

    public Float getWeightOrNull(int year, int month, int day)
    {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);

        return weights.get(cal);
    }

    public Float getWeightOrNull(Calendar date)
    {
        return weights.get(getFilteredCalendar(date));
    }

    public boolean setWeight(@NonNull Calendar date, @NonNull Float weight)
    {
        Calendar filtered = getFilteredCalendar(date);

        if (weights.containsKey(filtered))
        {
            return false;
        }
        else if (weight <= 0)
        {
            return false;
        }
        else
        {
            weights.put(filtered, weight);
            return true;
        }
    }

    public boolean replaceWeight(@NonNull Calendar date, @NonNull Float weight)
    {
        Calendar filtered = getFilteredCalendar(date);

        if (!weights.containsKey(filtered))
        {
            return false;
        }
        else if (weight <= 0)
        {
            return false;
        }
        else
        {
            weights.replace(filtered, weight);
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
            TreeMap<Calendar, Float> weights = new TreeMap<Calendar, Float>();
            int length = weightArray.length();
            for (int i = 0; i < length; i++)
            {
                JSONObject weight = weightArray.getJSONObject(i);

                weights.put(
                        getCalendarInMillis(weight.getLong(__JSON_KEY_WEIGHTS_WHEN__)),
                        new Float(weight.getDouble(__JSON_KEY_WEIGHTS_VALUE__))
                );
            }

            Cat cat = new Cat();
            cat.color = json.getInt(__JSON_KEY_COLOR__);
            cat.name = json.getString(__JSON_KEY_NAME__);
            cat.birthday = getCalendarInMillis(json.getLong(__JSON_KEY_BIRTHDAY__));
            cat.gender = Gender.parse(json.getString(__JSON_KEY_GENDER__));
            cat.species = json.getInt(__JSON_KEY_SPECIES__);
            cat.weights = weights;

            return cat;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    private static Calendar getFilteredCalendar(Calendar calendar)
    {
        Calendar cal = calendar.getInstance();
        cal.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));

        return cal;
    }

    private static Calendar getCalendarInMillis(long ms)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(ms);

        return cal;
    }

    public static Integer getSpeciesIndexOrNull(String species)
    {
        for (int i = 0; i < StringResources.Species.length; i++)
        {
            if (StringResources.Species.equals(species))
            {
                return i;
            }
        }

        return null;
    }
}
