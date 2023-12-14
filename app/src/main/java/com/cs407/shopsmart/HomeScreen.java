package com.cs407.shopsmart;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the home screen of the ShopSmart app where users can navigate through trending items,
 * access their shopping cart, set preferred shops, and log out.
 */
public class HomeScreen extends AppCompatActivity {

    private RecyclerView trendingRecycler;
    private HomeScreenAdapter adapter;

    private EditText searchItemEditText;

    Button logoutButton;
    Button cartButton;

    Button setShopsButton;
    SharedPreferences userSession;


    /**
     * Called when the activity is first created. Initializes the user interface,
     * buttons, SharedPreferences for user session, and sets click listeners.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously
     *                           being shut down, this Bundle contains the data it most
     *                           recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        View backgroundClickView = findViewById(R.id.backgroundClickView);
        searchItemEditText = findViewById(R.id.searchItem);

        backgroundClickView.setOnClickListener(v -> hideKeyboard());
        searchItemEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                hideKeyboard();
            }
        });

        // Initialize SharedPreferences for user session
        userSession = getSharedPreferences("userSession", MODE_PRIVATE);

        // Initialize buttons and set click listeners
        cartButton = findViewById(R.id.cartButton);
        cartButton.setOnClickListener(v -> {
            Intent switchToSavedShopping = new Intent(this, SavedShopping.class);
            startActivity(switchToSavedShopping);
        });

        setShopsButton = findViewById(R.id.setShopsButton);
        setShopsButton.setOnClickListener(v -> {
            Intent switchToStoreSelecting = new Intent(this, RegistrationShopSelection.class);
            startActivity(switchToStoreSelecting);
        });


        logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> {
            // Log out the user and switch to the login screen
            if(userSession.getBoolean("loggedIn", true)){
                SharedPreferences.Editor editor = userSession.edit();
                editor.putString("username", "Guest"); // default user.
                editor.putBoolean("loggedIn", false); // log em out
                editor.apply();
            }
            startActivity(new Intent(this, HomeLoginScreen.class)); // back to login screen
        });

        // Initialize RecyclerView
        trendingRecycler = findViewById(R.id.trending_recycler_view);
        initializeRecyclerView();
        setupCategoryButtons();
    }

    /**
     * Initializes the RecyclerView for trending items, sets its layout manager,
     * and adds a divider decoration.
     */
    private void initializeRecyclerView() {
        // Find the RecyclerView in the layout by its ID
        trendingRecycler = findViewById(R.id.trending_recycler_view);

        // Set the layout manager for the RecyclerView (Linear for a vertical list)
        trendingRecycler.setLayoutManager(new LinearLayoutManager(this));

        // Initialize your adapter with fake data
        adapter = new HomeScreenAdapter(createFakeData());
        trendingRecycler.setAdapter(adapter);

        // Add divider decoration to the RecyclerView
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(trendingRecycler.getContext(), DividerItemDecoration.VERTICAL);
        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.divider);
        dividerItemDecoration.setDrawable(drawable);
        trendingRecycler.addItemDecoration(dividerItemDecoration);
    }

    /**
     * Sets up click listeners for category buttons in the home screen.
     * Each button is associated with a specific category, and clicking
     * on a button navigates to the SearchResultsScreen with the corresponding
     * category query.
     */
    private void setupCategoryButtons() {
        setupButtonClickListener(R.id.techButton, "Tech");
        setupButtonClickListener(R.id.homeButton, "Home");
        setupButtonClickListener(R.id.toysButton, "Toys");
        setupButtonClickListener(R.id.stationaryButton, "Stationary");
        setupButtonClickListener(R.id.clothingButton, "Clothing");
        setupButtonClickListener(R.id.shopAllButton, ""); // Empty string for "Shop All" category
        setupSeeAllButtonListener(R.id.seeAll);
    }

    /**
     * Sets up a click listener for a specific button identified by its ID.
     *
     * @param buttonId The resource ID of the button to set up the click listener for.
     * @param query The search query associated with the button.
     */
    private void setupButtonClickListener(int buttonId, String query) {
        Button button = findViewById(buttonId);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the SearchResultsScreen with the specified search query
                Intent intent = new Intent(HomeScreen.this, SearchResultsScreen.class);
                intent.putExtra("searchQuery", query);
                startActivity(intent);
            }
        });
    }

    /**
     * Sets up a click listener for the "See All" button identified by its ID.
     *
     * @param buttonId The resource ID of the "See All" button to set up the click listener for.
     */
    private void setupSeeAllButtonListener(int buttonId) {
        Button button = findViewById(buttonId);
        button.setOnClickListener(v -> {
            // Start the SearchResultsScreen without a specific search query (for "See All")
            Intent intent = new Intent(HomeScreen.this, SearchResultsScreen.class);
            startActivity(intent);
        });
    }

    /**
     * Creates a list of fake shopping cart data for use in the trending items RecyclerView.
     *
     * @return List of ShoppingCartData objects representing placeholder shopping cart items.
     */
    private List<ShoppingCartData> createFakeData() {
        // Create fake data for the trending items
        List<ShoppingCartData> items = new ArrayList<>();
        items.add(new ShoppingCartData(
                "Apple - 10.2-Inch iPad (9th Generation) with Wi-Fi - 64GB - Space Gray",
                329.99,
                "BestBuy",
                "https://www.bestbuy.com/site/apple-10-2-inch-ipad-9th-generation-with-wi-fi-64gb-space-gray/4901809.p?skuId=4901809",
                "https://m.media-amazon.com/images/I/61NGnpjoRDL._AC_UF894,1000_QL80_.jpg"
        ));
        items.add(new ShoppingCartData("THE NORTH FACE Borealis Laptop Backpack",
                98.95,
                "Amazon.com",
                "https://www.amazon.com/North-Face-Borealis-TNF-Black/dp/B092G6G7ZN/ref=sr_1_3?qid=1701734322&refinements=p_89%3AThe%2BNorth%2BFace&s=apparel&sr=1-3&ufe=app_do%3Aamzn1.fos.17d9e15d-4e43-4581-b373-0e5c1a776d5d&th=1",
                "https://m.media-amazon.com/images/I/71oh8zVKBqL._AC_UY1000_.jpg"));
        items.add(new ShoppingCartData("Dash 2.6qt Express Digital Tasti-Crisp Nonstick Air Fryer",
                44.99,
                "Target",
                "https://www.target.com/p/dash-2-6-qt-express-digital-tasti-crisp-nonstick-air-fryer/-/A-82667189?ref=tgt_adv_xsp&AFID=google&fndsrc=tgtao&DFA=71700000012510313&CPNG=PLA_Appliances_Priority%2BShopping%7CAppliances_Ecomm_Home&adgroup=Appliances_Priority+TCINs&LID=700000001170770pgs&LNM=PRODUCT_GROUP&network=g&device=c&location=9018948&targetid=pla-2235524776790&ds_rl=1246978&ds_rl=1247068&gad_source=1&gclid=CjwKCAiAjrarBhAWEiwA2qWdCAmjJ0ZVJB98_FADGEB-le5Tg-bcbaV8dNFqdwksCTNqKsD8uBAH5RoCUswQAvD_BwE&gclsrc=aw.ds",
                "https://target.scene7.com/is/image/Target/GUEST_50d6171b-266c-4904-8cd8-d27881569b20?wid=1200&hei=1200&qlt=80&fmt=webp"));
        return items;
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
