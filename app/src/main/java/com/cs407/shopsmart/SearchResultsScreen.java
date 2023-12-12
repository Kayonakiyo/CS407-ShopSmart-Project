package com.cs407.shopsmart;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.Chip;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;


public class SearchResultsScreen extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SearchResultsAdapter adapter;
    private EditText searchEditText;
    private List<ShoppingCartData> allItems;
    private Chip priceFilterChip;
    private Chip storeFilterChip;
    private Chip distFilterChip;

    private Double minPriceFilter = null;
    private Double maxPriceFilter = null;
    private List<String> selectedStoresFilter = new ArrayList<>();

    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_results_screen);
// Shows the nav arrow on top of screen
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Create a single-threaded ExecutorService
        executorService = Executors.newSingleThreadExecutor();

        recyclerView = findViewById(R.id.recyclerViewSearchResults);
        searchEditText = findViewById(R.id.searchItem);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //allItems = loadItemsFromJson(); // Load the data from JSON
        adapter = new SearchResultsAdapter(new ArrayList<>()); // Initialize with an empty list
        recyclerView.setAdapter(adapter);

        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    // Execute network operation in the executor
                    executorService.execute(() -> performSearch(searchEditText.getText().toString()));

                    // Hide the keyboard and clear focus from the EditText
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
                    searchEditText.clearFocus(); // Clear focus to stop cursor blinking

                    return true;
                }
                return false;
            }
        });



        Intent intent = getIntent();
        String homeQuery = intent.getStringExtra("searchQuery");
        if(homeQuery != null) {
            // Execute network operation in the executor
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    performSearch(homeQuery);
                }
            });

        }
        priceFilterChip = findViewById(R.id.priceFilterChip);
        storeFilterChip = findViewById(R.id.storeFilterChip);
        distFilterChip = findViewById(R.id.distFilterChip);

        setupFilterChips();
    }

    private void setupFilterChips() {
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
                    clearPriceFilter();
                })
                .setOnCancelListener(dialogInterface -> {
                    priceFilterChip.setChecked(false);
                    clearPriceFilter();
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showStoreFilterDialog() {
        final String[] stores = {"Best Buy", "Target", "Walmart", "Amazon", "UW Bookstore"};
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
                    storeFilterChip.setChecked(false);
                    clearPriceFilter();
                })
                .setOnCancelListener(dialogInterface -> {
                    storeFilterChip.setChecked(false);
                    clearPriceFilter();
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showDistanceFilterDialog() {
        final String[] distances = {"5 miles", "10 miles", "15 miles"};
        int checkedItem = -1;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Filter by Distance")
                .setSingleChoiceItems(distances, checkedItem, (dialog, which) -> {
                    // 'which' is the index of the selected item
                })
                .setPositiveButton("Apply", (dialog, id) -> {
                    // implement filtering logic
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                    dialog.dismiss();
                    distFilterChip.setChecked(false);
                    clearPriceFilter();
                })
                .setOnCancelListener(dialogInterface -> {
                    distFilterChip.setChecked(false);
                    clearPriceFilter();
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Deals with navigating back by one activity.
     *
     * @param item The menu item that was selected.
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if (id == android.R.id.home){
            navigateUpTo(new Intent(this, HomeScreen.class));
            return true;
        }
        return false;
    }

    private void performSearch(String query) {
        List<ShoppingCartData> filteredList = new ArrayList<>();
        ArrayList<ShoppingCartData> queriedData = new ArrayList<>();

        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS) // Adjust the connection timeout
                .readTimeout(30, TimeUnit.SECONDS)    // Adjust the read timeout
                .writeTimeout(30, TimeUnit.SECONDS)   // Adjust the write timeout
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("query",query)
                .addFormDataPart("stores","[\"Amazon\", \"Target\", \"Best Buy\", \"UW Bookstore\"]")
                .build();
        Request request = new Request.Builder()
                .url("https://api.resolyth.dev")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseData = response.body().string();
                queriedData = (ArrayList<ShoppingCartData>) loadItemsFromJson(responseData);
            } else {
                // Handle unsuccessful response here
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Handle IO exceptions here
        }


        for (ShoppingCartData item : queriedData) {
            if (item.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(item);
            }
        }

        // Update UI on the main thread using runOnUiThread
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Update UI components here
                adapter.updateList(filteredList);
            }
        });

        // Update the allItems with search results and then apply all filters
        allItems = filteredList;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Update UI components here
                applyAllFilters();
            }
        });
    }

    private List<ShoppingCartData> loadItemsFromJson(String rawData) {
        Gson gson = new Gson();
        Type itemListType = new TypeToken<ArrayList<ShoppingCartData>>(){}.getType();
        try {
            ArrayList<ShoppingCartData> data = gson.fromJson(rawData, itemListType);
            return data;
        } catch (JsonSyntaxException e) {
            return new ArrayList<>(); // Return an empty list if there's an error
        }
    }

    private void applyPriceFilter(double minPrice, double maxPrice) {
        minPriceFilter = minPrice;
        maxPriceFilter = maxPrice;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Update UI components here
                applyAllFilters();
            }
        });
    }

    private void applyStoreFilter(List<String> selectedStores) {
        selectedStoresFilter = selectedStores;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Update UI components here
                applyAllFilters();
            }
        });
    }

    // placeholder for the distance filter
    private void applyDistanceFilter(int distance) {
        // Update this method when distance filtering is done
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Update UI components here
                applyAllFilters();
            }
        });

    }

    private void applyAllFilters() {
        List<ShoppingCartData> filteredList = new ArrayList<>(allItems);

        if (minPriceFilter != null && maxPriceFilter != null) {
            filteredList = filterByPrice(filteredList, minPriceFilter, maxPriceFilter);
        }

        if (!selectedStoresFilter.isEmpty()) {
            filteredList = filterByStores(filteredList, selectedStoresFilter);
        }

        // Add distance filter logic when implemented

        adapter.updateList(filteredList);

        // Update chips based on whether filters are active
        priceFilterChip.setChecked(minPriceFilter != null && maxPriceFilter != null);
        storeFilterChip.setChecked(!selectedStoresFilter.isEmpty());
        // Update distance chip when implemented
    }

    private List<ShoppingCartData> filterByPrice(List<ShoppingCartData> items, double minPrice, double maxPrice) {
        List<ShoppingCartData> result = new ArrayList<>();
        for (ShoppingCartData item : items) {
            if (item.getPrice() >= minPrice && item.getPrice() <= maxPrice) {
                result.add(item);
            }
        }
        return result;
    }

    private List<ShoppingCartData> filterByStores(List<ShoppingCartData> items, List<String> selectedStores) {
        List<ShoppingCartData> result = new ArrayList<>();
        for (ShoppingCartData item : items) {
            if (selectedStores.contains(item.getStore())) {
                result.add(item);
            }
        }
        return result;
    }

    private void clearPriceFilter() {
        minPriceFilter = null;
        maxPriceFilter = null;
        priceFilterChip.setChecked(false);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Update UI components here
                applyAllFilters();
            }
        });
    }

    private void clearStoreFilter() {
        selectedStoresFilter.clear();
        storeFilterChip.setChecked(false);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Update UI components here
                applyAllFilters();
            }
        });
    }

    private void clearDistanceFilter() {
        // Implement this when distance filtering is added
        distFilterChip.setChecked(false);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Update UI components here
                applyAllFilters();
            }
        });
    }



}
