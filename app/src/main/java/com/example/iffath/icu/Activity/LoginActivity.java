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
import com.example.iffath.icu.DTO.Request.LoginRequest;
import com.example.iffath.icu.DTO.Request.FirebaseRegisterIdRequest;
import com.example.iffath.icu.DTO.Response.LoginResponse;
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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, ResponseCallback {
    private final String TAG = "LOGIN";
    TextInputLayout email,password;
    ImageView splash;
    TextView logo, slogan;
    Button signIn, callRegister;

    AuthenticationService authenticationService;
    SharedPreferenceManager preferenceManager;
    AccountService accountService;
    String deviceToken;
    ResponseCallback deviceTokenCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        authenticationService = new AuthenticationService(this);

        //hooks
        splash = findViewById(R.id.login_image);
        logo = findViewById(R.id.login_welcome);
        slogan = findViewById(R.id.login_message);
        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);
        callRegister = findViewById(R.id.btnGetRegistered);
        signIn = findViewById(R.id.btnLogin);
        signIn.setOnClickListener(this);
        callRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btnLogin:
                SignIn();
                break;
            case R.id.btnGetRegistered:
                navigateToRegister();
                break;
        }
    }

    @Override
    public void onSuccess(Response response) {
        LoginResponse loginResponse = (LoginResponse) response.body();
        if(loginResponse != null) {
            storeLoggedInUser(loginResponse);
            navigateToHome(loginResponse.getAccount().getFirst_name(),loginResponse.getAccount().getLast_name());
        }
    }

    @Override
    public void onError(String errorMessage) {
        if(errorMessage.equals("ICU001")) {
            Toasty.error(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
        }else{
            Toasty.error(this, "Server error. Try again later", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onUserInteraction() {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void SignIn() {
        String mail = email.getEditText().getText().toString();
        String pw = password.getEditText().getText().toString();
        boolean valid = true;
        if (mail.isEmpty()) {
            valid = false;
            email.setError("E-mail cannot be blank");
        }
        if (pw.isEmpty()) {
            valid = false;
            password.setError("Password cannot be blank");
        }
        if (valid) {
            LoginRequest loginRequest = new LoginRequest(mail, pw);
            authenticationService.login(loginRequest, this);
        } else {
            Toasty.error(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToHome(String fName, String lName){
        Toast.makeText(this, "Welcome " + fName + " " + lName,
                Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.overridePendingTransition(0, 0);
    }

    private void navigateToRegister(){
        Intent intent = new Intent(this, RegisterActivity.class);

        //Transition of title and logo
        Pair[] pairs =new Pair[7];
        pairs[0] = new Pair<View,String>(splash,"logo_image");
        pairs[1] = new Pair<View,String>(logo,"logo_title");
        pairs[2] = new Pair<View,String>(slogan,"logo_slogan");
        pairs[3] = new Pair<View,String>(email,"email_trans");
        pairs[4] = new Pair<View,String>(password,"password_trans");
        pairs[5] = new Pair<View,String>(callRegister,"login_reg_Btntransition");
        pairs[6] = new Pair<View,String>(signIn,"confirm_transition");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, pairs);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent,options.toBundle());
        }
    }

    private void storeLoggedInUser(LoginResponse response){
        // inside of an Activity, `getString` is called directly
        preferenceManager = SharedPreferenceManager.getInstance(this);
        preferenceManager.StoreAccountDetails(response.getAccount());

        if(response.getCamera() != null){
            preferenceManager.StoreDeviceDetails(response.getCamera());
        }
        registerDeviceToken();
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
}