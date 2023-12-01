package com.cs407.shopsmart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

/**
 * The initial screen of the application, in our scenario, handles
 * the basic login logic.
 */
public class HomeLoginScreen extends AppCompatActivity {

    // UI Elements
    Button loginButton;
    Button createAccountButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_login_screen);

        // Initialize UI Elements
        loginButton = findViewById(R.id.LoginButton);
        createAccountButton = findViewById(R.id.CreateAccountButton);

        // Add element onClick functions
        loginButton.setOnClickListener(v -> {
            Intent switchToLogin = new Intent(this, LoginScreen.class);
            startActivity(switchToLogin);
        });

        createAccountButton.setOnClickListener(v -> {
            Intent switchToCreateAccount = new Intent(this, RegistrationScreen.class);
            startActivity(switchToCreateAccount);
        });



    }
}