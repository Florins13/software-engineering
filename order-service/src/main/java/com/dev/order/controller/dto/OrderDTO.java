package com.dev.order.controller.dto;

import com.dev.order.model.*;

import java.math.BigDecimal;
import java.util.List;

public class OrderDTO {
    public String transaction;
    public OrderState orderState;
    public AcquireType acquireType;
    public List<ShippingItem> shippingItems;
    public ShippingAddress shippingAddress;
    public BigDecimal totalPrice;
    public String userId;

    public OrderDTO(Order order) {
        this.transaction = order.getTransaction();
        this.orderState = order.getOrderState();
        this.acquireType = order.getAcquireType();
        this.shippingItems = order.getShippingItems();
        this.totalPrice = order.getTotalPrice();
        this.userId = order.getUserId();
        this.shippingAddress = order.getShippingAddress();
    }
}
