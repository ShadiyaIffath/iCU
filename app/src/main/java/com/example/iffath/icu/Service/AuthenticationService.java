package com.example.iffath.icu.Service;

import android.content.Context;

import com.example.iffath.icu.Callback.CustomizeCallback;
import com.example.iffath.icu.Callback.ResponseCallback;
import com.example.iffath.icu.Client.RetrofitClient;
import com.example.iffath.icu.DTO.Request.LoginRequest;
import com.example.iffath.icu.DTO.Request.RegisterRequest;
import com.example.iffath.icu.DTO.Response.LoginResponse;
import com.example.iffath.icu.DTO.Response.RegisterResponse;
import com.example.iffath.icu.Model.Account;
import com.example.iffath.icu.Service.Interface.IAuthenticationService;

import retrofit2.Call;

public class AuthenticationService {
    IAuthenticationService IAuthenticationService;
    Context mContext;

    public AuthenticationService(Context context) {
        this.IAuthenticationService = RetrofitClient.getRetrofitClientInstance().create(IAuthenticationService.class);
        this.mContext = context;
    }

    public void login(LoginRequest request, ResponseCallback callback) {
        Call<LoginResponse> loginResponseCall = IAuthenticationService.loginUser(request);
        loginResponseCall.enqueue(new CustomizeCallback<LoginResponse>(callback,mContext));
    }

    public void register(RegisterRequest registerRequest, ResponseCallback callback) {
        Call<RegisterResponse> userCall = IAuthenticationService.registerUser(registerRequest);
        userCall.enqueue(new CustomizeCallback<RegisterResponse>(callback, mContext));
    }
}
