package com.dev.cart.service;

import com.dev.cart.controller.dto.ProductDTO;
import com.dev.cart.model.Cart;
import com.dev.cart.model.CartItem;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.value.ValueCommands;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Optional;

@ApplicationScoped
public class CartService {

    private static final Duration CART_TTL = Duration.ofDays(7);
    private static final String CART_KEY_PREFIX = "cart:";

    private final ValueCommands<String, String> valueCommands;

    @Inject
    ObjectMapper objectMapper;

    @Inject
    @RestClient
    BikeRestClient bikeRestClient;

    public CartService(RedisDataSource redisDataSource) {
        this.valueCommands = redisDataSource.value(String.class, String.class);
    }

    public Cart getCart(String userId) {
        String key = CART_KEY_PREFIX + userId;
        String json = valueCommands.get(key);
        if (json == null) {
            return new Cart();
        }
        try {
            return objectMapper.readValue(json, Cart.class);
        } catch (JsonProcessingException e) {
            return new Cart();
        }
    }

    public void saveCart(String userId, Cart cart) {
        String key = CART_KEY_PREFIX + userId;
        try {
            String json = objectMapper.writeValueAsString(cart);
            valueCommands.setex(key, CART_TTL.toSeconds(), json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize cart", e);
        }
    }

    public void addToCart(String userId, Long id) {
        if (id == null) {
            throw new BadRequestException("A bike id is required to add an item to the cart");
        }

        ProductDTO bike = bikeRestClient.getBikeById(id);
        if (bike == null) {
            throw new NotFoundException("Bike with id " + id + " was not found");
        }

        CartItem newItem = new CartItem(
                bike.getId(),
                bike.getModel(),
                bike.getImageSource(),
                bike.getPrice(),
                1
        );
        Cart cart = getCart(userId);
        Optional<CartItem> existing = cart.getItems().stream()
                .filter(item -> item.getId().equals(newItem.getId()))
                .findFirst();

        if (existing.isPresent()) {
            existing.get().increaseQuantity();
        } else {
            cart.getItems().add(newItem);
        }
        saveCart(userId, cart);
    }

    public void deleteCartItem(String userId, Long productId) {
        Cart cart = getCart(userId);
        cart.getItems().removeIf(item -> item.getId().equals(productId));
        saveCart(userId, cart);
    }

    public void updateQuantity(String userId, Long productId, String type) {
        Cart cart = getCart(userId);
        cart.getItems().stream()
                .filter(item -> item.getId().equals(productId))
                .findFirst()
                .ifPresent(item -> {
                    if ("increase".equals(type)) {
                        item.increaseQuantity();
                    } else if ("decrease".equals(type)) {
                        item.decreaseQuantity();
                    }
                });
        saveCart(userId, cart);
    }

    public void clearCart(String userId) {
        String key = CART_KEY_PREFIX + userId;
        valueCommands.getdel(key);
    }

    public BigDecimal calculateRentTotal(String userId) {
        return getCart(userId).getRentTotal();
    }
}
