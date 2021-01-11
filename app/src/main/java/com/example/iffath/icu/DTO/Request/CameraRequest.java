package com.example.iffath.icu.DTO.Request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CameraRequest {
    private String model;
    private String rtsp_address;
    private boolean armed;
    private int account_id;

    public CameraRequest(String model, String rtsp_address, int account_id, boolean armed) {
        this.model = model;
        this.rtsp_address = rtsp_address;
        this.account_id = account_id;
        this.armed = false;
    }
}
