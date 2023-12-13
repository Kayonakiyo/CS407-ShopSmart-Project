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

/**
 * Custom CardView designed for displaying individual store information,
 * including store name and an associated image.
 * Provides functionality for handling click events to add the store to the user's preferred shops.
 */
public class StoreCardView extends CardView {
    private ImageView storeImageView;
    private TextView storeNameTextView;

    /**
     * Constructs a new StoreCardView instance.
     *
     * @param context The context in which the view is created.
     */
    public StoreCardView(Context context) {
        super(context);
        init(context);
    }

    /**
     * Constructs a new StoreCardView instance.
     *
     * @param context The context in which the view is created.
     * @param attrs   The set of attributes associated with the view.
     */
    public StoreCardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * Constructs a new StoreCardView instance.
     *
     * @param context      The context in which the view is created.
     * @param attrs        The set of attributes associated with the view.
     * @param defStyleAttr An attribute in the current theme that contains a reference to a
     *                     style resource that supplies defaults values for the view.
     *//**
    public StoreCardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * Initializes the StoreCardView by inflating the layout, setting up the internal views,
     * and attaching an onClickListener to display a toast message when clicked.
     *
     * @param context The context in which the view is created.
     */
    private void init(final Context context) {
        LayoutInflater.from(getContext()).inflate(R.layout.item_store_card, this, true);
        storeImageView = findViewById(R.id.storeImageView);
        storeNameTextView = findViewById(R.id.storeNameTextView);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String storeName = storeNameTextView.getText().toString();
                String toastMessage = storeName + " added to your preferred shops";
                Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Sets the name of the store displayed in the StoreCardView.
     *
     * @param storeName The name of the store to set.
     */
    public void setStoreName(String storeName) {
        storeNameTextView.setText(storeName);
    }

    /**
     * Sets the image resource for the store displayed in the StoreCardView.
     *
     * @param resId The resource identifier of the drawable to set as the image.
     */
    public void setStoreImage(int resId) {
        storeImageView.setImageResource(resId);
    }
}
