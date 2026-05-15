package com.dev.order.event;

import com.dev.order.service.OrderSagaOrchestrator;
import com.dev.order.service.OrderService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

@ApplicationScoped
public class OrderEventConsumer {

    private static final Logger LOG = Logger.getLogger(OrderEventConsumer.class);

    @Inject
    ObjectMapper objectMapper;

    @Inject
    OrderSagaOrchestrator sagaOrchestrator;

    @Inject
    OrderService orderService;

    @Incoming("stock-reserved")
    public void onStockReserved(String payload) {
        LOG.infof("Received stock.reserved event: %s", payload);
        try {
            JsonNode root = objectMapper.readTree(payload);
            String orderId = root.get("orderId").asText();

            String userId = orderService.findUserIdByTransaction(orderId);
            LOG.infof("Stock reserved for order %s, userId=%s", orderId, userId);

            sagaOrchestrator.onStockReserved(orderId, userId);
        } catch (Exception e) {
            LOG.errorf(e, "Error processing stock.reserved event");
        }
    }

    @Incoming("stock-insufficient")
    public void onStockInsufficient(String payload) {
        LOG.warnf("Received stock.insufficient event: %s", payload);
        try {
            JsonNode root = objectMapper.readTree(payload);
            String orderId = root.get("orderId").asText();
            String reason = root.has("reason") ? root.get("reason").asText() : "Insufficient stock";

            sagaOrchestrator.onStockInsufficient(orderId, reason);
        } catch (Exception e) {
            LOG.errorf(e, "Error processing stock.insufficient event");
        }
    }
}
