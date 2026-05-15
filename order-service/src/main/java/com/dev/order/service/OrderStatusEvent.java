package com.dev.order.service;

import com.dev.order.model.OrderState;

public class OrderStatusEvent {
    public String transactionId;
    public OrderState status;
    public String message;

    public OrderStatusEvent() {
    }

    public OrderStatusEvent(String transactionId, OrderState status, String message) {
        this.transactionId = transactionId;
        this.status = status;
        this.message = message;
    }
}
