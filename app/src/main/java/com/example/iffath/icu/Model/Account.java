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
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String token;
    private boolean active;
    private int phone;
    private String type;

    public Account(int id, String firstName, String lastName, String email, String password, int phone, String type) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.active = true;
        this.phone = phone;
        this.type = type;
    }

    public Account(String firstName, String lastName, String email, String password, int phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.active = true;
    }
}
