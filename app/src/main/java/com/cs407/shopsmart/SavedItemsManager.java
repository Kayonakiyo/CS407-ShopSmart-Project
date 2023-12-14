package com.cs407.shopsmart;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton class for managing saved items in the shopping cart.
 */
public class SavedItemsManager {
    private static SavedItemsManager instance = null;
    private List<ShoppingCartData> savedItems;

    /**
     * Private constructor to initialize the saved items list.
     */
    private SavedItemsManager() {
        savedItems = new ArrayList<>();
    }

    /**
     * Retrieves the instance of the SavedItemsManager class.
     *
     * @return The instance of SavedItemsManager.
     */
    public static synchronized SavedItemsManager getInstance() {
        if (instance == null) {
            instance = new SavedItemsManager();
        }
        return instance;
    }

    /**
     * Retrieves the instance of the SavedItemsManager class.
     *
     * @return The instance of SavedItemsManager.
     */
    public void addSavedItem(ShoppingCartData item) {
        if (!savedItems.contains(item)) {
            savedItems.add(item);
        }
    }

    /**
     * Retrieves the list of saved items in the shopping cart.
     *
     * @return The list of saved items.
     */
    public List<ShoppingCartData> getSavedItems() {
        return savedItems;
    }

    /**
     * Clears all saved items from the shopping cart.
     */
    public void clearSavedItems() {
        savedItems.clear();
    }
}
