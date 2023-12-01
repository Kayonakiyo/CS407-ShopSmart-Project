package com.cs407.shopsmart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class SavedShoppingAdapter extends RecyclerView.Adapter<SavedShoppingAdapter.ViewHolder> {

    private List<ShoppingCartData> items; // Data source

    // Constructor
    public SavedShoppingAdapter(List<ShoppingCartData> items) {
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_saved_shopping, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ShoppingCartData item = items.get(position);

        holder.itemName.setText(item.getName());
        holder.itemPrice.setText("$" + String.format("%.2f", item.getPrice()));
        holder.itemStore.setText(item.getStore());
        holder.itemOnlineLink.setText(item.getOnlineLink());

        Glide.with(holder.itemView.getContext())
                .load(item.getImageUrl())
                .into(holder.itemImage);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemName;
        TextView itemPrice;
        TextView itemStore;
        TextView itemOnlineLink;
        ImageView itemImage;

        ViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.item_name);
            itemPrice = itemView.findViewById(R.id.item_price);
            itemStore = itemView.findViewById(R.id.item_store);
            itemOnlineLink = itemView.findViewById(R.id.item_online_link);
            itemImage = itemView.findViewById(R.id.item_image);
        }
    }
}
