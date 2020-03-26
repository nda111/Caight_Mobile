package com.example.caight;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

class StaticResources
{
    static final String __WS_ADDRESS__ = "wss://caight.herokuapp.com/ws";

    static class AutoLogin
    {
        private static SharedPreferences preferences = null;

        private static final String KeyPreferences = "__KEY_PREFERENCE_AUTO_LOGIN__";
        private static final String KeyDoAutoLogin = "__KEY_DO_AUTO_LOGIN__";
        private static final String KeyEmail = "__KEY_EMAIL__";
        private static final String KeyPassword = "__KEY_PASSWORD__";

        private static void getPreferencesIfNull(Context context)
        {
            if (preferences == null)
            {
                preferences = context.getSharedPreferences(KeyPreferences, Context.MODE_PRIVATE);
            }
        }

        static void clear(Context context)
        {
            getPreferencesIfNull(context);
            preferences.edit().clear().apply();
        }

        static void set(Context context, boolean doAutoLogin, String email, String password)
        {
            getPreferencesIfNull(context);

            if (!doAutoLogin)
            {
                email = password = null;
            }

            preferences.edit()
                    .putBoolean(KeyDoAutoLogin, doAutoLogin)
                    .putString(KeyEmail, email)
                    .putString(KeyPassword, password)
                    .apply();
        }

        static boolean getDoAutoLogin(Context context)
        {
            getPreferencesIfNull(context);
            return preferences.getBoolean(KeyDoAutoLogin, false);
        }

        static String getEmail(Context context)
        {
            getPreferencesIfNull(context);
            return preferences.getString(KeyEmail, null);
        }

        static String getPassword(Context context)
        {
            getPreferencesIfNull(context);
            return preferences.getString(KeyPassword, null);
        }
    }

    static class StringArrays
    {
        private static SharedPreferences preferences = null;

        private static final String KeyPreferences = "__KEY_PREFERENCES_STRING_ARRAYS__";
        private static final String KeyNameExamples = "__KEY_NAME_EXAMPLES__";
        private static final String KeySpecies = "__KEY_SPECIES__";
        private static final String KeySortedSpecies = "__KEY_SPECIES_SORTED__";

        private static void getPreferencesIfNull(Context context)
        {
            if (preferences == null)
            {
                preferences = context.getSharedPreferences(KeyPreferences, Context.MODE_PRIVATE);
            }
        }

        static void initializeIfNotExists(Context context)
        {
            getPreferencesIfNull(context);

            Resources resources = context.getResources();
            if (!preferences.contains(KeyNameExamples))
            {
                String[] names = resources.getStringArray(R.array.name_examples);
                setNameExamples(context, names);
            }
            if (!preferences.contains(KeySpecies) || !preferences.contains(KeySortedSpecies))
            {
                String[] species = resources.getStringArray(R.array.species);
                setSpecies(context, species);
            }
        }

        static String[] getNameExamples(Context context)
        {
            getPreferencesIfNull(context);
            return preferences.getString(KeyNameExamples, "").split("\0");
        }

        static void setNameExamples(Context context, String[] examples)
        {
            getPreferencesIfNull(context);
            preferences.edit().putString(KeyNameExamples, String.join("\0", examples)).apply();
        }

        static String[] getSpecies(Context context)
        {
            getPreferencesIfNull(context);
            return preferences.getString(KeySpecies, "").split("\0");
        }

        static String[] getSortedSpecies(Context context)
        {
            getPreferencesIfNull(context);
            return preferences.getString(KeySortedSpecies, "").split("\0");
        }

        static void setSpecies(Context context, String[] species)
        {
            getPreferencesIfNull(context);

            String[] sortedSpecies = Arrays.copyOf(species, species.length);
            Arrays.sort(sortedSpecies, new Comparator<String>()
            {
                @Override
                public int compare(String o1, String o2)
                {
                    return o1.compareTo(o2);
                }
            });

            preferences.edit()
                    .putString(KeySpecies, String.join("\0", species))
                    .putString(KeySortedSpecies, String.join("\0", sortedSpecies))
                    .apply();
        }
    }

    static class Account
    {
        private static SharedPreferences preferences = null;

        private static final String KeyPreferences = "__KEY_PREFERENCES_ACCOUNT__";
        private static final String KeyEmail = "__KEY_EMAIL__";
        private static final String KeyName = "__KEY_NAME__";
        private static final String KeyId = "__KEY_ID__";
        private static final String KeyAuthenticationToken = "__KEY_AUTHENTICATION_TOKEN__";

        private static void getPreferencesIfNull(Context context)
        {
            if (preferences == null)
            {
                preferences = context.getSharedPreferences(KeyPreferences, Context.MODE_PRIVATE);
            }
        }

        static void clear(Context context)
        {
            getPreferencesIfNull(context);
            preferences.edit().clear().apply();
        }

        static String getEmail(Context context)
        {
            getPreferencesIfNull(context);
            return preferences.getString(KeyEmail, "");
        }

        static void setEmail(Context context, String email)
        {
            getPreferencesIfNull(context);
            preferences.edit().putString(KeyEmail, email).apply();
        }

        static String getName(Context context)
        {
            getPreferencesIfNull(context);
            return preferences.getString(KeyName, "");

        }

        static void setName(Context context, String name)
        {
            getPreferencesIfNull(context);
            preferences.edit().putString(KeyName, name).apply();
        }

        static byte[] getId(Context context)
        {
            getPreferencesIfNull(context);
            return Methods.longToByteArray(preferences.getLong(KeyId, -1));
        }

        static void setId(Context context, byte[] id)
        {
            getPreferencesIfNull(context);
            preferences.edit().putLong(KeyId, Methods.byteArrayToLong(id)).apply();
        }

        static String getAuthenticationToken(Context context)
        {
            getPreferencesIfNull(context);
            return preferences.getString(KeyAuthenticationToken, "");

        }

        static void setAuthenticationToken(Context context, String token)
        {
            getPreferencesIfNull(context);
            preferences.edit().putString(KeyAuthenticationToken, token).apply();
        }
    }

    static class Entity
    {
        private static SharedPreferences preferences = null;

        private static final String KeyPreferences = "__KEY_PREFERENCES_ENTITY__";
        private static final String KeyUpdateList = "__KEY_UPDATE_LIST__";
        private static final String KeyEntries = "__KEY_ENTRIES__";

        private static final String JsonKeyGroup = "__JSON_KEY_GROUP__";
        private static final String JsonKeyCats = "__JSON_KEY_CATS__";

        private static ArrayList<CatGroup> groups = null;
        private static HashMap<CatGroup, List<Cat>> entries = null;
        private static Boolean updateList = null;

        private static void getPreferencesIfNull(Context context)
        {
            if (preferences == null)
            {
                preferences = context.getSharedPreferences(KeyPreferences, Context.MODE_PRIVATE);
            }
        }

        private static void loadEntries(Context context)
        {
            getPreferencesIfNull(context);

            groups = new ArrayList<CatGroup>();
            entries = new HashMap<CatGroup, List<Cat>>();

            String jsonString = preferences.getString(KeyEntries, "[]");
            try
            {
                JSONArray array = new JSONArray(jsonString);
                for (int i = 0; i < array.length(); i++)
                {
                    JSONObject entry = (JSONObject)array.get(i);
                    CatGroup group = CatGroup.parseJson((JSONObject)entry.get(JsonKeyGroup));

                    JSONArray catsJson = (JSONArray)entry.get(JsonKeyCats);
                    ArrayList<Cat> cats = new ArrayList<Cat>(catsJson.length());
                    for (int j = 0; j < catsJson.length(); j++)
                    {
                        Cat cat = Cat.parseJson((JSONObject)catsJson.get(i));
                        cats.add(cat);
                    }

                    groups.add(group);
                    entries.put(group, cats);
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        static void clear(Context context)
        {
            getPreferencesIfNull(context);
            preferences.edit().clear().apply();
        }

        static ArrayList<CatGroup> getGroups(Context context)
        {
            if (groups == null)
            {
                loadEntries(context);
            }
            else
            {
                getPreferencesIfNull(context);
            }

            return groups;
        }

        static HashMap<CatGroup, List<Cat>> getEntries(Context context)
        {
            if (entries == null)
            {
                loadEntries(context);
            }
            else
            {
                getPreferencesIfNull(context);
            }

            return entries;
        }

        static void setEntries(Context context, ArrayList<CatGroup> groups, HashMap<CatGroup, List<Cat>> entries)
        {
            getPreferencesIfNull(context);

            try
            {
                JSONArray array = new JSONArray();
                for (CatGroup group : groups)
                {
                    List<Cat> cats = entries.get(group);
                    JSONObject entry = new JSONObject();

                    entry.put(JsonKeyGroup, group.toJsonObject());

                    JSONArray catArray = new JSONArray();
                    if (cats != null)
                    {
                        for (Cat cat : cats)
                        {
                            catArray.put(cat.toJsonObject());
                        }
                    }

                    entry.put(JsonKeyCats, catArray);
                }

                preferences.edit().putString(KeyEntries, array.toString()).apply();

                Entity.groups = groups;
                Entity.entries = entries;
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        static boolean getUpdateList(Context context)
        {
            if (updateList == null)
            {
                getPreferencesIfNull(context);
                updateList = preferences.getBoolean(KeyUpdateList, false);
            }

            return updateList;
        }

        static void setUpdateList(Context context, boolean updateList)
        {
            getPreferencesIfNull(context);

            Entity.updateList = updateList;
            preferences.edit().putBoolean(KeyUpdateList, updateList).apply();
        }
    }
}
