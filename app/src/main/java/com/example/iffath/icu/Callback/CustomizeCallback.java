package com.example.iffath.icu.Callback;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.WindowManager;

import com.example.iffath.icu.DTO.Response.MessageResponse;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomizeCallback<T> implements Callback<T> {

    private ResponseCallback responseCallBack;
    private ProgressDialog mProgressDialog;
    Context context;

    public CustomizeCallback(ResponseCallback responseCallBack,Context context) {
        this.responseCallBack = responseCallBack;
        this.context = context;
        mProgressDialog = new ProgressDialog(context);
        ((Activity) context).getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            ((Activity) context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
        if(!response.isSuccessful() && response.errorBody() != null){
            String error = null;
            try{
                error = new Gson().fromJson(response.errorBody().string(), MessageResponse.class).getCode();
//                error = response.errorBody().string();
            }catch (Exception e) {
                error = "Error converting response "+ e.getMessage();
                e.printStackTrace();
            }
            System.out.println("Customize callback not successful "+error);
            responseCallBack.onError(error);
        } else if(response.body()!=null) {
            responseCallBack.onSuccess(response);
            System.out.println("Customize callback successful "+response.body());
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            ((Activity) context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
        String errorMessage = t.getMessage();
        System.out.println("Customize callback failure message: " + errorMessage);
        responseCallBack.onError("Network error please try again later!" +errorMessage);
    }
}