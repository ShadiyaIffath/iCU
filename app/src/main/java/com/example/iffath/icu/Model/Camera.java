package com.example.iffath.icu.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Camera {
    private int id;
    private String model;
    private String rtsp_address;
    private boolean armed;
    private int account_id;
}
