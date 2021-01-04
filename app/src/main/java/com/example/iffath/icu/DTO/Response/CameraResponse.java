package com.example.iffath.icu.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CameraResponse {
    private int id;
    private String model;
    private String rtsp_address;
    private boolean armed;
    private int account_id;
}
