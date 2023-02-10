package org.uab.shop.service;

import org.uab.shop.model.Cart;

import java.math.BigDecimal;

public interface CartService {
    void addToCart(Long id);

    void updateQuantity(Long id, String type) throws Exception;

    BigDecimal calculateRentTotal();

    void deleteCartItem(Long id);

    Cart getUserCart();

    String getUserRole();
}
