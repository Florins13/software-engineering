package com.dev.shop.model;


import com.dev.user.model.User;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "uab_order")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShippingItem> shippingItems;

    @OneToOne(cascade = CascadeType.ALL)
    private ShippingAddress shippingAddress;
    @Enumerated(EnumType.STRING)
    private AcquireType acquireType;

    @Enumerated(EnumType.STRING)
    private OrderState orderState;


    private String transaction;
    private BigDecimal totalPrice;

    public Order() {
    }

    public Order(User user, ShippingAddress shippingAddress, AcquireType acquireType, OrderState orderState, BigDecimal totalPrice) {
        this.user = user;
        this.setShippingAddress(shippingAddress);
        this.setAcquireType(acquireType);
        this.setOrderState(orderState);
        this.totalPrice = totalPrice;
        this.transaction = UUID.randomUUID().toString();
    }

    public List<ShippingItem> getShippingItems() {
        return shippingItems;
    }


    public ShippingAddress getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(ShippingAddress shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public void setShippingItems(List<ShippingItem> shippingItems) {
        this.shippingItems = shippingItems;
    }

    public AcquireType getAcquireType() {
        return acquireType;
    }

    public void setAcquireType(AcquireType acquireType) {
        this.acquireType = acquireType;
    }

    public OrderState getOrderState() {
        return orderState;
    }

    public void setOrderState(OrderState orderState) {
        this.orderState = orderState;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTransaction() {
        return transaction;
    }

    public void setTransaction(String transaction) {
        this.transaction = transaction;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        if (totalPrice.compareTo(BigDecimal.ZERO) > 0) this.totalPrice = totalPrice;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
