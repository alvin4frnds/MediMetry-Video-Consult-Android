package com.medimetry.medimetryvideoconsultation;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by Pardeep on 8/28/2016.
 */

public class SharedPrefrenceClass
{




    SharedPreferences sharedPreferences;
    Context context;

    String logedInId;

    public String getLogedInId() {

        logedInId=retrieveSharedPref("loginId");
        return logedInId;
    }

    public void setLogedInId(String logedInId) {
        this.logedInId = logedInId;
        inputSharedPref("loginId",""+logedInId);
    }

    public SharedPrefrenceClass(Context ctx)
    {
        context=ctx;
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(context);
    }



    boolean login;
    String userId;

    public boolean isLogin() {
        login=RetrieveSavedBooleanPref("login");
        return login;
    }

    public void setLogin(boolean login) {
        this.login = login;
        SaveBooleanPref("login",login);
    }

    public String getUserId() {
        userId=retrieveSharedPref("userId");
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
        inputSharedPref("userId",userId);
    }

    public void inputSharedPref(String key, String value)
    {
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(""+key,""+value);
        editor.commit();
    }
    public String retrieveSharedPref(String key)
    {
        return sharedPreferences.getString(""+key,"0");
    }





    public void SaveBooleanPref(String key, boolean value)
    {
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean(key,value);
        editor.commit();
    }

    public boolean RetrieveSavedBooleanPref(String key)
    {
        return sharedPreferences.getBoolean(key,false);
    }









}
