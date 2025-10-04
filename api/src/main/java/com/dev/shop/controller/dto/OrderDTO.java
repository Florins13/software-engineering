package com.dev.shop.controller.dto;

import com.dev.shop.model.*;

import java.math.BigDecimal;
import java.util.List;

public class OrderDTO {
    public String transaction;
    public OrderState orderState;
    public AcquireType acquireType;
    public List<ShippingItem> shippingItems;

    public ShippingAddress shippingAddress;
    public BigDecimal totalPrice;
    public String username;

    public OrderDTO(Order order) {
        this.transaction = order.getTransaction();
        this.orderState = order.getOrderState();
        this.acquireType = order.getAcquireType();
        this.shippingItems = order.getShippingItems();
        this.totalPrice = order.getTotalPrice();
        this.username = order.getUser().getUsername();
        this.shippingAddress = order.getShippingAddress();
    }

    public List<ShippingItem> getShippingItems() {
        return shippingItems;
    }

    public void setShippingItems(List<ShippingItem> shippingItems) {
        this.shippingItems = shippingItems;
    }

    public String getTransaction() {
        return transaction;
    }

    public OrderState getOrderState() {
        return orderState;
    }

    public AcquireType getAcquireType() {
        return acquireType;
    }

    public ShippingAddress getShippingAddress() {
        return shippingAddress;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public String getUsername() {
        return username;
    }

}
