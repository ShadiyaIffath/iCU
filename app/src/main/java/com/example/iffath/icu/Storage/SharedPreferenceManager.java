package com.example.iffath.icu.Storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.iffath.icu.DTO.Request.AccountUpdateRequest;
import com.example.iffath.icu.DTO.Response.LoginResponse;
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

    public void StoreAccountDetails(LoginResponse account){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Key_Preferences, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("id", account.getId());
        editor.putString("firstName", account.getFirst_name());
        editor.putString("lastName", account.getLast_name());
        editor.putString("email", account.getEmail());
        editor.putInt("phone", account.getPhone());
        editor.putString("password", account.getPassword());
        editor.apply();
    }

    public Account GetAccount(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Key_Preferences, Context.MODE_PRIVATE);
        int id = sharedPreferences.getInt("id", -1);
        String fName = sharedPreferences.getString("firstName", "");
        String lName = sharedPreferences.getString("lastName", "");
        int number = sharedPreferences.getInt("phone", -1);
        String mail = sharedPreferences.getString("email", "");
        String password = sharedPreferences.getString("password", "");
        return new Account(id,fName,lName,mail,password,number);
    }

    public String GetAccountName(){
        String fullname,name = "";
        SharedPreferences sharedPreferences = context.getSharedPreferences(Key_Preferences, Context.MODE_PRIVATE);
        name = sharedPreferences.getString("firstName", name);
        fullname = name+" ";
        name = sharedPreferences.getString("lastName", name);
        fullname += name;
        return fullname;
    }

    public int getLoggedInUserId(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Key_Preferences, Context.MODE_PRIVATE);
        return sharedPreferences.getInt("id", -1);
    }

    public boolean isLoggedIn() {
        if (getLoggedInUserId() != -1) {
            return true;
        }
        return false;
    }

    public void updateAccount(AccountUpdateRequest account){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Key_Preferences, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("firstName", account.getFirst_name());
        editor.putString("lastName", account.getLast_name());
        editor.putString("email", account.getEmail());
        editor.putInt("phone", account.getPhone());
        editor.putString("password", account.getPassword());
        editor.apply();
    }

    public void clear() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Key_Preferences, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
