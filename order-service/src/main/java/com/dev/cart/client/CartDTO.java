package com.dev.cart.client;

import java.math.BigDecimal;
import java.util.List;

public class CartDTO {
    public List<CartItemDTO> cartItems;
    public BigDecimal cartTotal;
    public boolean cartIsEmpty;

    public CartDTO() {
    }
}
