package com.cs407.shopsmart;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapter class for the RecyclerView in the search results screen.
 * Handles the binding of shopping cart data to the RecyclerView items,
 * as well as implementing filtering for search functionality.
 */
public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.ItemViewHolder> implements Filterable {

    private List<ShoppingCartData> itemList;
    private List<ShoppingCartData> itemListFull;

    /**
     * Constructor for the SearchResultsAdapter.
     *
     * @param itemList List of ShoppingCartData representing the data source for the adapter.
     */
    public SearchResultsAdapter(List<ShoppingCartData> itemList) {
        this.itemList = itemList;
        this.itemListFull = new ArrayList<>(itemList);
    }

    /**
     * Called when RecyclerView needs a new {@link ItemViewHolder} of the given type to represent
     * an item.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_results, parent, false);
        return new ItemViewHolder(itemView);
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link ItemViewHolder#itemView} to reflect the item at the
     * given position.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        ShoppingCartData currentItem = itemList.get(position);
        holder.itemName.setText(currentItem.getName());
        holder.itemPrice.setText("$" + String.format("%.2f", currentItem.getPrice()));
        holder.itemStore.setText(currentItem.getStore());

        Glide.with(holder.itemView.getContext())
                .load(currentItem.getImageUrl())
                .into(holder.itemImage);

        holder.itemView.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentItem.getOnlineLink()));
            v.getContext().startActivity(browserIntent);
        });

        holder.addToCartButton.setOnClickListener(v -> {
            SavedItemsManager.getInstance().addSavedItem(currentItem);
            Toast.makeText(holder.itemView.getContext(), "Added to saved items", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in the adapter.
     */
    @Override
    public int getItemCount() {
        return itemList.size();
    }

    /**
     * Returns a filter that can be used to constrain data with a filtering pattern.
     *
     * @return A filter for the adapter's data.
     */
    @Override
    public Filter getFilter() {
        return itemFilter;
    }


    /**
     * Filter implementation for search functionality.
     */
    private Filter itemFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ShoppingCartData> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(itemListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (ShoppingCartData item : itemListFull) {
                    if (item.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            itemList.clear();
            itemList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    /**
     * Updates the adapter's item list with a new list of items.
     *
     * @param newList The new list of ShoppingCartData items.
     */
    public void updateList(List<ShoppingCartData> newList) {
        itemList.clear();
        itemList.addAll(newList);
        notifyDataSetChanged();
    }

    /**
     * ViewHolder class representing the individual items in the RecyclerView.
     */
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView itemName;
        TextView itemPrice;
        TextView itemStore;
        ImageView itemImage;
        ImageButton addToCartButton;

        public ItemViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.item_search_name);
            itemPrice = itemView.findViewById(R.id.item_search_price);
            itemStore = itemView.findViewById(R.id.item_search_store);
            itemImage = itemView.findViewById(R.id.item_search_image);
            addToCartButton = itemView.findViewById(R.id.add_to_cart_button);
        }
    }
}
