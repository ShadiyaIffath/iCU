package com.example.iffath.icu.Service;

import android.content.Context;

import com.example.iffath.icu.Callback.CustomizeCallback;
import com.example.iffath.icu.Callback.HomeCallback;
import com.example.iffath.icu.Callback.ResponseCallback;
import com.example.iffath.icu.Client.RetrofitClient;
import com.example.iffath.icu.DTO.Request.AccountUpdateRequest;
import com.example.iffath.icu.DTO.Request.PushyRegisterIdRequest;
import com.example.iffath.icu.DTO.Response.HomeResponse;
import com.example.iffath.icu.DTO.Response.MessageResponse;
import com.example.iffath.icu.Service.Interface.IAccountService;

import retrofit2.Call;

public class AccountService {
    IAccountService IAccountService;
    Context mContext;

    public AccountService(Context context){
        this.IAccountService = RetrofitClient.getRetrofitClientInstance().create(IAccountService.class);
        this.mContext = context;
    }

    public void UpdateAccount(AccountUpdateRequest accountUpdateRequest, int accountId, ResponseCallback responseCallback){
        Call<MessageResponse> responseCall = this.IAccountService.UpdateAccount(accountId,accountUpdateRequest);
        responseCall.enqueue(new CustomizeCallback<MessageResponse>(responseCallback,mContext));
    }

    public void DeleteAccount(int accountId,ResponseCallback responseCallback){
        Call<MessageResponse> responseCall = this.IAccountService.DeleteAccount(accountId);
        responseCall.enqueue(new CustomizeCallback<MessageResponse>(responseCallback,mContext));
    }

    public void RegisterDevice(PushyRegisterIdRequest pushyRequest, ResponseCallback responseCallback){
        Call<MessageResponse> responseCall = this.IAccountService.RegisterDevice(pushyRequest);
        responseCall.enqueue(new CustomizeCallback<MessageResponse>(responseCallback,mContext));
    }

    public void HomeDetails(int accountId, ResponseCallback responseCallback){
        Call<HomeResponse> responseCall = this.IAccountService.GetHomeDetails(accountId);
        responseCall.enqueue(new HomeCallback<HomeResponse>(responseCallback));
    }
}
