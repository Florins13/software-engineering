package com.dev.cart.client;

import java.math.BigDecimal;

public class CartItemDTO {
    public Long id;
    public String model;
    public String imageSource;
    public BigDecimal price;
    public int quantity;

    public CartItemDTO() {
    }
}
