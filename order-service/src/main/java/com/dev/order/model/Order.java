package com.dev.order.model;

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

    private String userId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
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

    public Order(String userId, ShippingAddress shippingAddress, AcquireType acquireType, BigDecimal totalPrice) {
        this.userId = userId;
        this.shippingAddress = shippingAddress;
        this.acquireType = acquireType;
        this.orderState = OrderState.PENDING;
        this.totalPrice = totalPrice;
        this.transaction = UUID.randomUUID().toString();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<ShippingItem> getShippingItems() {
        return shippingItems;
    }

    public void setShippingItems(List<ShippingItem> shippingItems) {
        this.shippingItems = shippingItems;
    }

    public ShippingAddress getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(ShippingAddress shippingAddress) {
        this.shippingAddress = shippingAddress;
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
        this.totalPrice = totalPrice;
    }
}
