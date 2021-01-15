package com.example.iffath.icu.Callback;

import android.content.Context;

import com.example.iffath.icu.DTO.Response.MessageResponse;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeCallback<T> implements Callback<T> {
    private ResponseCallback responseCallBack;

    public HomeCallback(ResponseCallback responseCallBack) {
        this.responseCallBack = responseCallBack;
    }
    @Override
    public void onResponse(Call<T> call, Response<T> response) {
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
        String errorMessage = t.getMessage();
        System.out.println("Customize callback failure message: " + errorMessage);
        responseCallBack.onError("Network error please try again later!" +errorMessage);
    }
}
