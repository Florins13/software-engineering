package com.dev.cart.model;

import java.math.BigDecimal;

public class CartItem {
    private Long id;
    private String model;
    private String imageSource;
    private BigDecimal price;
    private int quantity;

    public CartItem() {
    }

    public CartItem(Long id, String model, String imageSource, BigDecimal price, int quantity) {
        this.id = id;
        this.model = model;
        this.imageSource = imageSource;
        this.price = price;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getImageSource() {
        return imageSource;
    }

    public void setImageSource(String imageSource) {
        this.imageSource = imageSource;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void increaseQuantity() {
        this.quantity++;
    }

    public void decreaseQuantity() {
        if (this.quantity > 0) {
            this.quantity--;
        }
    }
}
