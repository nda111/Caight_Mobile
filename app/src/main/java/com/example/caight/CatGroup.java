package com.example.caight;

import org.json.JSONException;
import org.json.JSONObject;

public class CatGroup
{
    public static final String __JSON_KEY_GROUP_ID__ = "group_id";
    public static final String __JSON_KEY_GROUP_NAME__ = "group_name";
    public static final String __JSON_KEY_GROUP_OWNER__ = "group_owner";
    public static final String __JSON_KEY_GROUP_LOCKED__ = "group_locked";

    private Integer id = null;
    private String name = null;
    private String owner = null;
    private Boolean locked = null;

    private CatGroup()
    {
    }

    public CatGroup(Integer id, String name, String owner, Boolean locked)
    {
        setId(id);
        setName(name);
        setOwnerName(owner);
        setLocked(locked);
    }

    public JSONObject toJsonObject()
    {
        try
        {
            JSONObject json = new JSONObject();

            json.put(__JSON_KEY_GROUP_ID__, id);
            json.put(__JSON_KEY_GROUP_NAME__, name);
            json.put(__JSON_KEY_GROUP_OWNER__, owner);
            json.put(__JSON_KEY_GROUP_LOCKED__, locked);

            return json;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getOwnerName()
    {
        return owner;
    }

    public void setOwnerName(String owner)
    {
        this.owner = owner;
    }

    public Boolean isLocked()
    {
        return locked;
    }

    public void setLocked(boolean locked)
    {
        this.locked = locked;
    }

    public void lock()
    {
        this.locked = true;
    }

    public void unlock()
    {
        this.locked = false;
    }

    public static CatGroup parseJson(JSONObject json)
    {
        try
        {
            CatGroup group = new CatGroup();
            group.id = json.getInt(__JSON_KEY_GROUP_ID__);
            group.name = json.getString(__JSON_KEY_GROUP_NAME__);
            group.owner = json.getString(__JSON_KEY_GROUP_OWNER__);
            group.locked = json.getBoolean(__JSON_KEY_GROUP_LOCKED__);

            return group;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
