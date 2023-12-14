package com.cs407.shopsmart;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Adapter class for the RecyclerView in the SavedShopping activity.
 * Handles the binding of shopping cart data to the RecyclerView items.
 */
public class SavedShoppingAdapter extends RecyclerView.Adapter<SavedShoppingAdapter.ViewHolder> {

    private List<ShoppingCartData> items; // Data source

    /**
     * Constructor for the SavedShoppingAdapter.
     *
     * @param items List of ShoppingCartData representing the data source for the adapter.
     */
    public SavedShoppingAdapter(List<ShoppingCartData> items) {
        this.items = items;
    }

    /**
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_saved_shopping, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link ViewHolder#itemView} to reflect the item at the
     * given position.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ShoppingCartData item = items.get(position);

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

        holder.deleteButton.setOnClickListener(v -> {
            removeItem(position);
        });
    }

    /**
     * Removes an item from the data source at the specified position and updates the adapter.
     *
     * @param position The position of the item to be removed.
     */
    private void removeItem(int position) {
        items.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, items.size());
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in the adapter.
     */
    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemName;
        TextView itemPrice;
        TextView itemStore;
        ImageView itemImage;
        Button viewOnlineButton;
        ImageButton deleteButton;

        /**
         * ViewHolder class representing the individual items in the RecyclerView.
         */
        ViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.item_name);
            itemPrice = itemView.findViewById(R.id.item_price);
            itemStore = itemView.findViewById(R.id.item_store);
            itemImage = itemView.findViewById(R.id.item_image);
            viewOnlineButton = itemView.findViewById(R.id.item_view_online_button);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }
}
