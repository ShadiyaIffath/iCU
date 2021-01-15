package com.example.iffath.icu.Service;

import com.example.iffath.icu.Callback.CustomizeCallback;
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

    public AccountService(){
        this.IAccountService = RetrofitClient.getRetrofitClientInstance().create(IAccountService.class);
    }

    public void UpdateAccount(AccountUpdateRequest accountUpdateRequest, int accountId, ResponseCallback responseCallback){
        Call<MessageResponse> responseCall = this.IAccountService.UpdateAccount(accountId,accountUpdateRequest);
        responseCall.enqueue(new CustomizeCallback<MessageResponse>(responseCallback));
    }

    public void DeleteAccount(int accountId,ResponseCallback responseCallback){
        Call<MessageResponse> responseCall = this.IAccountService.DeleteAccount(accountId);
        responseCall.enqueue(new CustomizeCallback<MessageResponse>(responseCallback));
    }

    public void RegisterDevice(PushyRegisterIdRequest pushyRequest, ResponseCallback responseCallback){
        Call<MessageResponse> responseCall = this.IAccountService.RegisterDevice(pushyRequest);
        responseCall.enqueue(new CustomizeCallback<MessageResponse>(responseCallback));
    }

    public void HomeDetails(int accountId, ResponseCallback responseCallback){
        Call<HomeResponse> responseCall = this.IAccountService.GetHomeDetails(accountId);
        responseCall.enqueue(new CustomizeCallback<HomeResponse>(responseCallback));
    }
}
