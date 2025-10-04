package com.dev.shop.model;


import com.dev.product.model.Bike;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "uab_shipping_item")
public class ShippingItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Bike bike;

    @ManyToOne
    @JsonIgnore
    private Order order;
    private Integer quantity;

    public ShippingItem() {
    }

    public ShippingItem(Bike bike, Integer quantity) {
        this.setBike(bike);
        this.quantity = quantity;
    }

    public ShippingItem(Bike bike, Integer quantity, Order order) {
        this.setBike(bike);
        this.quantity = quantity;
        this.setOrder(order);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Bike getBike() {
        return bike;
    }

    public void setBike(Bike bike) {
        this.bike = bike;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }


}
