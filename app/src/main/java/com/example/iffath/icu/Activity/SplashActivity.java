package com.example.iffath.icu.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.iffath.icu.R;

public class SplashActivity extends AppCompatActivity {
    private static int SPLASH_DURATION = 3000;
    Animation topAnime, bottomAnime;
    ImageView splash;
    TextView logo, slogan;
    Intent intent;

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
}