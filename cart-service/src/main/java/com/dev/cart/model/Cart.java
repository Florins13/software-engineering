package com.dev.cart.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Cart {
    private List<CartItem> items;

    public Cart() {
        this.items = new ArrayList<>();
    }

    public Cart(List<CartItem> items) {
        this.items = items;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public BigDecimal getTotal() {
        return items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getRentTotal() {
        int totalQuantity = items.stream().mapToInt(CartItem::getQuantity).sum();
        return BigDecimal.valueOf(totalQuantity * 5L);
    }

    public boolean isEmpty() {
        return items.isEmpty() || items.stream().allMatch(item -> item.getQuantity() == 0);
    }
}
