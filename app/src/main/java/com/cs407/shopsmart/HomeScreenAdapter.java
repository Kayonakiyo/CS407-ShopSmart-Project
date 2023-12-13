package com.cs407.shopsmart;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Adapter class for managing and displaying trending items in the HomeScreen's RecyclerView.
 * Uses the ViewHolder pattern for efficient view recycling.
 */
public class HomeScreenAdapter extends RecyclerView.Adapter<HomeScreenAdapter.ViewHolder> {
    private List<ShoppingCartData> trendingItems;

    /**
     * Constructor for HomeScreenAdapter.
     *
     * @param items List of ShoppingCartData representing trending items.
     */
    public HomeScreenAdapter(List<ShoppingCartData> items)
    {
        this.trendingItems = items;
    }

    /**
     * Creates a new ViewHolder by inflating the item layout.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_saved_shopping, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Binds data to the ViewHolder and sets up click listeners.
     *
     * @param holder   The ViewHolder to bind data to.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(HomeScreenAdapter.ViewHolder holder, int position) {
        ShoppingCartData item = trendingItems.get(position);

        holder.itemName.setText(item.getName());
        holder.itemPrice.setText("$" + String.format("%.2f", item.getPrice()));
        holder.itemStore.setText(item.getStore());

        Glide.with(holder.itemView.getContext())
                .load(item.getImageUrl())
                .into(holder.itemImage);

        holder.viewOnlineButton.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getOnlineLink()));
            holder.itemView.getContext().startActivity(browserIntent);
        });
    }

    /**
     * Gets the total number of items in the data set held by the adapter.
     *
     * @return The total number of items.
     */
    @Override
    public int getItemCount() {
        return trendingItems.size();
    }

    /**
     * ViewHolder class for efficient view recycling.
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemName;
        TextView itemPrice;
        TextView itemStore;
        ImageView itemImage;
        Button viewOnlineButton; // Button for viewing the online link

        ViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.item_name);
            itemPrice = itemView.findViewById(R.id.item_price);
            itemStore = itemView.findViewById(R.id.item_store);
            itemImage = itemView.findViewById(R.id.item_image);
            viewOnlineButton = itemView.findViewById(R.id.item_view_online_button); // Initialize the button
        }
    }
}
