package com.example.iffath.icu.Service.Interface;

import com.example.iffath.icu.DTO.Request.AccountUpdateRequest;
import com.example.iffath.icu.DTO.Response.MessageResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.PATCH;
import retrofit2.http.Path;

public interface IAccountService {
    @PATCH("account/update-account/{accountId}")
    Call<MessageResponse> UpdateAccount(@Path("accountId") int id, @Body AccountUpdateRequest accountUpdateRequest);
}
