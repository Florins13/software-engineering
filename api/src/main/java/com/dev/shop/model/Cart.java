package com.dev.shop.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Entity
@Table(name = "uab_cart")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems;

    @Transient
    private BigDecimal total = new BigDecimal(BigInteger.ZERO, 2);

    public Cart() {
    }

    public Cart(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    public BigDecimal getTotal() {
        for (CartItem cartItem : this.getCartItems()) {
            total = total.add(this.calculateTotalCost(cartItem.getQuantity(), cartItem.getBike().getPrice()));
        }
        return total;
    }

    public BigDecimal calculateTotalCost(int cartItemQuantity, BigDecimal cartItemPrice) {
        BigDecimal totalCost = BigDecimal.ZERO;
        BigDecimal itemCost;
        itemCost = cartItemPrice.multiply(BigDecimal.valueOf(cartItemQuantity));
        totalCost = totalCost.add(itemCost);
        return totalCost;
    }

    public boolean cartIsEmpty() {
        return this.cartItems.stream().noneMatch(cartItem -> cartItem.getQuantity() > 0);
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItem> cartItem) {
        this.cartItems = cartItem;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

}
