package com.example.caight;

import android.graphics.Color;

import android.util.JsonToken;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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
    public static final String __JSON_KEY_ID__ = "id";
    public static final String __JSON_KEY_COLOR__ = "color";
    public static final String __JSON_KEY_NAME__ = "name";
    public static final String __JSON_KEY_BIRTHDAY__ = "birthday";
    public static final String __JSON_KEY_GENDER__ = "gender";
    public static final String __JSON_KEY_SPECIES__ = "species";
    public static final String __JSON_KEY_WEIGHTS__ = "weights";
    public static final String __JSON_KEY_WEIGHTS_WHEN__ = "when";
    public static final String __JSON_KEY_WEIGHTS_VALUE__ = "weight";
    public static final String __JSON_KEY_ATTRIBUTES__ = "attributes";

    //
    // private Fields
    //
    @NonNull
    private int id = -1;
    @NonNull
    private int color = -1;
    @NonNull
    private String name = null;
    @NonNull
    private Date birthday = null;
    @NonNull
    private Gender gender = null;
    @NonNull
    private int species = -1;
    @NonNull
    private TreeMap<Date, Float> weights = null;
    private String[] attributes = null;

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

    public Cat(@NonNull Color color, @NonNull String name, @NonNull Date birthday, @NonNull Gender gender, @NonNull Integer species, @NonNull Float weight)
    {
        setColor(color);
        setName(name);
        setBirthday(birthday);
        setGender(gender);
        setSpecies(species);
        this.weights = new TreeMap<Date, Float>();
        setWeight(Date.getToday(), weight);
    }

    public Cat(@NonNull Integer color, @NonNull String name, @NonNull Date birthday, @NonNull Gender gender, @NonNull Integer species, @NonNull Float weight)
    {
        setColor(color);
        setName(name);
        setBirthday(birthday);
        setGender(gender);
        setSpecies(species);
        this.weights = new TreeMap<Date, Float>();
        setWeight(Date.getToday(), weight);
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
            for (Map.Entry<Date, Float> entry : new TreeMap<Date, Float>(weights).entrySet())
            {
                JSONObject weight = new JSONObject();
                weight.put(__JSON_KEY_WEIGHTS_WHEN__, entry.getKey().toLong());
                weight.put(__JSON_KEY_WEIGHTS_VALUE__, entry.getValue());

                weightArray.put(weight);
            }

            JSONArray attributes = new JSONArray();
            if (this.attributes != null)
            {
                for (String attr : this.attributes)
                {
                    attributes.put(attr);
                }
            }

            JSONObject json = new JSONObject();
            json.put(__JSON_KEY_ID__, id);
            json.put(__JSON_KEY_COLOR__, color);
            json.put(__JSON_KEY_NAME__, name);
            json.put(__JSON_KEY_BIRTHDAY__, birthday.toLong());
            json.put(__JSON_KEY_GENDER__, gender.getValue());
            json.put(__JSON_KEY_SPECIES__, species);
            json.put(__JSON_KEY_WEIGHTS__, weightArray);
            json.put(__JSON_KEY_ATTRIBUTES__, attributes);

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

    public int getId()
    {
        return id;
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

    public Date getBirthday()
    {
        return birthday;
    }

    public boolean setBirthday(@NonNull Date birthday)
    {
        if (birthday.toLong() > Date.getToday().toLong())
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

    public int[] getAge()
    {
        Date age = Date.getPeriod(birthday, Date.getToday());

        return new int[] { age.getYear() - 1970, age.getMonth() - 1 };
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

    public Map.Entry<Date, Float> getLastWeight()
    {
        return weights.lastEntry();
    }

    public Float getWeightOrNull(int year, int month, int day)
    {
        Date cal = new Date(year, month, day);

        for (Map.Entry<Date, Float> entry : weights.entrySet())
        {
            Date date = entry.getKey();
            if (date.equals(new Date(year, month, day)))
            {
                return entry.getValue();
            }
        }

        return null;
    }

    public Float getWeightOrNull(Date date)
    {
        return weights.get(date);
    }

    public void setWeights(TreeMap<Date, Float> weights)
    {
        this.weights = weights;
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
            raiseAttrChangedEvent(OnCatAttributeChangedListener.__ID_WEIGHTS__, new Object[] { date, weight });
            return true;
        }
    }

    public boolean setWeight(int year, int month, int day, @NonNull Float weight)
    {
        Date date = new Date(year, month, day);
        return setWeight(date, weight);
    }

    public boolean deleteWeight(int year, int month, int day)
    {
        for (Map.Entry<Date, Float> entry : weights.entrySet())
        {
            Date date = entry.getKey();
            if (date.equals(new Date(year, month, day)))
            {
                weights.remove(date);
                return true;
            }
        }

        return false;
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
            raiseAttrChangedEvent(OnCatAttributeChangedListener.__ID_WEIGHTS__, new Object[] { date, weight });
            return true;
        }
    }

    public boolean replaceWeight(int year, int month, int day, @NonNull Float weight)
    {
        Date key = null;
        for (Map.Entry<Date, Float> entry : weights.entrySet())
        {
            Date date = entry.getKey();
            if (date.equals(new Date(year, month, day)))
            {
                key = entry.getKey();
            }
        }

        if (key != null)
        {
            weights.replace(key, weight);
            return true;
        }
        else
        {
            return false;
        }
    }

    public TreeMap<Date, Float> getWeightsInRange(Date from, Date to)
    {
        long f = from.toLong();
        long t = from.toLong();

        TreeMap<Date, Float> result = new TreeMap<Date, Float>();
        Iterator<Map.Entry<Date, Float>> iterator = weights.entrySet().iterator();
        while (iterator.hasNext())
        {
            Map.Entry<Date, Float> entry = iterator.next();
            if (entry.getKey().toLong() >= f)
            {
                result.put(entry.getKey(), entry.getValue());
                break;
            }
        }
        while (iterator.hasNext())
        {
            Map.Entry<Date, Float> entry = iterator.next();
            if (entry.getKey().toLong() <= t)
            {
                result.put(entry.getKey(), entry.getValue());
            }
            else
            {
                break;
            }
        }

        return result;
    }

    public TreeMap<Date, Float> getAllWeights()
    {
        return weights;
    }

    public boolean hasAttributes()
    {
        return attributes != null && attributes.length != 0;
    }

    public String[] getAttributes()
    {
        return attributes;
    }

    public void setAttributes(String[] attributes)
    {
        this.attributes = attributes;
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
                        Date.fromBigInt(weight.getLong(__JSON_KEY_WEIGHTS_WHEN__)),
                        new Float(weight.getDouble(__JSON_KEY_WEIGHTS_VALUE__))
                );
            }

            JSONArray attributes = json.getJSONArray(__JSON_KEY_ATTRIBUTES__);
            String[] attrArray = new String[attributes.length()];
            for (int i = 0; i < attrArray.length; i++)
            {
                attrArray[i] = attributes.getString(i);
            }

            Cat cat = new Cat();
            cat.id = json.getInt(__JSON_KEY_ID__);
            cat.color = json.getInt(__JSON_KEY_COLOR__);
            cat.name = json.getString(__JSON_KEY_NAME__);
            cat.birthday = Date.fromBigInt(json.getLong(__JSON_KEY_BIRTHDAY__));
            cat.gender = Gender.fromValue(json.getInt(__JSON_KEY_GENDER__));
            cat.species = json.getInt(__JSON_KEY_SPECIES__);
            cat.weights = weights;
            cat.attributes = attrArray;

            return cat;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
