package com.example.iffath.icu.DTO.Response;

import com.google.gson.annotations.SerializedName;

public class MessageResponse {
    @SerializedName("message")
    private String messageResponse;

    @SerializedName("code")
    private String code;

    public String getMessageResponse() {
        return messageResponse;
    }

    public String getCode(){return code;}
}

