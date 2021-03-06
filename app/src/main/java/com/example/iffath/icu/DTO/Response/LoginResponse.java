package com.example.iffath.icu.DTO.Response;

import com.example.iffath.icu.Model.Account;
import com.example.iffath.icu.Model.Camera;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private Account account;
    private Camera camera;
}
