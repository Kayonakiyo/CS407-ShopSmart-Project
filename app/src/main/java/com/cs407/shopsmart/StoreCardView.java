package com.cs407.shopsmart;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

public class StoreCardView extends CardView {
    private ImageView storeImageView;
    private TextView storeNameTextView;

    public StoreCardView(Context context) {
        super(context);
        init();
    }

    public StoreCardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StoreCardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.item_store_card, this, true);
        storeImageView = findViewById(R.id.storeImageView);
        storeNameTextView = findViewById(R.id.storeNameTextView);
    }

    public void setStoreName(String storeName) {
        storeNameTextView.setText(storeName);
    }

    public void setStoreImage(int resId) {
        storeImageView.setImageResource(resId);
    }
}
