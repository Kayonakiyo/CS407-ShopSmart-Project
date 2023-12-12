package com.cs407.shopsmart;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.io.IOException;
import java.util.Scanner;

public class SearchResultsScreen extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SearchResultsAdapter adapter;
    private EditText searchEditText;
    private List<ShoppingCartData> allItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_results_screen);

        recyclerView = findViewById(R.id.recyclerViewSearchResults);
        searchEditText = findViewById(R.id.searchItem);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //allItems = loadItemsFromJson(); // Load the data from JSON
        adapter = new SearchResultsAdapter(new ArrayList<>()); // Initialize with an empty list
        recyclerView.setAdapter(adapter);

        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch(searchEditText.getText().toString());
                    return true;
                }
                return false;
            }
        });

        Intent intent = getIntent();
        String homeQuery = intent.getStringExtra("searchQuery");
        if(homeQuery != null) {
            performSearch(homeQuery);
        }
    }

    private void performSearch(String query) {
        List<ShoppingCartData> filteredList = new ArrayList<>();
        ArrayList<ShoppingCartData> queriedData = new ArrayList<>();
        try{
            URL url = new URL("http://api.resolyth.dev");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type","application/json");
            connection.setRequestProperty("Accept", "application/json");
            String payload = "{\"query\":\"" + query + "\"}";// This should be your json body i.e. {"Name" : "Mohsin"}
            byte[] out = payload.getBytes(StandardCharsets.UTF_8);
            OutputStream stream = connection.getOutputStream();
            stream.write(out);
            String s = new String(out, StandardCharsets.UTF_8);
            queriedData = (ArrayList<ShoppingCartData>) loadItemsFromJson(s);
            System.out.println(connection.getResponseCode() + " " + connection.getResponseMessage()); // THis is optional
            connection.disconnect();
        }catch (Exception e){
            System.out.println(e);
            System.out.println("Failed successfully");
        }


        for (ShoppingCartData item : queriedData) {
            if (item.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter.updateList(filteredList);
    }

    private List<ShoppingCartData> loadItemsFromJson(String rawData) {
        Gson gson = new Gson();
        Type itemListType = new TypeToken<ArrayList<ShoppingCartData>>(){}.getType();
        try {
            Scanner scnr = new Scanner(rawData);
            return gson.fromJson(rawData, itemListType);
        } catch (JsonSyntaxException e) {
            Toast.makeText(this, "Error loading JSON data: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return new ArrayList<>(); // Return an empty list if there's an error
        }
    }
}
