package com.dev.cart.event;

import com.dev.cart.service.CartService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class CartEventConsumer {

    @Inject
    CartService cartService;

    @Inject
    ObjectMapper objectMapper;

    /**
     * Consumes cart.clear events from Order Service.
     * Payload: { "userId": "..." }
     */
    @Incoming("cart-clear")
    public void onCartClear(String payload) {
        try {
            JsonNode root = objectMapper.readTree(payload);
            String userId = root.get("userId").asText();
            cartService.clearCart(userId);
            System.out.println("Cart cleared for user: " + userId);
        } catch (Exception e) {
            System.err.println("Error processing cart.clear event: " + e.getMessage());
        }
    }
}
