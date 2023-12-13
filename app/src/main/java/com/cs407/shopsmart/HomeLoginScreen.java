package com.cs407.shopsmart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * The initial screen of the application, in our scenario, handles
 * the basic login logic.
 */
public class HomeLoginScreen extends AppCompatActivity {

    // UI Elements
    Button loginButton;
    Button createAccountButton;
    Button debugSwitchToSavedShopping;
    Button debugSwitchToMap;
    Button debugSwitchToRegistrationShopSelection;
    Button debugSwitchToHome;
    // SharedPreferences
    SharedPreferences userSession;

    /**
     * Called when the HomeLoginScreen activity is first created. Checks if the user is already
     * logged in. If logged in, redirects the user to the HomeScreen. Otherwise, initializes the
     * user interface elements, such as login and create account buttons, and sets their click listeners.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously
     *                           being shut down, this Bundle contains the data it most
     *                           recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // If user is logged in, direct them to the home screen instead.
        userSession = getSharedPreferences("userSession", MODE_PRIVATE);
        if(checkIfLoggedIn(userSession)){
            startActivity(new Intent(this, HomeScreen.class));
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_login_screen);

        // Initialize UI Elements
        loginButton = findViewById(R.id.LoginButton);
        createAccountButton = findViewById(R.id.CreateAccountButton);
        debugSwitchToSavedShopping = findViewById(R.id.switchtosaved);
        debugSwitchToRegistrationShopSelection = findViewById(R.id.switchtoshopselection);
        debugSwitchToMap = findViewById(R.id.switchtosearch);
        debugSwitchToHome = findViewById(R.id.switchtohome);

        debugSwitchToSavedShopping.setVisibility(View.GONE);
        debugSwitchToHome.setVisibility(View.GONE);
        debugSwitchToRegistrationShopSelection.setVisibility(View.GONE);
        debugSwitchToMap.setVisibility(View.GONE);

        // Add element onClick functions
        loginButton.setOnClickListener(v -> {
            Intent switchToLogin = new Intent(this, LoginScreen.class);
            startActivity(switchToLogin);
        });

        createAccountButton.setOnClickListener(v -> {
            Intent switchToCreateAccount = new Intent(this, RegistrationScreen.class);
            startActivity(switchToCreateAccount);
        });

        debugSwitchToSavedShopping.setOnClickListener(v -> {
            Intent switchToSavedShopping = new Intent(this, SavedShopping.class);
            startActivity(switchToSavedShopping);
        });

        debugSwitchToRegistrationShopSelection.setOnClickListener(v -> {
            Intent switchtoshopselection = new Intent(this, RegistrationShopSelection.class);
            startActivity(switchtoshopselection);
        });

        debugSwitchToMap.setOnClickListener(v -> {
            Intent switchToSearch = new Intent(this, MapScreen.class);
            startActivity(switchToSearch);
        });

        debugSwitchToHome.setOnClickListener(v -> {
            Intent switchToHome = new Intent(this, HomeScreen.class);
            startActivity(switchToHome);
        });
    }

    /**
     * Checks if the user is logged in based on the information stored in SharedPreferences.
     *
     * @param userSession SharedPreferences instance for user session.
     * @return True if the user is logged in; false otherwise.
     */
    private boolean checkIfLoggedIn(SharedPreferences userSession){
        // Upon app startup, check if userSession is true or false, if uninitialized, set to false.
        if(userSession.getAll().size() == 0){ // first time launch, no session.
            SharedPreferences.Editor editor = userSession.edit();
            editor.putString("username", "Guest"); // default user.
            editor.putBoolean("loggedIn", false);
            editor.apply();
        } else { // check if anyone is logged in
            String currentUsername = userSession.getString("username", "Guest");
            boolean loginStatus = userSession.getBoolean("loggedIn", false);
            return !currentUsername.equals("Guest") && loginStatus; // user is not logged in.
        }
        return true;
    }

}