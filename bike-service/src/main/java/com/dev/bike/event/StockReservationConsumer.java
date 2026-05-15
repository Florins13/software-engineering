package com.dev.bike.event;

import com.dev.bike.service.BikeService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class StockReservationConsumer {

    private static final Logger LOG = Logger.getLogger(StockReservationConsumer.class);

    @Inject
    BikeService bikeService;

    @Inject
    ObjectMapper objectMapper;

    @Channel("stock-reserved")
    Emitter<String> stockReservedEmitter;

    @Channel("stock-insufficient")
    Emitter<String> stockInsufficientEmitter;

    @Incoming("stock-reserve")
    public void onStockReserve(String payload) {
        LOG.infof("Received stock.reserve event: %s", payload);
        try {
            JsonNode root = objectMapper.readTree(payload);
            String orderId = root.get("orderId").asText();
            JsonNode items = root.get("items");

            List<ReservedItem> reserved = new ArrayList<>();
            boolean allReserved = true;
            String failureReason = "";

            for (JsonNode item : items) {
                long bikeId = item.get("bikeId").asLong();
                int quantity = item.get("quantity").asInt();

                LOG.infof("Attempting to reserve bikeId=%d, quantity=%d", bikeId, quantity);
                if (bikeService.reserveStock(bikeId, quantity)) {
                    reserved.add(new ReservedItem(bikeId, quantity));
                    LOG.infof("Reserved bikeId=%d successfully", bikeId);
                } else {
                    allReserved = false;
                    failureReason = "Insufficient stock for bike ID " + bikeId;
                    LOG.warnf("Failed to reserve bikeId=%d: %s", bikeId, failureReason);
                    break;
                }
            }

            if (allReserved) {
                ObjectNode response = objectMapper.createObjectNode();
                response.put("orderId", orderId);
                response.put("success", true);
                stockReservedEmitter.send(objectMapper.writeValueAsString(response));
                LOG.infof("Published stock.reserved for order %s", orderId);
            } else {
                for (ReservedItem ri : reserved) {
                    bikeService.releaseStock(ri.bikeId, ri.quantity);
                }
                ObjectNode response = objectMapper.createObjectNode();
                response.put("orderId", orderId);
                response.put("success", false);
                response.put("reason", failureReason);
                stockInsufficientEmitter.send(objectMapper.writeValueAsString(response));
                LOG.warnf("Published stock.insufficient for order %s: %s", orderId, failureReason);
            }
        } catch (Exception e) {
            LOG.errorf(e, "Error processing stock.reserve event");
        }
    }

    @Incoming("stock-release")
    public void onStockRelease(String payload) {
        LOG.infof("Received stock.release event: %s", payload);
        try {
            JsonNode root = objectMapper.readTree(payload);
            JsonNode items = root.get("items");

            for (JsonNode item : items) {
                long bikeId = item.get("bikeId").asLong();
                int quantity = item.get("quantity").asInt();
                bikeService.releaseStock(bikeId, quantity);
            }
            LOG.info("Stock released successfully");
        } catch (Exception e) {
            LOG.errorf(e, "Error processing stock.release event");
        }
    }

    @Incoming("stock-confirm")
    public void onStockConfirm(String payload) {
        LOG.infof("Received stock.confirm event: %s", payload);
        try {
            JsonNode root = objectMapper.readTree(payload);
            JsonNode items = root.get("items");

            for (JsonNode item : items) {
                long bikeId = item.get("bikeId").asLong();
                int quantity = item.get("quantity").asInt();
                bikeService.confirmReservation(bikeId, quantity);
            }
            LOG.infof("Stock confirmed for order: %s", root.get("orderId").asText());
        } catch (Exception e) {
            LOG.errorf(e, "Error processing stock.confirm event");
        }
    }

    private record ReservedItem(long bikeId, int quantity) {}
}
