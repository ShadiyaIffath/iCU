package com.example.iffath.icu.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iffath.icu.R;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    TextInputLayout email,password;
    ImageView splash;
    TextView logo, slogan;
    Button signIn, callRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

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
//        if (valid) {
//            LoginRequest loginRequest = new LoginRequest(mail, pw);
//            authenticationService.login(loginRequest, this);
//        } else {
//            Toasty.error(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
//        }
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
}