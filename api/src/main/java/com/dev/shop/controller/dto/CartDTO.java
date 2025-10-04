package com.dev.shop.controller.dto;

import com.dev.shop.model.Cart;
import com.dev.shop.model.CartItem;

import java.math.BigDecimal;
import java.util.List;


public class CartDTO {
    public List<CartItem> cartItems;
    public BigDecimal cartTotal;

    public boolean cartIsEmpty;

    public CartDTO(Cart cart) {
        this.cartItems = cart.getCartItems();
        this.cartTotal = cart.getTotal();
        this.cartIsEmpty = cart.cartIsEmpty();
    }

}
