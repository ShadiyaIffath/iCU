package com.example.iffath.icu.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.iffath.icu.Model.Account;
import com.example.iffath.icu.R;
import com.example.iffath.icu.storage.SharedPreferenceManager;
import com.google.android.material.textfield.TextInputLayout;

import es.dmoral.toasty.Toasty;


public class ProfileFragment extends Fragment implements View.OnClickListener {
    TextInputLayout profile_firstName, profile_lastName, profile_email, profile_phone, profile_new_password;
    Button btnProfile;

    SharedPreferenceManager preferenceManager;
    Account account,editAccount;

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
        //hooks
        profile_firstName = view.findViewById(R.id.profile_firstName);
        profile_lastName = view.findViewById(R.id.profile_lastName);
        profile_email = view.findViewById(R.id.profile_email);
        profile_phone = view.findViewById(R.id.profile_phone);
        profile_new_password = view.findViewById(R.id.profile_new_password);
        btnProfile = view.findViewById(R.id.btnProfile);

        profile_firstName.getEditText().setText(account.getFirstName());
        profile_lastName.getEditText().setText(account.getLastName());
        profile_email.getEditText().setText(account.getEmail());
        profile_phone.getEditText().setText(String.valueOf(account.getPhone()));

        btnProfile.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        String fName = profile_firstName.getEditText().getText().toString();
        String lName = profile_lastName.getEditText().getText().toString();
        String email = profile_email.getEditText().getText().toString();
        String phone = profile_phone.getEditText().getText().toString();
        String password = profile_new_password.getEditText().getText().toString();

        if (fName.isEmpty() || lName.isEmpty() || email.isEmpty() || phone.isEmpty()) {
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
            Toasty.error(getContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show();
        } else {
            editAccount = new Account(account.getId(), fName,lName,account.getEmail(),account.getPassword(), Integer.parseInt(phone), account.getType());
            if(!password.isEmpty() || !email.equals(account.getEmail())){
                editAccount.setPassword(password);
                editAccount.setEmail(email);
            }else{
            }
        }
    }
}