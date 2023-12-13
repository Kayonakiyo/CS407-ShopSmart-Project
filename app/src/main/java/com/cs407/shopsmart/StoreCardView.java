package com.cs407.shopsmart;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

public class StoreCardView extends CardView {
    private ImageView storeImageView;
    private TextView storeNameTextView;

    public StoreCardView(Context context) {
        super(context);
        init(context);
    }

    public StoreCardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public StoreCardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(final Context context) {
        LayoutInflater.from(getContext()).inflate(R.layout.item_store_card, this, true);
        storeImageView = findViewById(R.id.storeImageView);
        storeNameTextView = findViewById(R.id.storeNameTextView);
//        setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String storeName = storeNameTextView.getText().toString();
//                String toastMessage = storeName + " added to your preferred shops";
//                Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    public void setStoreName(String storeName) {
        storeNameTextView.setText(storeName);
    }

    public void setStoreImage(int resId) {
        storeImageView.setImageResource(resId);
    }
}
