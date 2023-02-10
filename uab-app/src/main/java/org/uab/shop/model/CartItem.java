package org.uab.shop.model;


import org.uab.bike.model.Bike;

import javax.persistence.*;

@Entity
@Table(name = "uab_cart_item")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Bike bike;

    @ManyToOne(cascade = CascadeType.REFRESH)
    private Cart cart;
    private Integer quantity;

    public CartItem() {
    }

    public CartItem(Bike bike, Integer quantity, Cart cart) {
        this.bike = bike;
        this.quantity = quantity;
        this.cart = cart;
    }

    public void increaseQuantity() {
        if (this.quantity < this.getBike().getStock()) {
            this.quantity = this.quantity + 1;
        }
    }

    public void decreaseQuantity() {
        if (this.quantity > 0) {
            this.quantity = this.quantity - 1;
        }
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public Bike getBike() {
        return bike;
    }

    public void setBike(Bike bike) {
        this.bike = bike;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
