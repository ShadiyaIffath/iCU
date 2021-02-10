package com.example.iffath.icu.DTO.Request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FirebaseRegisterIdRequest {
    private int accountId;
    private String deviceId;
}
