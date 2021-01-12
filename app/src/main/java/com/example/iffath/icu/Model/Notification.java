package com.example.iffath.icu.Model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    private int id;
    private String message;
    private String occurred_on;
    private String title;
    private int account_id;
}
