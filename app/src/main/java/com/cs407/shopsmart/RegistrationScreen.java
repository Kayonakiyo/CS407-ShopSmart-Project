package com.cs407.shopsmart;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class RegistrationScreen extends AppCompatActivity {

    EditText username;
    EditText password;
    EditText confirmPassword;
    Button registerButton;
    SharedPreferences loginDatabase;
    SharedPreferences userSession; // holds who is logged in right now
    SecureRandom randomGen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_screen);

        // Shows the nav arrow on top of screen
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        randomGen = new SecureRandom();
        username = findViewById(R.id.editTextUsername);
        password = findViewById(R.id.editTextPassword);
        confirmPassword = findViewById(R.id.editTextConfirmPassword);
        registerButton = findViewById(R.id.buttonRegister);

        loginDatabase = getSharedPreferences("loginDatabase", MODE_PRIVATE);
        userSession = getSharedPreferences("userSession", MODE_PRIVATE);

        registerButton.setOnClickListener(v -> {
            if(handleRegistration(username.getText().toString(), password.getText().toString(), confirmPassword.getText().toString(), password, confirmPassword)){
                Toast.makeText(this, "Successfully registered user! You may log in now.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, HomeLoginScreen.class));
            }
        });

    }


    /**
     * Deals with navigating back by one activity.
     *
     * @param item The menu item that was selected.
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            navigateUpTo(new Intent(this, HomeLoginScreen.class));
            return true;
        }
        return false;
    }



    private boolean handleRegistration(String username, String password, String confirmPassword, EditText passwordField, EditText repPasswordField){
        if(username == null || username.trim().length() == 0){
            Toast.makeText(this, "Please fill in the username field!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password == null || password.trim().length() == 0){
            Toast.makeText(this, "Please fill in the password field!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(confirmPassword == null || confirmPassword.trim().length() == 0){
            Toast.makeText(this, "Please enter your repeated password!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!password.equals(confirmPassword)){
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            passwordField.setText("");
            repPasswordField.setText("");
            return false;
        }

        Map<String, ?> keyValues = loginDatabase.getAll();
        for (Map.Entry<String, ?> keyValuePair : keyValues.entrySet()){
            if(username.equals(keyValuePair.getKey().toString())){
                Toast.makeText(this, "Username already registered!", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        // All checks passed! Add user and their hashed passwords+salt to database.
        byte[] saltBytes = new byte[20];
        randomGen.nextBytes(saltBytes);
        SecretKey hashedPassword = null;
        String encodedKeyAndSalt = "[";
        String saltString = Base64.getEncoder().encodeToString(saltBytes);
        try{
            hashedPassword = pbkdf2(password.toCharArray(), saltBytes, 4096, 256);
            encodedKeyAndSalt += Base64.getEncoder().encodeToString(hashedPassword.getEncoded()) + ",";
        } catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        } catch (InvalidKeySpecException e){
            e.printStackTrace();
        }
        encodedKeyAndSalt += saltString + "]";
        SharedPreferences.Editor editor = loginDatabase.edit();
        editor.putString(username, encodedKeyAndSalt); // save the hashed password and salt as an 'array', can be deserialized easily.
        editor.apply();

        // After successful registration, this user is now logged in and session reflects that.
        editor = userSession.edit();
        editor.putString("username", username);
        editor.putBoolean("loggedIn", true);
        editor.apply();

        return true;
    }

    /**
     * A helper function that can take in a password, a random salt, and allows the user
     * to hash their password to be placed into the database safely.
     *
     * @param password the users plaintext password
     * @param salt a randomly generated byte array to include with the plaintext password
     * @param iterationCount defaulted to 4096
     * @param keyLength defaulted to 256
     * @return the result of the password+salt, hashed!
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static SecretKey pbkdf2(char[] password, byte[] salt, int iterationCount, int keyLength) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterationCount, keyLength);
        return factory.generateSecret(spec);
    }
}