package org.uab.shop.model;

public enum OrderState {
    NEW,
    CANCELED,
    WAITING_PAYMENT,
    PROCESSING,
    SHIPPING,
    COMPLETED
}
