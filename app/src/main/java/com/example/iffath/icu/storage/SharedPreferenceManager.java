package com.example.iffath.icu.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.iffath.icu.Model.Account;
import com.example.iffath.icu.R;

public class SharedPreferenceManager {
    private static String Key_Preferences;
    private static Context context;
    public static SharedPreferenceManager sharedPreferenceManagerInstance;

    private SharedPreferenceManager(Context activityContext){
        Key_Preferences = activityContext.getString(R.string.preference_key);
        context = activityContext;
    }

    public static SharedPreferenceManager getInstance(Context activityContext) {
        if(sharedPreferenceManagerInstance == null) {
            sharedPreferenceManagerInstance = new SharedPreferenceManager(activityContext);
        }
        return sharedPreferenceManagerInstance;
    }

    public Account GetAccount(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Key_Preferences, Context.MODE_PRIVATE);
        int id = sharedPreferences.getInt("id", -1);
        String fName = sharedPreferences.getString("firstName", "");
        String lName = sharedPreferences.getString("lastName", "");
        int number = sharedPreferences.getInt("phone", -1);
        String mail = sharedPreferences.getString("email", "");
        String password = sharedPreferences.getString("password", "");
        String role = sharedPreferences.getString("role", "ROLE_USER");
        return new Account(id,fName,lName,mail,password,"",true,number,role);
    }

    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Key_Preferences, Context.MODE_PRIVATE);
        int id = 0;
        id = sharedPreferences.getInt("id", -1);
        if (sharedPreferences.getInt("id", -1) != -1) {
            return true;
        }
        return false;
    }

    public void clear() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Key_Preferences, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
