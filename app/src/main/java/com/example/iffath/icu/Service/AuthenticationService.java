package com.example.iffath.icu.Service;

import com.example.iffath.icu.Callback.CustomizeCallback;
import com.example.iffath.icu.Callback.ResponseCallback;
import com.example.iffath.icu.Client.RetrofitClient;
import com.example.iffath.icu.DTO.LoginRequest;
import com.example.iffath.icu.DTO.LoginResponse;
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

    public void register(Account acc, ResponseCallback callback) {
        Call<LoginResponse> userCall = IAuthenticationService.registerUser(acc);
        userCall.enqueue(new CustomizeCallback<LoginResponse>(callback));
    }
}
