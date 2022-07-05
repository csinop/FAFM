package com.example.fitnessappformyself.main_menu_fragments;

import android.content.SharedPreferences;
import java.util.Set;

public class SharedPreferencesHandler<T> {
    private final SharedPreferences preference;

    public SharedPreferencesHandler(SharedPreferences preference){
        this.preference = preference;
    }

    /* put into SharedPreferences */
    public void saveToSharedPreferences(String whereToSave, T data){
        if(data instanceof String)
            preference.edit().putString(whereToSave, (String) data).apply();
        else if(data instanceof Integer)
            preference.edit().putInt(whereToSave, (Integer) data).apply();
        else if(data instanceof Boolean)
            preference.edit().putBoolean(whereToSave, (Boolean) data).apply();
        else if(data instanceof Long)
            preference.edit().putLong(whereToSave, (Long) data).apply();
        else if(data instanceof Float)
            preference.edit().putFloat(whereToSave, (Float) data).apply();
        else if(data instanceof Set)
            preference.edit().putStringSet(whereToSave, (Set<String>) data).apply();
        else{
            //error
        }
    }

    /* delete from SharedPreferences */
    public void deleteFromSharedPreferences(String whereToSave, String applyOrCommit){
            preference.edit().remove(whereToSave).apply();
    }

    /* delete SharedPreferences */
    public void deleteSharedPreferences(String applyOrCommit) {
        preference.edit().clear().apply();
    }

    /* get from SharedPreferences */
    public T getFromSharedPreferences(String whatToGet, T defaultValue){
        try {
            if (defaultValue instanceof String) {
                String r = preference.getString(whatToGet, (String) defaultValue);
                return (T) r;
            } else if (defaultValue instanceof Integer) {
                Integer r = preference.getInt(whatToGet, (Integer) defaultValue);
                return (T) r;
            } else if (defaultValue instanceof Boolean) {
                Boolean r = preference.getBoolean(whatToGet, (Boolean) defaultValue);
                return (T) r;
            } else if (defaultValue instanceof Long) {
                Long r = preference.getLong(whatToGet, (Long) defaultValue);
                return (T) r;
            } else if (defaultValue instanceof Float) {
                Float r = preference.getFloat(whatToGet, (Float) defaultValue);
                return (T) r;
            } else if (defaultValue instanceof Set) {
                Set<String> r = preference.getStringSet(whatToGet, (Set<String>) defaultValue);
                return (T) r;
            } else {
                return null;
            }
        }catch (ClassCastException e) {
            e.printStackTrace();
            return null;
        }
    }

}
