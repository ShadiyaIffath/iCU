package com.example.iffath.icu.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    private int id;
    private String first_name;
    private String last_name;
    private String email;
    private String password;
    private int phone;

    public Account(String first_name, String last_name, String email, String password, int phone) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.password = password;
        this.phone = phone;
    }
}
