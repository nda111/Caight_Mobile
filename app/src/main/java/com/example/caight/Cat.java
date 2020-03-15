package com.example.caight;

import android.graphics.Color;
import android.icu.util.Calendar;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.Period;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
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
    @NonNull
    private int color = -1;
    @NonNull
    private String name = null;
    @NonNull
    private Calendar birthday = null;
    @NonNull
    private Gender gender = null;
    @NonNull
    private int species = -1;
    @NonNull
    private TreeMap<Calendar, Float> weights = null;

    //
    // event Listeners
    //
    private LinkedList<OnCatAttributeChangedListener> attrChangedListener = new LinkedList<OnCatAttributeChangedListener>();

    //
    // Constructor
    //
    private Cat()
    {
    }

    public Cat(@NonNull Color color, @NonNull String name, @NonNull Calendar birthday, @NonNull Gender gender, @NonNull Integer species, @NonNull Float weight)
    {
        setColor(color);
        setName(name);
        setBirthday(birthday);
        setGender(gender);
        setSpecies(species);
        this.weights = new TreeMap<Calendar, Float>();
        setWeight(Calendar.getInstance(), weight);
    }

    public Cat(@NonNull Integer color, @NonNull String name, @NonNull Calendar birthday, @NonNull Gender gender, @NonNull Integer species, @NonNull Float weight)
    {
        setColor(color);
        setName(name);
        setBirthday(birthday);
        setGender(gender);
        setSpecies(species);
        this.weights = new TreeMap<Calendar, Float>();
        setWeight(Calendar.getInstance(), weight);

        //System.out.println(name + ": " + color);
    }

    //
    // private Methods
    //
    private void raiseAttrChangedEvent(int id, Object newValue)
    {
        if (attrChangedListener != null)
        {
            Iterator<OnCatAttributeChangedListener> iterator = attrChangedListener.listIterator();
            while (iterator.hasNext())
            {
                iterator.next().changed(id, newValue);
            }
        }
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
    public void addAttrChangedListener(OnCatAttributeChangedListener l)
    {
        this.attrChangedListener.add(l);
    }

    public boolean removeAttrChangedListener(OnCatAttributeChangedListener l)
    {
        return this.attrChangedListener.remove(l);
    }

    public int getColorInteger()
    {
        return this.color;
    }

    public Color getColor()
    {
        return Color.valueOf(this.color);
    }

    public void setColor(@NonNull Color color)
    {
        this.color = color.toArgb() | /*alpha*/0xFF000000;
        raiseAttrChangedEvent(OnCatAttributeChangedListener.__ID_COLOR__, this.color);
    }

    public void setColor(int color)
    {
        this.color = color | 0xFF000000;
        raiseAttrChangedEvent(OnCatAttributeChangedListener.__ID_COLOR__, this.color);

        System.out.println(name + ": " + this.color);
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
            raiseAttrChangedEvent(OnCatAttributeChangedListener.__ID_NAME__, this.name);
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
            raiseAttrChangedEvent(OnCatAttributeChangedListener.__ID_BIRTHDAY__, this.birthday);
            return true;
        }
    }

    public Period getAge()
    {
        return Period.between(
                LocalDate.of(
                        birthday.get(Calendar.YEAR),
                        birthday.get(Calendar.MONTH),
                        birthday.get(Calendar.DAY_OF_MONTH)),
                LocalDate.now());
    }

    public Gender getGender()
    {
        return gender;
    }

    public void setGender(@NonNull Gender gender)
    {
        this.gender = gender;
        raiseAttrChangedEvent(OnCatAttributeChangedListener.__ID_GENDER__, this.gender);
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
        if (species < 0)
        {
            return false;
        }
        else
        {
            this.species = species;
            raiseAttrChangedEvent(OnCatAttributeChangedListener.__ID_SPECIES__, this.species);
            return true;
        }
    }

    public Map.Entry<Calendar, Float> getLastWeight()
    {
        return weights.lastEntry();
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
            raiseAttrChangedEvent(OnCatAttributeChangedListener.__ID_WEIGHTS__, new Object[]{ filtered, weight });
            return true;
        }
    }

    public boolean setWeight(int year, int month, int day, @NonNull Float weight)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, 0, 0, 0);

        return setWeight(calendar, weight);
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
            raiseAttrChangedEvent(OnCatAttributeChangedListener.__ID_WEIGHTS__, new Object[]{ filtered, weight });
            return true;
        }
    }

    public boolean replaceWeight(int year, int month, int day, @NonNull Float weight)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, 0, 0, 0);

        return replaceWeight(calendar, weight);
    }

    public Collection<Float> getAllWeights()
    {
        return weights.values();
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
