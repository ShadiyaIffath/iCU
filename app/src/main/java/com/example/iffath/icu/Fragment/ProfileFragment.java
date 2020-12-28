package com.example.iffath.icu.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iffath.icu.Callback.ResponseCallback;
import com.example.iffath.icu.DTO.Request.AccountUpdateRequest;
import com.example.iffath.icu.Model.Account;
import com.example.iffath.icu.R;
import com.example.iffath.icu.Service.AccountService;
import com.example.iffath.icu.Storage.SharedPreferenceManager;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;

import es.dmoral.toasty.Toasty;
import retrofit2.Response;


public class ProfileFragment extends Fragment implements View.OnClickListener, ResponseCallback {
    TextInputLayout profile_firstName, profile_lastName, profile_email, profile_phone, profile_new_password;
    Button btnProfile;
    TextView profile_title;

    SharedPreferenceManager preferenceManager;
    Account account;
    AccountUpdateRequest editAccount;
    AccountService accountService;

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
        //hooks
        profile_title = view.findViewById(R.id.profile_title);
        profile_firstName = view.findViewById(R.id.profile_firstName);
        profile_lastName = view.findViewById(R.id.profile_lastName);
        profile_email = view.findViewById(R.id.profile_email);
        profile_phone = view.findViewById(R.id.profile_phone);
        profile_new_password = view.findViewById(R.id.profile_new_password);
        btnProfile = view.findViewById(R.id.btnProfile);

        String full_name = preferenceManager.GetAccountName();
        profile_title.setText(full_name);
        profile_firstName.getEditText().setText(account.getFirst_name());
        profile_lastName.getEditText().setText(account.getLast_name());
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
            editAccount = new AccountUpdateRequest(fName,lName,account.getEmail(),account.getPassword(), Integer.parseInt(phone));
            if(!password.isEmpty() || !email.equals(account.getEmail())){
                editAccount.setPassword(password);
                editAccount.setEmail(email);
            }else{
                accountService.UpdateAccount(editAccount,account.getId(),this);
            }
        }
    }

    @Override
    public void onSuccess(Response response) {
        preferenceManager.updateAccount(editAccount);
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
        profile_phone.getEditText().setText(String.valueOf(account.getPhone()));
        profile_new_password.getEditText().setText("");
    }

    private void setNavigationView(String name) {
        NavigationView navigationView;
        navigationView = getActivity().findViewById(R.id.navigationHome);
        View navigationHeader = navigationView.getHeaderView(0);
        TextView nameTxt = navigationHeader.findViewById(R.id.logged_user);
        nameTxt.setText(name);
    }
}