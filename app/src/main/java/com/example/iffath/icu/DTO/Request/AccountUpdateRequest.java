package com.example.iffath.icu.DTO.Request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountUpdateRequest {
    private String first_name;
    private String last_name;
    private String email;
    private String address;
    private String password;
    private String device_id;
    private int phone;
}
