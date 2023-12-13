package com.cs407.shopsmart;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Activity for selecting preferred stores during user registration.
 * Allows users to choose their preferred stores by interacting with StoreCardView elements.
 */
public class RegistrationShopSelection extends AppCompatActivity {

    SharedPreferences userPreferences;
    SharedPreferences userSession;

    Button storeSelectButton;

    /**
     * Initializes the shop selection screen and sets up UI elements and click listeners.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously
     *                           being shut down, this Bundle contains the data it most
     *                           recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_shop_selection);

        userPreferences = getSharedPreferences("userPreferences", MODE_PRIVATE);
        userSession = getSharedPreferences("userSession", MODE_PRIVATE);

        // Shows the nav arrow on top of screen
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        storeSelectButton = findViewById(R.id.storeSelectButton);
        storeSelectButton.setOnClickListener(view -> startActivity(new Intent(this, HomeScreen.class)));

        StoreCardView bookstoreStoreCardView = findViewById(R.id.bookstoreStoreCardView);
        bookstoreStoreCardView.setStoreName("UW Bookstore");
        bookstoreStoreCardView.setStoreImage(R.drawable.universitybookstore);
        bookstoreStoreCardView.setOnClickListener(v -> {
            String currentUser = userSession.getString("username", "Guest");

            if(userPreferences.getString(currentUser, "Guest").contains("UW Bookstore")){ // if they have already selected this, de-select it
                String[] previousPrefs = userPreferences.getString(currentUser, "Guest").split(",");
                ArrayList<String> prevPrefs = (ArrayList<String>) Arrays.asList(previousPrefs);
                prevPrefs.remove("UW Bookstore");
                SharedPreferences.Editor editor = userPreferences.edit();
                editor.putString(userSession.getString(currentUser, "Guest"), String.join(",", prevPrefs)); // save new store choices
                editor.apply();
                return; // done de-selecting!
            }
            SharedPreferences.Editor editor = userPreferences.edit();
            String[] previousPrefs = userPreferences.getString(currentUser, "Guest").split(",");
            ArrayList<String> prevPrefs = (ArrayList<String>) Arrays.asList(previousPrefs);
            prevPrefs.add("UW Bookstore");
            editor.putString(userSession.getString(currentUser, "Guest"), String.join(",", prevPrefs)); // add this new store
            editor.apply();
        });

        StoreCardView amazonStoreCardView = findViewById(R.id.amazonStoreCardView);
        amazonStoreCardView.setStoreName("Amazon");
        amazonStoreCardView.setStoreImage(R.drawable.amazon);
        bookstoreStoreCardView.setOnClickListener(v -> {
            String currentUser = userSession.getString("username", "Guest");

            if(userPreferences.getString(currentUser, "Guest").contains("Amazon")){ // if they have already selected this, de-select it
                String[] previousPrefs = userPreferences.getString(currentUser, "Guest").split(",");
                ArrayList<String> prevPrefs = (ArrayList<String>) Arrays.asList(previousPrefs);
                prevPrefs.remove("Amazon");
                SharedPreferences.Editor editor = userPreferences.edit();
                editor.putString(userSession.getString(currentUser, "Guest"), String.join(",", prevPrefs)); // save new store choices
                editor.apply();
                return; // done de-selecting!
            }
            SharedPreferences.Editor editor = userPreferences.edit();
            String[] previousPrefs = userPreferences.getString(currentUser, "Guest").split(",");
            ArrayList<String> prevPrefs = (ArrayList<String>) Arrays.asList(previousPrefs);
            prevPrefs.add("Amazon");
            editor.putString(userSession.getString(currentUser, "Guest"), String.join(",", prevPrefs)); // add this new store
            editor.apply();
        });

        StoreCardView targetStoreCardView = findViewById(R.id.targetStoreCardView);
        targetStoreCardView.setStoreName("Target");
        targetStoreCardView.setStoreImage(R.drawable.target);

        StoreCardView bbStoreCardView = findViewById(R.id.bbStoreCardView);
        bbStoreCardView.setStoreName("Best Buy");
        bbStoreCardView.setStoreImage(R.drawable.bestbuy);

        StoreCardView walmartStoreCardView = findViewById(R.id.walmartStoreCardView);
        walmartStoreCardView.setStoreName("Walmart");
        walmartStoreCardView.setStoreImage(R.drawable.walmart);

        // have a single button to apply all changes
    }

    /**
     * Handles navigating back to the previous activity.
     *
     * @param item The menu item that was selected.
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if (id == android.R.id.home){
            navigateUpTo(new Intent(this, RegistrationScreen.class));
            return true;
        }
        return false;
    }
}