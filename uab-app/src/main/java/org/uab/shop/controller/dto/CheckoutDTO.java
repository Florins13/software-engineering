package org.uab.shop.controller.dto;

import org.uab.shop.model.Cart;

import java.math.BigDecimal;

public class CheckoutDTO extends CartDTO {
    public BigDecimal rentTotal;

    public CheckoutDTO(Cart cart, BigDecimal rentTotal) {
        super(cart);
        this.rentTotal = rentTotal;
    }
}
