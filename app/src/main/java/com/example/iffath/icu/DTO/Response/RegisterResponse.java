package com.example.iffath.icu.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {
    private int id;
    private String first_name;
    private String last_name;
    private String email;
    private String password;
    private String address;
    private String device_id;
    private int phone;
}
