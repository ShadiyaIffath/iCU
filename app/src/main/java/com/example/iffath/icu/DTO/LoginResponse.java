package com.example.iffath.icu.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private int phone;
    private String type;
}
