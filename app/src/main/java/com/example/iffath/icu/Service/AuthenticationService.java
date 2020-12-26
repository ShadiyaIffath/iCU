package com.example.iffath.icu.Service;

import com.example.iffath.icu.Callback.CustomizeCallback;
import com.example.iffath.icu.Callback.ResponseCallback;
import com.example.iffath.icu.Client.RetrofitClient;
import com.example.iffath.icu.DTO.Request.LoginRequest;
import com.example.iffath.icu.DTO.Request.RegisterRequest;
import com.example.iffath.icu.DTO.Response.LoginResponse;
import com.example.iffath.icu.Model.Account;
import com.example.iffath.icu.Service.Interface.IAuthenticationService;

import retrofit2.Call;

public class AuthenticationService {
    com.example.iffath.icu.Service.Interface.IAuthenticationService IAuthenticationService;

    public AuthenticationService() {
        this.IAuthenticationService = RetrofitClient.getRetrofitClientInstance().create(IAuthenticationService.class);
    }

    public void login(LoginRequest request, ResponseCallback callback) {
        Call<LoginResponse> loginResponseCall = IAuthenticationService.loginUser(request);
        loginResponseCall.enqueue(new CustomizeCallback<LoginResponse>(callback));
    }

    public void register(RegisterRequest registerRequest, ResponseCallback callback) {
        Call<LoginResponse> userCall = IAuthenticationService.registerUser(registerRequest);
        userCall.enqueue(new CustomizeCallback<LoginResponse>(callback));
    }
}
