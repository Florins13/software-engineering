package org.uab.shop.service;

import org.uab.bike.model.Bike;
import org.uab.shop.model.*;
import org.uab.shop.repository.CartItemRepository;
import org.uab.shop.repository.OrderRepository;
import org.uab.user.service.UserServiceImpl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class OrderServiceImpl implements OrderService {
    @Inject
    OrderRepository orderRepository;

    @Inject
    UserServiceImpl userServiceImpl;

    @Inject
    CartItemRepository cartItemRepository;

    @Override
    public List<Order> getOrders() {
        return orderRepository.getAllOrders();
    }

    @Override
    public Order placeOrder(String fullName, String address, Integer telephone, String zipCode, String acquireType) throws Exception {
        Order newOrder = createOrder(fullName, address, telephone, zipCode, acquireType);
        updateStock(newOrder.getShippingItems());
        // Empty out the cart of user after placing order.
        cartItemRepository.deleteCartItemById(getUserCart().getId());
        return newOrder;
    }

    @Override
    public Order createOrder(String fullName, String address, Integer telephone, String zipCode, String acquireType) {
        AcquireType acquire = acquireType.equals("rent") ? AcquireType.RENT : AcquireType.BUY;
        ShippingAddress shippingAddress = new ShippingAddress(fullName, address, telephone, zipCode);
        Order newOrder = new Order(userServiceImpl.getCurrentUser(), shippingAddress, acquire, OrderState.NEW, getPriceBasedOnAcquireType(acquire));
        newOrder.setShippingItems(createShippingItems(getUserCart().getCartItems(), newOrder));
        orderRepository.save(newOrder);
        return newOrder;
    }

    @Override
    public List<ShippingItem> createShippingItems(List<CartItem> cartItems, Order order) {
        List<ShippingItem> shippingItems;
        shippingItems = cartItems.stream().map(cartItem -> new ShippingItem(cartItem.getBike(), cartItem.getQuantity(), order)).collect(Collectors.toList());
        return shippingItems;
    }

    @Override
    public List<Bike> updateStock(List<ShippingItem> shippingItems) {
        shippingItems.forEach(shippingItem -> shippingItem.getBike().setStock(shippingItem.getBike().getStock() - shippingItem.getQuantity()));
        return shippingItems.stream().map(shippingItem -> shippingItem.getBike()).collect(Collectors.toList());
    }

    @Override
    public BigDecimal getPriceBasedOnAcquireType(AcquireType acquire) {
        if (acquire.equals(AcquireType.BUY)) {
            return getUserCart().getTotal();
        } else
            return BigDecimal.valueOf(getUserCart().getCartItems().stream().mapToInt(cartItem -> cartItem.getQuantity()).sum() * 5L);
    }

    @Override
    public String getUserRole() {
        return userServiceImpl.getUserRole();
    }

    @Override
    public Cart getUserCart() {
        return userServiceImpl.getUserCart();
    }


}
