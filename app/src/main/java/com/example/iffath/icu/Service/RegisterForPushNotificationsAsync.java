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
    ResponseCallback responseCallback;
    SharedPreferenceManager preferenceManager;
    int accountId;
    String deviceToken;

    public RegisterForPushNotificationsAsync(Activity activity,int accountId) {
        this.accountId = accountId;
        this.mActivity = activity;
    }

    protected Object doInBackground(Void... params) {
        try {
            // Register the device for notifications
            deviceToken = Pushy.register(mActivity.getApplicationContext());

            // Registration succeeded, log token to logcat
            Log.d(TAG, "Pushy device token: " + deviceToken);

            preferenceManager = SharedPreferenceManager.getInstance(mActivity.getApplicationContext());
            accountService = new AccountService(mActivity.getApplicationContext());
            createCallback();

            accountService.RegisterDevice(new PushyRegisterIdRequest(accountId,deviceToken),responseCallback);
            return deviceToken;
        }
        catch (Exception exc) {
            return exc;
        }
    }

    private void createCallback(){
        responseCallback = new ResponseCallback() {
            @Override
            public void onSuccess(Response response) {
                preferenceManager.StorePushNotificationToken(deviceToken);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG,errorMessage);
            }
        };
    }
}