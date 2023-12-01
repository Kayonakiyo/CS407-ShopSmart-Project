package com.cs407.shopsmart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;

public class SavedShopping extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SavedShoppingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_shopping);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.saved_items_recycler_view); // Make sure this ID matches with your XML
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize your adapter with fake data
        adapter = new SavedShoppingAdapter(createFakeData());
        recyclerView.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.divider);
        dividerItemDecoration.setDrawable(drawable);
        recyclerView.addItemDecoration(dividerItemDecoration);
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
        items.add(new ShoppingCartData("Product 2", 49.99, "Store 2", "http://link2.com", "http://image2.com"));
        items.add(new ShoppingCartData("Product 3", 15.99, "Store 3", "http://link3.com", "http://image3.com"));
        return items;
    }
}
