package com.dev.shop.controller.dto;

import com.dev.shop.model.Cart;

import java.math.BigDecimal;

public class CheckoutDTO extends CartDTO {
    public BigDecimal rentTotal;

    public CheckoutDTO(Cart cart, BigDecimal rentTotal) {
        super(cart);
        this.rentTotal = rentTotal;
    }
}
