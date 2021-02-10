package com.example.iffath.icu.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iffath.icu.Callback.ResponseCallback;
import com.example.iffath.icu.DTO.Request.FirebaseRegisterIdRequest;
import com.example.iffath.icu.DTO.Request.RegisterRequest;
import com.example.iffath.icu.DTO.Response.RegisterResponse;
import com.example.iffath.icu.R;
import com.example.iffath.icu.Service.AccountService;
import com.example.iffath.icu.Service.AuthenticationService;
import com.example.iffath.icu.Storage.SharedPreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.messaging.FirebaseMessaging;

import es.dmoral.toasty.Toasty;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, ResponseCallback {
    private final String TAG = "REGISTER";
    TextInputLayout firstName,lastName,email,passTxt,addressTxt, numberTxt;
    ImageView splash;
    TextView logo, slogan;
    Button callSignIn, register;

    RegisterRequest account;
    String deviceToken;
    ResponseCallback deviceTokenCallback;
    SharedPreferenceManager preferenceManager;
    AuthenticationService authenticationService;
    AccountService accountService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);

        //hooks
        splash = findViewById(R.id.register_logo);
        logo = findViewById(R.id.register_welcome);
        slogan = findViewById(R.id.register_message);
        firstName = findViewById(R.id.register_firstName);
        lastName = findViewById(R.id.register_lastName);
        numberTxt = findViewById(R.id.register_phone);
        addressTxt = findViewById(R.id.register_address);
        email = findViewById(R.id.register_email);
        passTxt = findViewById(R.id.register_password);
        register = findViewById(R.id.btnRegister);
        callSignIn = findViewById(R.id.btnGetLoggedIn);
        register.setOnClickListener(this);
        callSignIn.setOnClickListener(this);

        authenticationService = new AuthenticationService(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnRegister:
                register();
                break;
            case R.id.btnGetLoggedIn:
                navigateToLogin();
                break;
            default:
                break;
        }
    }

    @Override
    public void onSuccess(Response response) {
        RegisterResponse registerResponse = (RegisterResponse) response.body();
        if(registerResponse != null) {
            storeRegisteredUser(registerResponse);
            registerDeviceToken();
            navigateToHome(registerResponse.getFirst_name(),registerResponse.getLast_name());
        }
    }

    @Override
    public void onError(String errorMessage) {
        if(errorMessage.equals("ICU002")) {
            Toasty.error(this, "E-mail is in use", Toast.LENGTH_SHORT).show();
            email.setError("E-mail is already used");
        }else{
            Toasty.error(this, "Registration failed, try again later", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onUserInteraction() {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void register() {
        String fName = firstName.getEditText().getText().toString();
        String lName = lastName.getEditText().getText().toString();
        String number = numberTxt.getEditText().getText().toString();
        String mail = email.getEditText().getText().toString();
        String password = passTxt.getEditText().getText().toString();
        String address = addressTxt.getEditText().getText().toString();

        if (fName.isEmpty() || lName.isEmpty() || mail.isEmpty() || number.isEmpty() || address.isEmpty()|| password.isEmpty()) {
            if (fName.isEmpty()) {
                firstName.setError("First name cannot be blank");
            }
            if (lName.isEmpty()) {
                lastName.setError("Last name cannot be blank");
            }
            if (password.isEmpty()) {
                passTxt.setError("Password cannot be blank");
            }
            if (mail.isEmpty()) {
                email.setError("E-mail cannot be blank");
            }
            if(address.isEmpty()){
                addressTxt.setError("Address cannot be blank");
            }
            if (number.isEmpty()) {
                numberTxt.setError("Contact number cannot be blank");
            }
            Toasty.error(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
        } else {
            account = new RegisterRequest(fName, lName, mail, password,address,"", Integer.parseInt(number));
            authenticationService.register(account,this);
        }
    }

    private void navigateToHome(String fName, String lName) {
        Toast.makeText(this, "Welcome " + fName + " " + lName,
                Toast.LENGTH_SHORT).show();
        Intent intent;
        intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.overridePendingTransition(0, 0);
    }

    private void navigateToLogin(){
        Intent intent = new Intent(this,LoginActivity.class);

        //Transition of title and logo
        Pair[] pairs =new Pair[7];
        pairs[0] = new Pair<View,String>(splash,"logo_image");
        pairs[1] = new Pair<View,String>(logo,"logo_title");
        pairs[2] = new Pair<View,String>(slogan,"logo_slogan");
        pairs[3] = new Pair<View,String>(email,"email_trans");
        pairs[4] = new Pair<View,String>(passTxt,"password_trans");
        pairs[5] = new Pair<View,String>(callSignIn,"login_reg_Btntransition");
        pairs[6] = new Pair<View,String>(register,"confirm_transition");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(RegisterActivity.this, pairs);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent,options.toBundle());
        }
    }

    private void storeRegisteredUser(RegisterResponse response){
        preferenceManager = SharedPreferenceManager.getInstance(this);
        preferenceManager.StoreRegisteredAccountDetails(response);
    }

    private void createCallback(){
        deviceTokenCallback = new ResponseCallback() {
            @Override
            public void onSuccess(Response response) {
                preferenceManager.StorePushNotificationToken(deviceToken);
                Log.d(TAG,"Device token saved");
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG,errorMessage);
            }
        };
    }

    private void registerDeviceToken(){
        createCallback();
        accountService = new AccountService(this);
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        // Get new FCM registration token
                        deviceToken = task.getResult();
                        accountService.RegisterDevice(new FirebaseRegisterIdRequest(preferenceManager.GetLoggedInUserId(), deviceToken), deviceTokenCallback);
                    }
                });
    }

}