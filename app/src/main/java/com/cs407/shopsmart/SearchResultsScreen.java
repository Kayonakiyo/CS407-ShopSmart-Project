package com.cs407.shopsmart;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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

        // Setup the RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        allItems = loadItemsFromJson(); // Load the data from JSON
        if (allItems != null) {
            adapter = new SearchResultsAdapter(allItems);
            recyclerView.setAdapter(adapter);
        } else {
            // Show a toast if the items are null (i.e., JSON loading failed)
            Toast.makeText(this, "Failed to load items.", Toast.LENGTH_LONG).show();
        }

        // Setup the search bar
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Nothing needed here
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // As the user types in the search bar, filter the adapter's data
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Nothing needed here
            }
        });

        // TODO: Setup the sorting ChipGroup with onClickListeners
    }

    private List<ShoppingCartData> loadItemsFromJson() {
        Gson gson = new Gson();
        Type itemListType = new TypeToken<ArrayList<ShoppingCartData>>(){}.getType();
        try {
            InputStreamReader isr = new InputStreamReader(getAssets().open("UWBSData.json"));
            return gson.fromJson(isr, itemListType);
        } catch (IOException e) {
            // Handling IOException by showing a Toast message
            Toast.makeText(this, "Error loading JSON data: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return null; // Return null if there's an error
        }
    }
}
