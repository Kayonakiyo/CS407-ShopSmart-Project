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
    Button debugSwtichToSearchResults;
    Button debugSwitchToRegistrationShopSelection;
    Button debugSwitchToHome;
    // SharedPreferences
    SharedPreferences userSession;

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
        debugSwtichToSearchResults = findViewById(R.id.switchtosearch);
        debugSwitchToHome = findViewById(R.id.switchtohome);

        debugSwitchToSavedShopping.setVisibility(View.GONE);

        debugSwtichToSearchResults.setVisibility(View.GONE);
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

        debugSwtichToSearchResults.setOnClickListener(v -> {
            Intent switchToSearch = new Intent(this, SearchResultsScreen.class);
            startActivity(switchToSearch);
        });

        debugSwitchToHome.setOnClickListener(v -> {
            Intent switchToHome = new Intent(this, HomeScreen.class);
            startActivity(switchToHome);
        });
    }

    /**
     * Checks if a user is currently logged in.
     *
     * @param userSession the database that holds session data.
     * @return false if not logged in, true if logged in
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