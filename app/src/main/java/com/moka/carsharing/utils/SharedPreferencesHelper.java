package com.moka.carsharing.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Burgess on 2017/4/30.
 */

public class SharedPreferencesHelper {
    private SharedPreferences sp;
    private static SharedPreferencesHelper sharedPreferencesHelper;

    public static SharedPreferencesHelper getInstance(Context context){
        if (null == sharedPreferencesHelper){
            sharedPreferencesHelper = new SharedPreferencesHelper();
            sharedPreferencesHelper.sp = context.getSharedPreferences("sp_demo", Context.MODE_PRIVATE);
        }
        return sharedPreferencesHelper;
    }

    public void putInt(String name, int value){
        sp.edit().putInt(name, value).apply();
    }

    public void putFloat(String name, float value){
        sp.edit().putFloat(name, value).apply();
    }

    public void putString(String name, String value){
        sp.edit().putString(name, value).apply();
    }

    public String getString(String name){
        return sp.getString(name, null);
    }

    public Float getFloat(String name){
        return sp.getFloat(name, 0);
    }
}
