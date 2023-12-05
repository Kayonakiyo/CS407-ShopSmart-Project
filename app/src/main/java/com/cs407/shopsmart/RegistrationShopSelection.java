package com.cs407.shopsmart;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

public class RegistrationShopSelection extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_shop_selection);

        // Shows the nav arrow on top of screen
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        StoreCardView bookstoreStoreCardView = findViewById(R.id.bookstoreStoreCardView);
        bookstoreStoreCardView.setStoreName("UW Bookstore");
        bookstoreStoreCardView.setStoreImage(R.drawable.universitybookstore);

        StoreCardView amazonStoreCardView = findViewById(R.id.amazonStoreCardView);
        amazonStoreCardView.setStoreName("Amazon");
        amazonStoreCardView.setStoreImage(R.drawable.amazon);

        StoreCardView targetStoreCardView = findViewById(R.id.targetStoreCardView);
        targetStoreCardView.setStoreName("Target");
        targetStoreCardView.setStoreImage(R.drawable.target);

        StoreCardView bbStoreCardView = findViewById(R.id.bbStoreCardView);
        bbStoreCardView.setStoreName("Best Buy");
        bbStoreCardView.setStoreImage(R.drawable.bestbuy);

        StoreCardView walmartStoreCardView = findViewById(R.id.walmartStoreCardView);
        walmartStoreCardView.setStoreName("Walmart");
        walmartStoreCardView.setStoreImage(R.drawable.walmart);
    }

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