package com.cs407.shopsmart;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

/**
 * The initial screen of the application, in our scenario, handles
 * the basic login logic using fragments.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}