package com.cs407.shopsmart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.ItemViewHolder> implements Filterable {

    private List<ShoppingCartData> itemList;
    private List<ShoppingCartData> itemListFull;

    public SearchResultsAdapter(List<ShoppingCartData> itemList) {
        this.itemList = itemList;
        this.itemListFull = new ArrayList<>(itemList);
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_saved_shopping, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        ShoppingCartData currentItem = itemList.get(position);
        holder.itemName.setText(currentItem.getName());
        holder.itemPrice.setText("$" + String.format("%.2f", currentItem.getPrice()));
        holder.itemStore.setText(currentItem.getStore());

        Glide.with(holder.itemView.getContext())
                .load(currentItem.getImageUrl())
                .into(holder.itemImage);

        holder.viewOnlineButton.setOnClickListener(v -> {
            // Implement your logic to open the product link
            // For example, you might start a new Activity with a WebView or open the link in a browser
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public Filter getFilter() {
        return itemFilter;
    }

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

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView itemName;
        TextView itemPrice;
        TextView itemStore;
        ImageView itemImage;
        Button viewOnlineButton;

        public ItemViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.item_name);
            itemPrice = itemView.findViewById(R.id.item_price);
            itemStore = itemView.findViewById(R.id.item_store);
            itemImage = itemView.findViewById(R.id.item_image);
            viewOnlineButton = itemView.findViewById(R.id.item_view_online_button);
        }
    }
}
