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
import com.example.iffath.icu.storage.SharedPreferenceManager;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{
    TextInputLayout firstName,lastName,email,passTxt, numberTxt;
    ImageView splash;
    TextView logo, slogan;
    Button callSignIn, register;
    SharedPreferenceManager preferenceManager;
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
        email = findViewById(R.id.register_email);
        passTxt = findViewById(R.id.register_password);
        register = findViewById(R.id.btnRegister);
        callSignIn = findViewById(R.id.btnGetLoggedIn);
        register.setOnClickListener(this);
        callSignIn.setOnClickListener(this);
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

    private void register() {
        String fName = firstName.getEditText().getText().toString();
        String lName = lastName.getEditText().getText().toString();
        String number = numberTxt.getEditText().getText().toString();
        String mail = email.getEditText().getText().toString();
        String password = passTxt.getEditText().getText().toString();

        if (fName.isEmpty() || lName.isEmpty() || mail.isEmpty() || number.isEmpty() || password.isEmpty()) {
            if (fName.isEmpty()) {
                firstName.setError("Name cannot be blank");
            }
            if (lName.isEmpty()) {
                lastName.setError("Name cannot be blank");
            }
            if (password.isEmpty()) {
                passTxt.setError("Password cannot be blank");
            }
            if (mail.isEmpty()) {
                email.setError("E-mail cannot be blank");
            }
            if (number.isEmpty()) {
                numberTxt.setError("Contact number cannot be blank");
            }
//            Toasty.error(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
        } else {
//            account = new Account(fName, lName, mail , password, Integer.parseInt(number));
//            authenticationService.register(account,this);
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

}