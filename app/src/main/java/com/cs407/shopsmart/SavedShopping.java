package com.cs407.shopsmart;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import android.graphics.drawable.Drawable;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;

public class SavedShopping extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SavedShoppingAdapter adapter;
    private ArrayList<ShoppingCartData> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_shopping);

        // Get test data

        Gson jsonParser = new Gson();
        Scanner reader = null;
        try {
            reader = new Scanner(getAssets().open("UWBSData.json"));
        } catch (FileNotFoundException e) {
            Toast.makeText(this, "Could not parse data!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), HomeLoginScreen.class));
            return;
        } catch (IOException e) {
            Toast.makeText(this, "Could not parse data!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), HomeLoginScreen.class));
            return;
        }
        String dataString = "";
        while(reader.hasNextLine()){
            dataString += reader.nextLine();
        }
        items = jsonParser.fromJson(dataString, new TypeToken<ArrayList<ShoppingCartData>>(){}.getType());

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.saved_items_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new SavedShoppingAdapter(items);
        recyclerView.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.divider);
        dividerItemDecoration.setDrawable(drawable);
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    private List<ShoppingCartData> createFakeData() {
        List<ShoppingCartData> items = new ArrayList<>();
        items.add(new ShoppingCartData(
                "Apple - AirPods Pro - White",
                249.99,
                "BestBuy",
                "https://www.bestbuy.com/site/apple-airpods-pro-white/5706659.p?skuId=5706659",
                "https://pisces.bbystatic.com/image2/BestBuy_US/images/products/5706/5706659_sd.jpg"
        ));
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
