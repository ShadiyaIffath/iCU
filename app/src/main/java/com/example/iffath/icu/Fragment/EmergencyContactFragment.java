package com.example.iffath.icu.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iffath.icu.Callback.ResponseCallback;
import com.example.iffath.icu.DTO.Request.EmergencyContactRequest;
import com.example.iffath.icu.Model.EmergencyContact;
import com.example.iffath.icu.R;
import com.example.iffath.icu.Service.ContactsService;
import com.example.iffath.icu.Storage.SharedPreferenceManager;
import com.google.android.material.button.MaterialButton;

import es.dmoral.toasty.Toasty;
import retrofit2.Response;

public class EmergencyContactFragment extends Fragment implements View.OnClickListener {
    TextView contact_card_title;
    EditText contact_card_name, contact_card_nickname,contact_card_email,contact_card_phone;
    MaterialButton btnContactBack,btnContactConfirm;
    View view;

    boolean isEdit = false;
    int accountId;
    EmergencyContact contact;
    EmergencyContactRequest contactRequest;
    SharedPreferenceManager preferenceManager;
    ContactsService contactsService;
    ResponseCallback editContactCallback, createContactCallback;

    public EmergencyContactFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_emergency_contact, container, false);
        preferenceManager = SharedPreferenceManager.getInstance(this.getContext());
        accountId = preferenceManager.GetLoggedInUserId();
        contactsService = new ContactsService(getContext());
        createCallBacks();

        //hooks
        contact_card_title = view.findViewById(R.id.contact_card_title);
        contact_card_name = view.findViewById(R.id.contact_card_name);
        contact_card_nickname = view.findViewById(R.id.contact_card_nickname);
        contact_card_email = view.findViewById(R.id.contact_card_email);
        contact_card_phone = view.findViewById(R.id.contact_card_phone);
        btnContactBack = view.findViewById(R.id.btnContactBack);
        btnContactConfirm = view.findViewById(R.id.btnContactConfirm);

        btnContactBack.setOnClickListener(this);
        btnContactConfirm.setOnClickListener(this);

        //data retrieval
        Bundle bundle = this.getArguments();
        if(bundle != null) {
            isEdit = bundle.getBoolean("isEdit");
            if (isEdit) {
                contact_card_title.setText("Edit Contact");
                contact = bundle.getParcelable("Contact");
                assignContactDetails();
            } else {
                contact_card_title.setText("New Contact");
            }
        }
        return view;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btnContactBack:
                navigateToManageContacts();
                break;

            case R.id.btnContactConfirm:
                extractValues();
                break;
        }
    }

    private void assignContactDetails(){
        contact_card_name.setText(contact.getName());
        contact_card_nickname.setText(contact.getNickname());
        contact_card_email.setText(contact.getEmail());
        contact_card_phone.setText(String.valueOf(contact.getPhone()));
    }

    private void navigateToManageContacts(){
        NavDirections action = EmergencyContactFragmentDirections.actionEmergencyContactFragmentToNavigationContacts();
        Navigation.findNavController(view).navigate(action);
    }

    private void extractValues(){
        String name = contact_card_name.getText().toString();
        String nickname = contact_card_nickname.getText().toString();
        String email = contact_card_email.getText().toString();
        String phone  = contact_card_phone.getText().toString();
        boolean valid  = true;

        if(name.isEmpty()){
            valid = false;
            contact_card_name.setError("This field cannot be null");
        }
        if(phone.isEmpty()){
            valid = false;
            contact_card_phone.setError("This field cannot be null");
        }
        if(email.isEmpty()){
            valid = false;
            contact_card_email.setError("This field cannot be null");
        }

        if(valid){
            contactRequest = new EmergencyContactRequest();
            contactRequest.setName(name);
            contactRequest.setNickname(nickname);
            contactRequest.setEmail(email);
            contactRequest.setPhone(Integer.parseInt(phone));
            contactRequest.setAccount_id(accountId);
            if(isEdit){
                contactsService.EditContact(contactRequest,contact.getId(),editContactCallback);
            }else {
                contactsService.AddContact(contactRequest, createContactCallback);
            }
        }else{
            Toasty.error(getContext(),"Request denied! Empty fields",Toasty.LENGTH_SHORT).show();
        }
    }

    private void createCallBacks(){
        createContactCallback = new ResponseCallback() {
            @Override
            public void onSuccess(Response response) {
                Toasty.success(getContext(), "Contact created",Toasty.LENGTH_SHORT).show();
                navigateToManageContacts();
            }

            @Override
            public void onError(String errorMessage) {
                if(errorMessage.equals("ICU002")){
                    Toasty.error(getContext(), "E-mail is in use by another contact", Toast.LENGTH_SHORT).show();
                    contact_card_email.setError("E-mail is already used");
                }else {
                    Toasty.error(getContext(), "Server error. Try again later", Toast.LENGTH_SHORT).show();
                }
            }
        };

        editContactCallback = new ResponseCallback() {
            @Override
            public void onSuccess(Response response) {
                Toasty.success(getContext(), "Contact updated",Toasty.LENGTH_SHORT).show();
                navigateToManageContacts();
            }

            @Override
            public void onError(String errorMessage) {
                Toasty.error(getContext(), "Server error. Try again later", Toast.LENGTH_SHORT).show();
            }
        };
    }
}