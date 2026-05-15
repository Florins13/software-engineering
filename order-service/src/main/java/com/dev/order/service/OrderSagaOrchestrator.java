package com.dev.order.service;

import com.dev.order.model.Order;
import com.dev.order.model.OrderState;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.context.ManagedExecutor;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;

@ApplicationScoped
public class OrderSagaOrchestrator {

    private static final Logger LOG = Logger.getLogger(OrderSagaOrchestrator.class);

    @Inject
    ObjectMapper objectMapper;

    @Inject
    OrderService orderService;

    @Inject
    OrderSseManager sseManager;

    @Inject
    ManagedExecutor executor;

    @Channel("stock-reserve")
    Emitter<String> stockReserveEmitter;

    @Channel("cart-clear")
    Emitter<String> cartClearEmitter;

    @Channel("stock-confirm")
    Emitter<String> stockConfirmEmitter;

    public void startStockReservation(Order order, String userId) {
        LOG.infof("Starting stock reservation saga for order %s", order.getTransaction());
        try {
            ObjectNode payload = objectMapper.createObjectNode();
            payload.put("orderId", order.getTransaction());

            ArrayNode items = payload.putArray("items");
            order.getShippingItems().forEach(item -> {
                ObjectNode itemNode = items.addObject();
                itemNode.put("bikeId", item.getBikeId());
                itemNode.put("quantity", item.getQuantity());
            });

            String msg = objectMapper.writeValueAsString(payload);
            LOG.infof("Publishing stock.reserve: %s", msg);
            stockReserveEmitter.send(msg);
            LOG.info("stock.reserve published successfully");
        } catch (Exception e) {
            LOG.errorf(e, "Failed to start stock reservation saga");
            orderService.updateOrderState(order.getTransaction(), OrderState.FAILED);
            sseManager.broadcast(order.getTransaction(), OrderState.FAILED, "Failed to initiate stock check");
        }
    }

    public void onStockReserved(String orderId, String userId) {
        LOG.infof("Stock reserved for order %s — confirming", orderId);
        orderService.updateOrderState(orderId, OrderState.CONFIRMED);
        sseManager.broadcast(orderId, OrderState.CONFIRMED, null);

        publishStockConfirm(orderId);

        try {
            ObjectNode payload = objectMapper.createObjectNode();
            payload.put("userId", userId);
            cartClearEmitter.send(objectMapper.writeValueAsString(payload));
            LOG.infof("Published cart.clear for userId=%s", userId);
        } catch (Exception e) {
            LOG.errorf(e, "Failed to clear cart");
        }

        simulateOrderProgression(orderId);
    }

    public void onStockInsufficient(String orderId, String reason) {
        LOG.warnf("Stock insufficient for order %s: %s", orderId, reason);
        orderService.updateOrderState(orderId, OrderState.FAILED);
        sseManager.broadcast(orderId, OrderState.FAILED, reason);
    }

    private void publishStockConfirm(String orderId) {
        try {
            Order order = orderService.findByTransaction(orderId);
            if (order == null) return;

            ObjectNode payload = objectMapper.createObjectNode();
            payload.put("orderId", orderId);

            ArrayNode items = payload.putArray("items");
            order.getShippingItems().forEach(item -> {
                ObjectNode itemNode = items.addObject();
                itemNode.put("bikeId", item.getBikeId());
                itemNode.put("quantity", item.getQuantity());
            });

            stockConfirmEmitter.send(objectMapper.writeValueAsString(payload));
            LOG.infof("Published stock.confirm for order %s", orderId);
        } catch (Exception e) {
            LOG.errorf(e, "Failed to publish stock.confirm");
        }
    }

    private void simulateOrderProgression(String orderId) {
        executor.submit(() -> {
            try {
                Thread.sleep(3000);
                orderService.updateOrderState(orderId, OrderState.PROCESSING);
                sseManager.broadcast(orderId, OrderState.PROCESSING, null);

                Thread.sleep(3000);
                orderService.updateOrderState(orderId, OrderState.SHIPPING);
                sseManager.broadcast(orderId, OrderState.SHIPPING, null);

                Thread.sleep(3000);
                orderService.updateOrderState(orderId, OrderState.COMPLETED);
                sseManager.broadcast(orderId, OrderState.COMPLETED, null);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }
}
