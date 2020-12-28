package com.example.iffath.icu.Service.Interface;

import com.example.iffath.icu.DTO.Request.EmergencyContactRequest;
import com.example.iffath.icu.DTO.Response.MessageResponse;
import com.example.iffath.icu.Model.EmergencyContact;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface IContactService {
    @GET("contacts/{accountId}")
    Call<List<EmergencyContact>> GetAccountContacts(@Path("accountId") int id);

    @POST("contacts/new-contact")
    Call<MessageResponse> CreateContact(@Body EmergencyContactRequest emergencyContact);

    @PATCH("contacts/edit-contact/{contactId}")
    Call<MessageResponse> EditContact(@Path("contactId") int id, @Body EmergencyContactRequest emergencyContact);

    @DELETE("contacts/delete-contact/{contactId}")
    Call<MessageResponse> DeleteContact(@Path("contactId") int id);
}
