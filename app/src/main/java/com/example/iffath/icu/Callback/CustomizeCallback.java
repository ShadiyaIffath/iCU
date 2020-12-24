package com.example.iffath.icu.Callback;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomizeCallback<T> implements Callback<T> {

    private ResponseCallback responseCallBack;

    public CustomizeCallback(ResponseCallback responseCallBack) {
        this.responseCallBack = responseCallBack;
    }
    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if(!response.isSuccessful() && response.errorBody() != null){
            String error = null;
            try{
//                error = new Gson().fromJson(response.errorBody().string(), MessageResponse.class).getMessageResponse();
                error = response.errorBody().string();
            }catch (IOException e) {
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