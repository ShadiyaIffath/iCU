package com.example.iffath.icu.Service;

import com.example.iffath.icu.Callback.CustomizeCallback;
import com.example.iffath.icu.Callback.ResponseCallback;
import com.example.iffath.icu.Client.RetrofitClient;
import com.example.iffath.icu.DTO.Response.MessageResponse;
import com.example.iffath.icu.Model.Notification;
import com.example.iffath.icu.Service.Interface.INotificationService;

import java.util.List;

import retrofit2.Call;

public class NotificationService {
    INotificationService INotificationService;

    public NotificationService(){
        this.INotificationService= RetrofitClient.getRetrofitClientInstance().create(INotificationService.class);
    }

    public void DeleteNotification(int notificationId, ResponseCallback callback){
        Call<MessageResponse> call = INotificationService.DeleteNotification(notificationId);
        call.enqueue(new CustomizeCallback<MessageResponse>(callback));
    }

    public void GetAllNotifications(int accountId, ResponseCallback responseCallback){
        Call<List<Notification>> call = INotificationService.GetAccountMessages(accountId);
        call.enqueue(new CustomizeCallback<List<Notification>>(responseCallback));
    }
}
