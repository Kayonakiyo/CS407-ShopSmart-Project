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

public class HomeScreenAdapter extends RecyclerView.Adapter<HomeScreenAdapter.ViewHolder> {
    private List<ShoppingCartData> trendingItems;

    // Constructor
    public HomeScreenAdapter(List<ShoppingCartData> items)
    {
        this.trendingItems = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_saved_shopping, parent, false);
        return new ViewHolder(view);
    }

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

    @Override
    public int getItemCount() {
        return trendingItems.size();
    }

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
