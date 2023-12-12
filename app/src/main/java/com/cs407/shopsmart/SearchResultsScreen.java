package com.cs407.shopsmart;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.Chip;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

public class SearchResultsScreen extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SearchResultsAdapter adapter;
    private EditText searchEditText;
    private List<ShoppingCartData> allItems;
    private Chip priceFilterChip;
    private Chip storeFilterChip;
    private Chip distFilterChip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_results_screen);

        recyclerView = findViewById(R.id.recyclerViewSearchResults);
        searchEditText = findViewById(R.id.searchItem);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        allItems = loadItemsFromJson(); // Load the data from JSON
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

        priceFilterChip = findViewById(R.id.priceFilterChip);
        storeFilterChip = findViewById(R.id.storeFilterChip);
        distFilterChip = findViewById(R.id.distFilterChip);

        setupFilterChips();
    }

    private void setupFilterChips() {
        Chip priceFilterChip = findViewById(R.id.priceFilterChip);
        Chip storeFilterChip = findViewById(R.id.storeFilterChip);
        Chip distFilterChip = findViewById(R.id.distFilterChip);

        priceFilterChip.setOnCheckedChangeListener((chip, isChecked) -> {
            if (isChecked) {
                showPriceFilterDialog();
            } else {
                clearPriceFilter();
            }
        });

        storeFilterChip.setOnCheckedChangeListener((chip, isChecked) -> {
            if (isChecked) {
                showStoreFilterDialog();
            } else {
                clearStoreFilter();
            }
        });

        distFilterChip.setOnCheckedChangeListener((chip, isChecked) -> {
            if (isChecked) {
                showDistanceFilterDialog();
            } else {
                clearDistanceFilter();
            }
        });
    }


    private void showPriceFilterDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_price_filter, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        final EditText minPriceInput = dialogView.findViewById(R.id.minPrice);
        final EditText maxPriceInput = dialogView.findViewById(R.id.maxPrice);

        builder.setTitle("Filter by Price")
                .setPositiveButton("Apply", (dialog, id) -> {
                    String minPriceStr = minPriceInput.getText().toString();
                    String maxPriceStr = maxPriceInput.getText().toString();
                    if (!minPriceStr.isEmpty() && !maxPriceStr.isEmpty()) {
                        double minPrice = Double.parseDouble(minPriceStr);
                        double maxPrice = Double.parseDouble(maxPriceStr);
                        applyPriceFilter(minPrice, maxPrice);
                    }
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                    dialog.dismiss();
                    priceFilterChip.setChecked(false);
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showStoreFilterDialog() {
        final String[] stores = {"BestBuy", "Target", "Walmart", "Amazon", "UW Bookstore"};
        final boolean[] checkedStores = new boolean[stores.length];

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Filter by Store")
                .setMultiChoiceItems(stores, checkedStores, (dialog, which, isChecked) -> {
                    checkedStores[which] = isChecked;
                })
                .setPositiveButton("Apply", (dialog, id) -> {
                    List<String> selectedStores = new ArrayList<>();
                    for (int i = 0; i < stores.length; i++) {
                        if (checkedStores[i]) {
                            selectedStores.add(stores[i]);
                        }
                    }
                    applyStoreFilter(selectedStores);
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                    dialog.dismiss();
                    // Uncheck the store filter chip if you have the reference
                    // Example: storeFilterChip.setChecked(false);
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showDistanceFilterDialog() {

    }

    private void performSearch(String query) {
        List<ShoppingCartData> filteredList = new ArrayList<>();
        for (ShoppingCartData item : allItems) {
            if (item.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter.updateList(filteredList);
    }

    private List<ShoppingCartData> loadItemsFromJson() {
        Gson gson = new Gson();
        Type itemListType = new TypeToken<ArrayList<ShoppingCartData>>(){}.getType();
        try {
            InputStreamReader isr = new InputStreamReader(getAssets().open("UWBSData.json"));
            return gson.fromJson(isr, itemListType);
        } catch (IOException e) {
            Toast.makeText(this, "Error loading JSON data: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return new ArrayList<>(); // Return an empty list if there's an error
        }
    }

    private void applyPriceFilter(double minPrice, double maxPrice) {
        List<ShoppingCartData> filteredList = new ArrayList<>();
        for (ShoppingCartData item : allItems) {
            double price = Double.parseDouble(String.valueOf(item.getPrice()));
            if (price >= minPrice && price <= maxPrice) {
                filteredList.add(item);
            }
        }
        adapter.updateList(filteredList);
    }

    private void applyStoreFilter(List<String> selectedStores) {
        List<ShoppingCartData> filteredList = new ArrayList<>();
        for (ShoppingCartData item : allItems) {
            if (selectedStores.contains(item.getStore())) {
                filteredList.add(item);
            }
        }
        adapter.updateList(filteredList);
    }

    private void applyDistanceFilter(int distance) {

    }

    private void clearPriceFilter() {
        adapter.updateList(new ArrayList<>(allItems)); // Assuming allItems is original unfiltered list
    }

    private void clearStoreFilter() {
        adapter.updateList(new ArrayList<>(allItems));
    }

    private void clearDistanceFilter() {
        adapter.updateList(new ArrayList<>(allItems));
    }

}
