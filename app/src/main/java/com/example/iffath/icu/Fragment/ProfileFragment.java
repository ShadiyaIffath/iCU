package com.example.iffath.icu.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iffath.icu.Activity.LoginActivity;
import com.example.iffath.icu.Callback.ResponseCallback;
import com.example.iffath.icu.DTO.Request.AccountUpdateRequest;
import com.example.iffath.icu.Model.Account;
import com.example.iffath.icu.R;
import com.example.iffath.icu.Service.AccountService;
import com.example.iffath.icu.Storage.SharedPreferenceManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;

import es.dmoral.toasty.Toasty;
import retrofit2.Response;


public class ProfileFragment extends Fragment implements View.OnClickListener {
    TextInputLayout profile_firstName, profile_lastName, profile_address, profile_email, profile_phone, profile_new_password;
    MaterialButton btnProfile,btn_profile_delete;
    TextView profile_title;

    SharedPreferenceManager preferenceManager;
    Account account;
    AccountUpdateRequest editAccount;
    AccountService accountService;
    ResponseCallback updateCallback,deleteCallback;

    public ProfileFragment() {
        // Required empty public constructor
    }
    

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        preferenceManager = SharedPreferenceManager.getInstance(getContext());
        account = preferenceManager.GetAccount();
        accountService = new AccountService();
        createCallbacks();
        //hooks
        profile_title = view.findViewById(R.id.profile_title);
        profile_firstName = view.findViewById(R.id.profile_firstName);
        profile_lastName = view.findViewById(R.id.profile_lastName);
        profile_email = view.findViewById(R.id.profile_email);
        profile_address = view.findViewById(R.id.profile_address);
        profile_phone = view.findViewById(R.id.profile_phone);
        profile_new_password = view.findViewById(R.id.profile_new_password);
        btnProfile = view.findViewById(R.id.btnProfile);
        btn_profile_delete = view.findViewById(R.id.btn_profile_delete);

        String full_name = preferenceManager.GetAccountName();
        profile_title.setText(full_name);
        profile_firstName.getEditText().setText(account.getFirst_name());
        profile_lastName.getEditText().setText(account.getLast_name());
        profile_email.getEditText().setText(account.getEmail());
        profile_address.getEditText().setText(account.getAddress());
        profile_phone.getEditText().setText(String.valueOf(account.getPhone()));

        btnProfile.setOnClickListener(this);
        btn_profile_delete.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.btnProfile){
            updateAccount();
        }else if(view.getId()==R.id.btn_profile_delete){
            deleteAccount();
        }
    }

    private void createCallbacks(){
        updateCallback = new ResponseCallback() {
            @Override
            public void onSuccess(Response response) {
                preferenceManager.UpdateAccount(editAccount);
                account = preferenceManager.GetAccount();
                String name =preferenceManager.GetAccountName();
                setNavigationView(name);
                profile_title.setText(name);
                Toasty.success(getContext(), "Profile successfully updated", Toasty.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String errorMessage) {
                Toasty.error(getContext(), "Server error. Try again later", Toast.LENGTH_SHORT).show();
                profile_firstName.getEditText().setText(account.getFirst_name());
                profile_lastName.getEditText().setText(account.getLast_name());
                profile_email.getEditText().setText(account.getEmail());
                profile_address.getEditText().setText(account.getAddress());
                profile_phone.getEditText().setText(String.valueOf(account.getPhone()));
                profile_new_password.getEditText().setText("");
            }
        };

        deleteCallback = new ResponseCallback() {
            @Override
            public void onSuccess(Response response) {
                Toasty.success(getContext(), "Account has been successfully deleted", Toasty.LENGTH_SHORT).show();
                preferenceManager.Clear();
                startActivity(new Intent(getContext(), LoginActivity.class));
            }

            @Override
            public void onError(String errorMessage) {
                Toasty.error(getContext(), "Server error. Try again later", Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void updateAccount(){
        String fName = profile_firstName.getEditText().getText().toString();
        String lName = profile_lastName.getEditText().getText().toString();
        String email = profile_email.getEditText().getText().toString();
        String phone = profile_phone.getEditText().getText().toString();
        String address = profile_address.getEditText().getText().toString();
        String password = profile_new_password.getEditText().getText().toString();

        if (fName.isEmpty() || lName.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            if (fName.isEmpty()) {
                profile_firstName.setError("Name cannot be blank");
            }
            if (lName.isEmpty()) {
                profile_lastName.setError("Name cannot be blank");
            }
            if (email.isEmpty()) {
                profile_email.setError("E-mail cannot be blank");
            }
            if (phone.isEmpty()) {
                profile_phone.setError("Contact number cannot be blank");
            }
            if(address.isEmpty()){
                profile_address.setError("Address cannot be blank");
            }
            Toasty.error(getContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show();
        } else {
            editAccount = new AccountUpdateRequest(fName,lName,account.getEmail(),address,account.getPassword(),account.getDevice_id(), Integer.parseInt(phone));
            if(!password.isEmpty() || !email.equals(account.getEmail())){
                editAccount.setPassword(password);
                editAccount.setEmail(email);
            }else{
                profile_firstName.setError(null);
                profile_lastName.setError(null);
                profile_firstName.setError(null);
                profile_firstName.setError(null);
                profile_firstName.setError(null);
                profile_firstName.setError(null);
                profile_firstName.setError(null);
                accountService.UpdateAccount(editAccount,account.getId(),updateCallback);
            }
        }
    }

    private  void deleteAccount(){
        final AlertDialog dialogBuilder = new AlertDialog.Builder(this.getContext()).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.confirmation_dialog, null);
        Button confirm =  dialogView.findViewById(R.id.delete_confirm);
        Button cancel =  dialogView.findViewById(R.id.delete_cancel);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
                accountService.DeleteAccount(account.getId(),deleteCallback);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
            }
        });
        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    private void setNavigationView(String name) {
        NavigationView navigationView;
        navigationView = getActivity().findViewById(R.id.navigationHome);
        View navigationHeader = navigationView.getHeaderView(0);
        TextView nameTxt = navigationHeader.findViewById(R.id.logged_user);
        nameTxt.setText(name);
    }
}