package com.example.iffath.icu.Service.Interface;

import com.example.iffath.icu.DTO.Request.CameraRequest;
import com.example.iffath.icu.DTO.Request.EmergencyContactRequest;
import com.example.iffath.icu.DTO.Response.MessageResponse;
import com.example.iffath.icu.Model.Camera;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ICameraService {
    @POST("device/test-connection")
    Call<MessageResponse> TestConnection(@Body String rtsp);

    @POST("device/setup-device")
    Call<Camera> SetupConnection(@Body CameraRequest cameraRequest);

    @PATCH("device/update-device/{deviceId}")
    Call<MessageResponse> EditCamera(@Path("deviceId") int id, @Body CameraRequest cameraRequest);

    @DELETE("device/delete-device/{deviceId}")
    Call<MessageResponse> DeleteCamera(@Path("deviceId") int id);
}
