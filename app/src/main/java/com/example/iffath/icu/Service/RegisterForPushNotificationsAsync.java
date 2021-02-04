package com.example.iffath.icu.Service;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.example.iffath.icu.Callback.ResponseCallback;
import com.example.iffath.icu.DTO.Request.PushyRegisterIdRequest;
import com.example.iffath.icu.Storage.SharedPreferenceManager;

import java.net.URL;

import me.pushy.sdk.Pushy;
import retrofit2.Response;

public class RegisterForPushNotificationsAsync extends AsyncTask<Void, Void, Object> {
    Activity mActivity;
    final String TAG = "PUSHY";

    AccountService accountService;
    ResponseCallback pushyTokenCallback;
    SharedPreferenceManager preferenceManager;
    int accountId;
    String deviceToken;

    public RegisterForPushNotificationsAsync(Activity activity, int accountId) {
        this.mActivity = activity;
        this.accountId = accountId;
        preferenceManager = SharedPreferenceManager.getInstance(activity.getApplicationContext());
        accountService = new AccountService(activity.getApplicationContext());
    }

    protected Object doInBackground(Void... params) {
        try {
            // Register the device for notifications
            String deviceToken = Pushy.register(mActivity.getApplicationContext());
            // Registration succeeded, log token to logcat
            Log.d(TAG, "Pushy device token: " + deviceToken);
            createCallback();
            return deviceToken;
        }
        catch (Exception exc) {
            return exc;
        }
    }

    @Override
    protected void onPostExecute(Object result) {
        // Registration failed?
        if (result instanceof Exception) {
            // Log to console
            Log.e("Pushy", result.toString());
        }
        else {
           deviceToken =  result.toString();
           accountService.RegisterDevice(new PushyRegisterIdRequest(accountId, deviceToken),pushyTokenCallback);
        }
    }

    private void createCallback(){
        pushyTokenCallback = new ResponseCallback() {
            @Override
            public void onSuccess(Response response) {
                preferenceManager.StorePushNotificationToken(deviceToken);
                Log.d("PUSHY","Device token saved");
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("PUSHY",errorMessage);
            }
        };
    }

}