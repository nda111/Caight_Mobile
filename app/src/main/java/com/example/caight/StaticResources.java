package com.example.caight;

import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class StaticResources
{
    static SharedPreferences loginPreferences = null;
    static final String LoginPreferenceName = "__SHARED_PREFERENCE_LOGIN__";
    static final String LoginPreferenceItemAutoLogin = "__SHARED_PREFERENCES_LOGIN_AUTO_LOGIN__";
    static final String LogInPreferenceItemEmail = "__SHARED_PREFERENCES_LOGIN_EMAIL__";
    static final String LogInPreferenceItemPassword = "__SHARED_PREFERENCES_LOGIN_PASSWORD__";

    static String myEmail = null;
    static String myName = null;
    static byte[] accountId = null;
    static String authToken = null;

    static boolean updateEntityList = false;

    static ArrayList<CatGroup> groups = new ArrayList<CatGroup>();
    static HashMap<CatGroup, List<Cat>> entries = new HashMap<CatGroup, List<Cat>>();
}
