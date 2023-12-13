package com.cs407.shopsmart;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class SavedShopping extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SavedShoppingAdapter adapter;
    private ArrayList<ShoppingCartData> savedItems;
    private Button goToMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_shopping);

        goToMap = findViewById(R.id.mapButton);
        goToMap.setOnClickListener(v -> {
            Intent mapIntent = new Intent(getApplicationContext(), MapScreen.class);
            ArrayList<String> uniqueStores = new ArrayList<>();

            for(ShoppingCartData item : savedItems){
                if(!uniqueStores.contains(item.getStore())){
                    uniqueStores.add(item.getStore());
                }
            }

            mapIntent.putStringArrayListExtra("stores", uniqueStores);
            startActivity(mapIntent);
        });
        recyclerView = findViewById(R.id.saved_items_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
// Shows the nav arrow on top of screen
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
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
            navigateUpTo(new Intent(this, HomeScreen.class));
            return true;
        }
        return false;
    }
}
