package com.example.iffath.icu.Service;

import android.content.Context;
import android.os.Message;

import com.example.iffath.icu.Callback.CustomizeCallback;
import com.example.iffath.icu.Callback.ResponseCallback;
import com.example.iffath.icu.Client.RetrofitClient;
import com.example.iffath.icu.DTO.Request.EmergencyContactRequest;
import com.example.iffath.icu.DTO.Response.MessageResponse;
import com.example.iffath.icu.Model.EmergencyContact;
import com.example.iffath.icu.Service.Interface.IContactService;

import java.util.List;

import retrofit2.Call;

public class ContactsService {
    IContactService IContactService;
    Context mContext;

    public ContactsService(Context context){
        this.IContactService = RetrofitClient.getRetrofitClientInstance().create(IContactService.class);
        this.mContext = context;
    }

    public void GetAccountContacts(int accountId, ResponseCallback callback){
        Call<List<EmergencyContact>> contactsResponseCall = IContactService.GetAccountContacts(accountId);
        contactsResponseCall.enqueue(new CustomizeCallback<List<EmergencyContact>>(callback,mContext));
    }

    public void AddContact(EmergencyContactRequest emergencyContact, ResponseCallback callback){
        Call<MessageResponse> responseCall = IContactService.CreateContact(emergencyContact);
        responseCall.enqueue(new CustomizeCallback<MessageResponse>(callback,mContext));
    }

    public void EditContact(EmergencyContactRequest emergencyContactRequest,int contactId, ResponseCallback responseCallback){
        Call<MessageResponse> responseCall = IContactService.EditContact(contactId,emergencyContactRequest);
        responseCall.enqueue(new CustomizeCallback<MessageResponse>(responseCallback,mContext));
    }

    public void DeleteContact(int contactId, ResponseCallback responseCallback){
        Call<MessageResponse> responseCall = IContactService.DeleteContact(contactId);
        responseCall.enqueue(new CustomizeCallback<MessageResponse>(responseCallback,mContext));
    }


}
