package com.example.iffath.icu.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HomeResponse {
    private boolean armed;
    private int burglarAlerts;
    private int contacts;
}
