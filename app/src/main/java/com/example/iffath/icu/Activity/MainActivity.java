package com.example.iffath.icu.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.iffath.icu.R;
import com.example.iffath.icu.Storage.SharedPreferenceManager;
import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    String name;
    SharedPreferenceManager preferenceManager;

    TextView nameTxt;
    AppBarConfiguration appBarConfiguration;
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        drawer = findViewById(R.id.main_activity);
        this.navigationView = findViewById(R.id.navigationHome);
        View navigationHeader = this.navigationView.getHeaderView(0);
        nameTxt = navigationHeader.findViewById(R.id.logged_user);

        //get logged in users name
        preferenceManager = SharedPreferenceManager.getInstance( this);
        name = preferenceManager.GetAccountName();
        nameTxt.setText(name);

        // The custom toolbar replaces the action bar
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar())
                .setDisplayHomeAsUpEnabled ( true );

        appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_home)
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //logout click event
        navigationView.getMenu().findItem(R.id.nav_logout).setOnMenuItemClickListener(menuItem -> {
            logout();
            return true;
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void logout() {
        preferenceManager.Clear();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }
}