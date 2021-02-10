package com.example.iffath.icu.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iffath.icu.Callback.ResponseCallback;
import com.example.iffath.icu.DTO.Request.FirebaseRegisterIdRequest;
import com.example.iffath.icu.R;
import com.example.iffath.icu.Service.AccountService;
import com.example.iffath.icu.Storage.SharedPreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {
    private static int SPLASH_DURATION = 3000;
    private final String TAG = "SPLASH";
    Animation topAnime, bottomAnime;
    ImageView splash;
    TextView logo, slogan;
    Intent intent;

    AccountService accountService;
    ResponseCallback deviceTokenCallback;
    SharedPreferenceManager preferenceManager;
    String deviceToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        //get Animation
        topAnime = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnime = AnimationUtils.loadAnimation(this, R.anim.bottom_top_animation);

        //hooks
        splash = findViewById(R.id.splashImage);
        logo = findViewById(R.id.splashTitle);
        slogan = findViewById(R.id.splashSlogan);

        //set animation
        splash.setAnimation(topAnime);
        logo.setAnimation(bottomAnime);
        slogan.setAnimation(bottomAnime);

        preferenceManager = SharedPreferenceManager.getInstance( this);
        boolean isLogged = preferenceManager.IsLoggedIn();
        if (isLogged) {
            accountService = new AccountService(getApplicationContext());
            createCallback();
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
            navigateToHome();
        }else{
            navigateToLogin();
        }
    }

    private void navigateToLogin(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                intent = new Intent(SplashActivity.this, LoginActivity.class);

                //Transition of title and logo
                Pair[] pairs = new Pair[2];
                pairs[0] = new Pair<View, String>(splash, "logo_image");
                pairs[1] = new Pair<View, String>(logo, "logo_title");

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SplashActivity.this, pairs);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent, options.toBundle());
                }
            }
        }, SPLASH_DURATION);
    }

    private void navigateToHome(){
        String name = preferenceManager.GetAccountName();
        Toast.makeText(this, "Welcome " + name,  Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }, SPLASH_DURATION);
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