package com.example.iffath.icu.Callback;

import retrofit2.Response;

public interface ResponseCallback {
    void onSuccess(Response response);
    void onError(String errorMessage);
}