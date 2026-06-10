package com.dev.bike.event;

import com.dev.bike.service.BikeService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class StockReservationConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(StockReservationConsumer.class);

    private final BikeService bikeService;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public StockReservationConsumer(BikeService bikeService, ObjectMapper objectMapper,
                                    KafkaTemplate<String, String> kafkaTemplate) {
        this.bikeService = bikeService;
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "stock.reserve", groupId = "bike-service")
    public void onStockReserve(String payload) {
        LOG.info("Received stock.reserve event: {}", payload);
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

                LOG.info("Attempting to reserve bikeId={}, quantity={}", bikeId, quantity);
                if (bikeService.reserveStock(bikeId, quantity)) {
                    reserved.add(new ReservedItem(bikeId, quantity));
                    LOG.info("Reserved bikeId={} successfully", bikeId);
                } else {
                    allReserved = false;
                    failureReason = "Insufficient stock for bike ID " + bikeId;
                    LOG.warn("Failed to reserve bikeId={}: {}", bikeId, failureReason);
                    break;
                }
            }

            if (allReserved) {
                ObjectNode response = objectMapper.createObjectNode();
                response.put("orderId", orderId);
                response.put("success", true);
                kafkaTemplate.send("stock.reserved", objectMapper.writeValueAsString(response));
                LOG.info("Published stock.reserved for order {}", orderId);
            } else {
                for (ReservedItem ri : reserved) {
                    bikeService.releaseStock(ri.bikeId, ri.quantity);
                }
                ObjectNode response = objectMapper.createObjectNode();
                response.put("orderId", orderId);
                response.put("success", false);
                response.put("reason", failureReason);
                kafkaTemplate.send("stock.insufficient", objectMapper.writeValueAsString(response));
                LOG.warn("Published stock.insufficient for order {}: {}", orderId, failureReason);
            }
        } catch (Exception e) {
            LOG.error("Error processing stock.reserve event", e);
        }
    }

    @KafkaListener(topics = "stock.release", groupId = "bike-service")
    public void onStockRelease(String payload) {
        LOG.info("Received stock.release event: {}", payload);
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
            LOG.error("Error processing stock.release event", e);
        }
    }

    @KafkaListener(topics = "stock.confirm", groupId = "bike-service")
    public void onStockConfirm(String payload) {
        LOG.info("Received stock.confirm event: {}", payload);
        try {
            JsonNode root = objectMapper.readTree(payload);
            JsonNode items = root.get("items");

            for (JsonNode item : items) {
                long bikeId = item.get("bikeId").asLong();
                int quantity = item.get("quantity").asInt();
                bikeService.confirmReservation(bikeId, quantity);
            }
            LOG.info("Stock confirmed for order: {}", root.get("orderId").asText());
        } catch (Exception e) {
            LOG.error("Error processing stock.confirm event", e);
        }
    }

    private record ReservedItem(long bikeId, int quantity) {}
}
