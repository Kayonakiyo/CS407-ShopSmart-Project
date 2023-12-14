package com.cs407.shopsmart;

/**
 * Represents a data model for items in a shopping cart, including name, price, store,
 * online link, and image URL.
 */
public class ShoppingCartData {
    private String name;
    private double price;
    private String store;
    private String onlineLink;
    private String imageUrl;

    /**
     * Constructor for creating a ShoppingCartData instance.
     *
     * @param name      The name of the item.
     * @param price     The price of the item.
     * @param store     The store where the item is available.
     * @param onlineLink The online link for purchasing the item.
     * @param imageUrl  The URL of the image representing the item.
     */
    public ShoppingCartData(String name, double price, String store, String onlineLink, String imageUrl) {
        this.name = name;
        this.price = price;
        this.store = store;
        this.onlineLink = onlineLink;
        this.imageUrl = imageUrl;
    }

    /**
     * Gets the name of the item.
     *
     * @return The name of the item.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the item.
     *
     * @param name The name to set for the item.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the price of the item.
     *
     * @return The price of the item.
     */
    public double getPrice() {
        return price;
    }

    /**
     * Sets the price of the item.
     *
     * @param price The price to set for the item.
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Gets the store where the item is available.
     *
     * @return The store where the item is available.
     */
    public String getStore() {
        return store;
    }

    /**
     * Sets the store where the item is available.
     *
     * @param store The store to set for the item.
     */
    public void setStore(String store) {
        this.store = store;
    }

    /**
     * Gets the online link for purchasing the item.
     *
     * @return The online link for purchasing the item.
     */
    public String getOnlineLink() {
        return onlineLink;
    }

    /**
     * Sets the online link for purchasing the item.
     *
     * @param onlineLink The online link to set for the item.
     */
    public void setOnlineLink(String onlineLink) {
        this.onlineLink = onlineLink;
    }

    /**
     * Gets the URL of the image representing the item.
     *
     * @return The URL of the image representing the item.
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Sets the URL of the image representing the item.
     *
     * @param imageUrl The URL of the image to set for the item.
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
