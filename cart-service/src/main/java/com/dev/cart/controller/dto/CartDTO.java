package com.dev.cart.controller.dto;

import com.dev.cart.model.Cart;
import com.dev.cart.model.CartItem;

import java.math.BigDecimal;
import java.util.List;

public class CartDTO {
    public List<CartItem> cartItems;
    public BigDecimal cartTotal;
    public boolean cartIsEmpty;

    public CartDTO(Cart cart) {
        this.cartItems = cart.getItems();
        this.cartTotal = cart.getTotal();
        this.cartIsEmpty = cart.isEmpty();
    }
}
