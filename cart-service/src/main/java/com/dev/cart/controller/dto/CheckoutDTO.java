package com.dev.cart.controller.dto;

import com.dev.cart.model.Cart;

import java.math.BigDecimal;

public class CheckoutDTO extends CartDTO {
    public BigDecimal rentTotal;

    public CheckoutDTO(Cart cart) {
        super(cart);
        this.rentTotal = cart.getRentTotal();
    }
}
