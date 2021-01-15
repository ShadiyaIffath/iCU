package com.example.iffath.icu.Service.Interface;

import com.example.iffath.icu.DTO.Request.AccountUpdateRequest;
import com.example.iffath.icu.DTO.Request.PushyRegisterIdRequest;
import com.example.iffath.icu.DTO.Response.HomeResponse;
import com.example.iffath.icu.DTO.Response.MessageResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface IAccountService {
    @PATCH("account/update-account/{accountId}")
    Call<MessageResponse> UpdateAccount(@Path("accountId") int id, @Body AccountUpdateRequest accountUpdateRequest);

    @DELETE("account/delete-account/{accountId}")
    Call<MessageResponse> DeleteAccount(@Path("accountId") int id);

    @POST("account/register-device")
    Call<MessageResponse> RegisterDevice(@Body PushyRegisterIdRequest pushyRequest);

    @GET("account/home-details/{accountId}")
    Call<HomeResponse> GetHomeDetails(@Path("accountId") int id);
}
