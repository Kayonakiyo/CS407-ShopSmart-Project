package com.cs407.shopsmart;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HomeScreen extends AppCompatActivity {

    private RecyclerView trendingRecycler;

    private HomeScreenAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_shopping);

        // Initialize RecyclerView
        trendingRecycler = findViewById(R.id.saved_items_recycler_view);
        trendingRecycler.setLayoutManager(new LinearLayoutManager(this));

        // Initialize your adapter with fake data
        adapter = new HomeScreenAdapter(createFakeData());
        trendingRecycler.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(trendingRecycler.getContext(), DividerItemDecoration.VERTICAL);
        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.divider);
        dividerItemDecoration.setDrawable(drawable);
        trendingRecycler.addItemDecoration(dividerItemDecoration);

        Button tech = (Button)findViewById(R.id.techButton);

        tech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the SearchResultsScreen activity
                Intent intent = new Intent(HomeScreen.this, SearchResultsScreen.class);

                // Put extra data (the word "Tech") into the intent
                intent.putExtra("searchQuery", "Tech");

                // Start the SearchResultsScreen activity with the intent
                startActivity(intent);
            }
        });

        Button home = (Button)findViewById(R.id.homeButton);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the SearchResultsScreen activity
                Intent intent = new Intent(HomeScreen.this, SearchResultsScreen.class);

                // Put extra data (the word "Tech") into the intent
                intent.putExtra("searchQuery", "Home");

                // Start the SearchResultsScreen activity with the intent
                startActivity(intent);
            }
        });

        Button toys = (Button)findViewById(R.id.toysButton);

        toys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the SearchResultsScreen activity
                Intent intent = new Intent(HomeScreen.this, SearchResultsScreen.class);

                // Put extra data (the word "Tech") into the intent
                intent.putExtra("searchQuery", "Toys");

                // Start the SearchResultsScreen activity with the intent
                startActivity(intent);
            }
        });

        Button stationary = (Button)findViewById(R.id.stationaryButton);

        stationary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the SearchResultsScreen activity
                Intent intent = new Intent(HomeScreen.this, SearchResultsScreen.class);

                // Put extra data (the word "Tech") into the intent
                intent.putExtra("searchQuery", "Stationary");

                // Start the SearchResultsScreen activity with the intent
                startActivity(intent);
            }
        });

        Button clothing = (Button)findViewById(R.id.clothingButton);

        clothing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the SearchResultsScreen activity
                Intent intent = new Intent(HomeScreen.this, SearchResultsScreen.class);

                // Put extra data (the word "Tech") into the intent
                intent.putExtra("searchQuery", "Clothing");

                // Start the SearchResultsScreen activity with the intent
                startActivity(intent);
            }
        });

        Button shopAll = (Button)findViewById(R.id.shopAllButton);

        shopAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the SearchResultsScreen activity
                Intent intent = new Intent(HomeScreen.this, SearchResultsScreen.class);

                // Start the SearchResultsScreen activity with the intent
                startActivity(intent);
            }
        });
    }

    private List<ShoppingCartData> createFakeData() {
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
}
