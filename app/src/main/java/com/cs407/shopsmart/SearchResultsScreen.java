package com.cs407.shopsmart;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.Chip;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.util.Map;
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

/**
 * Represents the screen for displaying search results and applying filters.
 */
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
    private ProgressBar loadingSpinner;

    private TextView loadingText;

    /**
     * Called when the activity is first created. Responsible for initializing the activity components,
     * setting up the UI, and handling any initial operations.
     *
     * @param savedInstanceState A Bundle containing the saved state of the activity, or null if
     *                           this is the first time it is being created.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_results_screen);
// Shows the nav arrow on top of screen
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        loadingSpinner = findViewById(R.id.loading_spinner);
        loadingText = findViewById(R.id.loading_text);
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
                    executorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            performSearch(searchEditText.getText().toString());
                        }
                    });

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
                    searchEditText.clearFocus();

                    return true;
                }
                return false;
            }
        });

        Intent intent = getIntent();
        String homeQuery = intent.getStringExtra("searchQuery");
        if(homeQuery != null) {
            searchEditText.setText(homeQuery);

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

    /**
     * Sets up the filter chips by defining their behavior when checked or unchecked.
     * These chips include priceFilterChip, storeFilterChip, and distFilterChip.
     */
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

    /**
     * Displays a dialog for filtering items based on price range.
     */
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

    /**
     * Displays a dialog for filtering items based on selected stores.
     */
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

    /**
     * Displays a dialog for filtering items based on distance.
     */
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

    /**
     * Performs a search based on the provided query, retrieves search results from the server,
     * and updates the UI accordingly.
     *
     * @param query The search query entered by the user.
     */
    private void performSearch(String query) {
        List<ShoppingCartData> filteredList = new ArrayList<>();
        ArrayList<ShoppingCartData> queriedData = new ArrayList<>();

        runOnUiThread(() -> {
            loadingText.setVisibility(View.VISIBLE);
            loadingSpinner.setVisibility(View.VISIBLE);
        });

        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS) // Adjust the connection timeout
                .readTimeout(30, TimeUnit.SECONDS)    // Adjust the read timeout
                .writeTimeout(30, TimeUnit.SECONDS)   // Adjust the write timeout
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("query",query)
                .addFormDataPart("stores","[\"Amazon\", \"Best Buy\", \"UW Bookstore\"]")
                .build();
        Request request = new Request.Builder()
                .url("https://api.resolyth.dev")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                runOnUiThread( () -> {
                    loadingText.setVisibility(View.GONE);
                    loadingSpinner.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Query complete! Loading data...", Toast.LENGTH_SHORT).show();
                });
                String responseData = response.body().string();
                queriedData = (ArrayList<ShoppingCartData>) loadItemsFromJson(responseData);
            } else {
                // Handle unsuccessful response here
            }
        } catch (IOException e) {
            e.printStackTrace();
            runOnUiThread(() -> {
                Toast.makeText(getApplicationContext(), "Query timed out.", Toast.LENGTH_SHORT).show();
            });
        }


        for (ShoppingCartData item : queriedData) {
            filteredList.add(item);
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

    /**
     * Parses the raw JSON data received from the server and converts it into a list of ShoppingCartData objects.
     *
     * @param rawData The raw JSON data as a string.
     * @return A list of ShoppingCartData objects parsed from the JSON data.
     */
    private List<ShoppingCartData> loadItemsFromJson(String rawData) {
        rawData = cleanUpJson(rawData);
        Gson gson = new Gson();
        Type itemListType = new TypeToken<ArrayList<ShoppingCartData>>(){}.getType();
        try {
            ArrayList<ShoppingCartData> data = gson.fromJson(rawData, itemListType);
            return data;
        } catch (JsonSyntaxException e) {
            return new ArrayList<>(); // Return an empty list if there's an error
        }
    }

    /**
     * Used to parse through endpoint's JSON response and see if any doubles have comma's in them,
     * and if so, parse them out.
     * @param rawJson
     * @return
     */
    private String cleanUpJson(String rawJson){
        // Parse JSON into a JsonArray
        JsonParser parser = new JsonParser();
        JsonElement jsonElement = parser.parse(rawJson);
        JsonArray jsonArray = jsonElement.getAsJsonArray();

        // Iterate through each JSON object in the array
        for (JsonElement element : jsonArray) {
            if (element.isJsonObject()) {
                JsonObject jsonObject = element.getAsJsonObject();
                checkAndUpdateValues(jsonObject);
            }
        }

        // Convert the updated JsonArray to JSON string
        Gson gson = new Gson();
        String updatedJson = gson.toJson(jsonArray);
        return updatedJson;
    }

    /**
     * Recursively iterates through a JsonObject to check and update numeric values.
     *
     * @param jsonObject The JsonObject to check and update.
     */
    private void checkAndUpdateValues(JsonObject jsonObject) {
        for (String key : jsonObject.keySet()) {
            JsonElement value = jsonObject.get(key);

            if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isString() && key.equals("price")) {
                String stringValue = value.getAsString();
                if (stringValue.contains(",")) {
                    String updatedValue = stringValue.replace(",", "");
                    jsonObject.addProperty(key, updatedValue);
                }
                if (stringValue.contains("-")){
                    String updatedValue = stringValue.substring(0, stringValue.indexOf('-')-1);
                    jsonObject.addProperty(key, updatedValue);
                }
            } else if (value.isJsonObject()) {
                checkAndUpdateValues(value.getAsJsonObject());
            }
        }
    }

    /**
     * Applies a price filter to the search results within the specified range.
     *
     * @param minPrice The minimum price value for the filter.
     * @param maxPrice The maximum price value for the filter.
     */
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

    /**
     * Applies a store filter to the search results based on the selected stores.
     *
     * @param selectedStores A list of selected stores for the filter.
     */
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

    /**
     * Applies a distance filter to the search results within the specified distance range.
     *
     * @param distance The maximum distance for the filter.
     */
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

    /**
     * Applies all active filters (price, store, distance) to the search results and updates the UI.
     */
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

    /**
     * Filters a list of items based on the specified price range.
     *
     * @param items    The list of items to be filtered.
     * @param minPrice The minimum price value for the filter.
     * @param maxPrice The maximum price value for the filter.
     * @return The filtered list of items.
     */
    private List<ShoppingCartData> filterByPrice(List<ShoppingCartData> items, double minPrice, double maxPrice) {
        List<ShoppingCartData> result = new ArrayList<>();
        for (ShoppingCartData item : items) {
            if (item.getPrice() >= minPrice && item.getPrice() <= maxPrice) {
                result.add(item);
            }
        }
        return result;
    }

    /**
     * Filters a list of items based on the selected stores.
     *
     * @param items          The list of items to be filtered.
     * @param selectedStores The list of selected stores for the filter.
     * @return The filtered list of items.
     */
    private List<ShoppingCartData> filterByStores(List<ShoppingCartData> items, List<String> selectedStores) {
        List<ShoppingCartData> result = new ArrayList<>();
        for (ShoppingCartData item : items) {
            if (selectedStores.contains(item.getStore())) {
                result.add(item);
            }
        }
        return result;
    }

    /**
     * Clears the applied price filter and updates the UI.
     */
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

    /**
     * Clears the applied store filter and updates the UI.
     */
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

    /**
     * Clears the applied distance filter and updates the UI.
     */
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
