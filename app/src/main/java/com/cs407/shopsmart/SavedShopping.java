package com.cs407.shopsmart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;

public class SavedShopping extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SavedShoppingAdapter adapter;
    private ArrayList<ShoppingCartData> savedItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_shopping);

        recyclerView = findViewById(R.id.saved_items_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get saved items from the SavedItemsManager
        savedItems = (ArrayList<ShoppingCartData>) SavedItemsManager.getInstance().getSavedItems();
        if (savedItems.isEmpty()) {
            Toast.makeText(this, "No items saved", Toast.LENGTH_SHORT).show();
        }

        // Set up the adapter with the saved items
        adapter = new SavedShoppingAdapter(savedItems);
        recyclerView.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.divider);
        dividerItemDecoration.setDrawable(drawable);
        recyclerView.addItemDecoration(dividerItemDecoration);
    }
}
