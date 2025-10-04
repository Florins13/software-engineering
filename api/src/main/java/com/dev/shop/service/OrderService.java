package com.dev.shop.service;



import com.dev.product.model.Bike;
import com.dev.shop.model.*;

import java.math.BigDecimal;
import java.util.List;

public interface OrderService {
    List<Order> getOrders();

    Order placeOrder(String fullName, String address, Integer telephone, String zipCode, String acquireType) throws Exception;

    Order createOrder(String fullName, String address, Integer telephone, String zipCode, String acquireType);

    List<ShippingItem> createShippingItems(List<CartItem> cartItems, Order order);

    List<Bike> updateStock(List<ShippingItem> shippingItems);

    BigDecimal getPriceBasedOnAcquireType(AcquireType acquire);

    Cart getUserCart();

    String getUserRole();
}
