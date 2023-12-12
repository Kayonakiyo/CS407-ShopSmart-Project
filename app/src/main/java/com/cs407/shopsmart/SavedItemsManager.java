package com.cs407.shopsmart;

import java.util.ArrayList;
import java.util.List;

public class SavedItemsManager {
    private static SavedItemsManager instance = null;
    private List<ShoppingCartData> savedItems;

    private SavedItemsManager() {
        savedItems = new ArrayList<>();
    }

    public static synchronized SavedItemsManager getInstance() {
        if (instance == null) {
            instance = new SavedItemsManager();
        }
        return instance;
    }

    public void addSavedItem(ShoppingCartData item) {
        if (!savedItems.contains(item)) {
            savedItems.add(item);
        }
    }

    public List<ShoppingCartData> getSavedItems() {
        return savedItems;
    }

    public void clearSavedItems() {
        savedItems.clear();
    }
}
