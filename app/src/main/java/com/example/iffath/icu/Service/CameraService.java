package com.example.iffath.icu.Service;

import android.content.Context;

import com.example.iffath.icu.Callback.CustomizeCallback;
import com.example.iffath.icu.Callback.HomeCallback;
import com.example.iffath.icu.Callback.ResponseCallback;
import com.example.iffath.icu.Client.RetrofitClient;
import com.example.iffath.icu.DTO.Request.AccountUpdateRequest;
import com.example.iffath.icu.DTO.Request.CameraRequest;
import com.example.iffath.icu.DTO.Response.MessageResponse;
import com.example.iffath.icu.Model.Camera;
import com.example.iffath.icu.Service.Interface.ICameraService;

import retrofit2.Call;

public class CameraService {
    ICameraService ICameraService;
    Context mContext;

    public CameraService(Context context){
        this.ICameraService = RetrofitClient.getRetrofitClientInstance().create(ICameraService.class);
        this.mContext = context;
    }

    public void UpdateCamera(CameraRequest cameraRequest, int deviceId, ResponseCallback responseCallback){
        Call<MessageResponse> responseCall = this.ICameraService.EditCamera(deviceId,cameraRequest);
        responseCall.enqueue(new CustomizeCallback<MessageResponse>(responseCallback,mContext));
    }

    public void SetupCamera(CameraRequest cameraRequest,ResponseCallback responseCallback){
        Call<Camera> responseCall = this.ICameraService.SetupConnection(cameraRequest);
        responseCall.enqueue(new CustomizeCallback<Camera>(responseCallback,mContext));
    }

    public void DeleteCamera(int deviceId, ResponseCallback responseCallback){
        Call<MessageResponse> responseCall = this.ICameraService.DeleteCamera(deviceId);
        responseCall.enqueue(new CustomizeCallback<MessageResponse>(responseCallback, mContext));
    }

    public void ArmCamera(int accountId,ResponseCallback responseCallback){
        Call<MessageResponse> responseCall = this.ICameraService.ArmCamera(accountId);
        responseCall.enqueue(new CustomizeCallback<MessageResponse>(responseCallback,mContext));
    }

    public void GetCamera(int deviceId, ResponseCallback responseCallback){
        Call<Camera> responseCall = this.ICameraService.GetDevice(deviceId);
        responseCall.enqueue(new HomeCallback<Camera>(responseCallback));
    }
}
