package com.example.iffath.icu.Service.Interface;

import com.example.iffath.icu.DTO.LoginRequest;
import com.example.iffath.icu.DTO.LoginResponse;
import com.example.iffath.icu.Model.Account;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface IAuthenticationService {
    @POST("signIn")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    @POST("signUp")
    Call<LoginResponse> registerUser(@Body Account account);
}