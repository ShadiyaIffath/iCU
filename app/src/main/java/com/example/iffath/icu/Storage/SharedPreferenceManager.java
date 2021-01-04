package com.example.iffath.icu.Storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.iffath.icu.DTO.Request.AccountUpdateRequest;
import com.example.iffath.icu.DTO.Request.CameraRequest;
import com.example.iffath.icu.DTO.Response.CameraResponse;
import com.example.iffath.icu.DTO.Response.LoginResponse;
import com.example.iffath.icu.DTO.Response.RegisterResponse;
import com.example.iffath.icu.Model.Account;
import com.example.iffath.icu.Model.Camera;
import com.example.iffath.icu.R;

public class SharedPreferenceManager {
    private static String profile_Key_Preferences, device_key_preferences;
    private static Context context;
    public static SharedPreferenceManager sharedPreferenceManagerInstance;

    private SharedPreferenceManager(Context activityContext){
        device_key_preferences = activityContext.getString(R.string.device_key);
        profile_Key_Preferences =  activityContext.getString(R.string.preference_key);
        context = activityContext;
    }

    public static SharedPreferenceManager getInstance(Context activityContext) {
        if(sharedPreferenceManagerInstance == null) {
            sharedPreferenceManagerInstance = new SharedPreferenceManager(activityContext);
        }
        return sharedPreferenceManagerInstance;
    }

    public void StoreAccountDetails(Account account){
        SharedPreferences sharedPreferences = context.getSharedPreferences(profile_Key_Preferences, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        System.out.println(account.getId());
        editor.putInt("id", account.getId());
        editor.putString("firstName", account.getFirst_name());
        editor.putString("lastName", account.getLast_name());
        editor.putString("email", account.getEmail());
        editor.putString("address",account.getAddress());
        editor.putInt("phone", account.getPhone());
        editor.putString("password", account.getPassword());
        editor.apply();
    }

    public void StoreDeviceDetails(Camera camera){
        SharedPreferences sharedPreferences = context.getSharedPreferences(device_key_preferences, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("camera_id", camera.getId());
        editor.putString("camera_model",camera.getModel());
        editor.putString("camera_rtsp",camera.getRtsp_address());
        editor.putBoolean("camera_armed", camera.isArmed());
        editor.apply();
    }

    public void StoreRegisteredAccountDetails(RegisterResponse account){
        SharedPreferences sharedPreferences = context.getSharedPreferences(profile_Key_Preferences, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("id", account.getId());
        editor.putString("firstName", account.getFirst_name());
        editor.putString("lastName", account.getLast_name());
        editor.putString("email", account.getEmail());
        editor.putInt("phone", account.getPhone());
        editor.putString("password", account.getPassword());
        editor.putString("address",account.getAddress());
        editor.apply();
    }

    public Account GetAccount(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(profile_Key_Preferences, Context.MODE_PRIVATE);
        int id = sharedPreferences.getInt("id", -1);
        String fName = sharedPreferences.getString("firstName", "");
        String lName = sharedPreferences.getString("lastName", "");
        int number = sharedPreferences.getInt("phone", -1);
        String mail = sharedPreferences.getString("email", "");
        String password = sharedPreferences.getString("password", "");
        String address = sharedPreferences.getString("address","");
        return new Account(id,fName,lName,mail,address,password,number);
    }

    public Camera GetCamera(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(device_key_preferences, Context.MODE_PRIVATE);
        int id = sharedPreferences.getInt("camera_id",-1);
        String model = sharedPreferences.getString("camera_model","");
        String rtsp = sharedPreferences.getString("camera_rtsp","");
        boolean armed = sharedPreferences.getBoolean("camera_armed", false);
        int accountId = GetLoggedInUserId();
        return new Camera(id,model,rtsp,armed,accountId);
    }

    public String GetAccountName(){
        String fullname,name = "";
        SharedPreferences sharedPreferences = context.getSharedPreferences(profile_Key_Preferences, Context.MODE_PRIVATE);
        name = sharedPreferences.getString("firstName", name);
        fullname = name+" ";
        name = sharedPreferences.getString("lastName", name);
        fullname += name;
        return fullname;
    }

    public int GetLoggedInUserId(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(profile_Key_Preferences, Context.MODE_PRIVATE);
        return sharedPreferences.getInt("id", -1);
    }

    public boolean IsLoggedIn() {
        if (GetLoggedInUserId() != -1) {
            return true;
        }
        return false;
    }

    public boolean HasConnection(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(device_key_preferences, Context.MODE_PRIVATE);
        if(sharedPreferences.getInt("camera_id",-1)!=-1){
            return true;
        }
        return false;
    }

    public void UpdateAccount(AccountUpdateRequest account){
        SharedPreferences sharedPreferences = context.getSharedPreferences(profile_Key_Preferences, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("firstName", account.getFirst_name());
        editor.putString("lastName", account.getLast_name());
        editor.putString("email", account.getEmail());
        editor.putInt("phone", account.getPhone());
        editor.putString("password", account.getPassword());
        editor.apply();
    }

    public void UpdateDeviceDetails(CameraRequest camera){
        SharedPreferences sharedPreferences = context.getSharedPreferences(device_key_preferences, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("camera_model",camera.getModel());
        editor.putString("camera_rtsp",camera.getRtsp_address());
        editor.putBoolean("camera_armed", camera.isArmed());
        editor.apply();
    }

    public void Clear() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(profile_Key_Preferences, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        clearDeviceSettings();
    }

    public void clearDeviceSettings(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(device_key_preferences,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
