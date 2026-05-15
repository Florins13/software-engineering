package com.dev.order.service;

import com.dev.cart.client.CartDTO;
import com.dev.order.model.*;
import com.dev.order.repository.OrderRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class OrderService {

    @Inject
    OrderRepository orderRepository;

    @Inject
    @RestClient
    CartRestClient cartRestClient;

    /**
     * Places an order: fetches cart, creates order in PENDING state.
     * Returns the order. Saga must be started by the caller AFTER this transaction commits.
     */
    @Transactional
    public Order placeOrder(String userId, String fullName, String address,
                            Integer telephone, String zipCode, String acquireType) throws Exception {

        CartDTO cartDTO = cartRestClient.getCart(userId);
        if (cartDTO.cartIsEmpty) {
            throw new Exception("Your cart is empty, please add items and try again!");
        }

        AcquireType acquire = AcquireType.RENT.name().equalsIgnoreCase(acquireType) ? AcquireType.RENT : AcquireType.BUY;
        BigDecimal totalPrice = calculatePrice(cartDTO, acquire);

        ShippingAddress shippingAddress = new ShippingAddress(fullName, address, telephone, zipCode);
        Order order = new Order(userId, shippingAddress, acquire, totalPrice);

        List<ShippingItem> shippingItems = cartDTO.cartItems.stream()
                .map(item -> {
                    ShippingItem si = new ShippingItem(item.id, item.model, item.price, item.quantity);
                    si.setOrder(order);
                    return si;
                })
                .collect(Collectors.toList());
        order.setShippingItems(shippingItems);

        orderRepository.save(order);

        return order;
    }

    public List<Order> getOrdersByUserId(String userId) {
        return orderRepository.getOrdersByUserId(userId);
    }

    @Transactional
    public Order findByTransaction(String transaction) {
        return orderRepository.findByTransaction(transaction).orElse(null);
    }

    @Transactional
    public String findUserIdByTransaction(String transaction) {
        return orderRepository.findByTransaction(transaction)
                .map(Order::getUserId)
                .orElse("unknown");
    }

    @Transactional
    public void updateOrderState(String transaction, OrderState newState) {
        orderRepository.findByTransaction(transaction).ifPresent(order -> {
            order.setOrderState(newState);
        });
    }

    private BigDecimal calculatePrice(CartDTO cartDTO, AcquireType acquire) {
        if (acquire == AcquireType.BUY) {
            return cartDTO.cartTotal;
        } else {
            int totalQuantity = cartDTO.cartItems.stream().mapToInt(item -> item.quantity).sum();
            return BigDecimal.valueOf(totalQuantity * 5L);
        }
    }
}
