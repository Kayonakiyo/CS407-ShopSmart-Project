package com.cs407.shopsmart;

public class ShoppingCartData {
    private String name;
    private double price;
    private String store;
    private String onlineLink;
    private String imageUrl;

    public ShoppingCartData(String name, double price, String store, String onlineLink, String imageUrl) {
        this.name = name;
        this.price = price;
        this.store = store;
        this.onlineLink = onlineLink;
        this.imageUrl = imageUrl;
    }

    // Getter and setter methods
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public String getOnlineLink() {
        return onlineLink;
    }

    public void setOnlineLink(String onlineLink) {
        this.onlineLink = onlineLink;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
