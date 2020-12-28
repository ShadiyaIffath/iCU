package com.example.iffath.icu.DTO.Request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyContactRequest {
    private String name;
    private String nickname;
    private String email;
    private int phone;
    private int account_id;
}
