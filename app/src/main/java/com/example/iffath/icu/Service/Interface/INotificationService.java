package com.example.iffath.icu.Service.Interface;

import com.example.iffath.icu.DTO.Response.MessageResponse;
import com.example.iffath.icu.Model.Notification;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface INotificationService {

    @GET("notifications/{accountId}")
    Call<List<Notification>> GetAccountMessages(@Path("accountId") int id);


    @DELETE("notifications/delete-notification/{notificationId}")
    Call<MessageResponse> DeleteNotification(@Path("notificationId") int id);
}
