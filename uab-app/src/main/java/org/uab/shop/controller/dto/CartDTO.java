package org.uab.shop.controller.dto;

import org.uab.shop.model.Cart;
import org.uab.shop.model.CartItem;

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
