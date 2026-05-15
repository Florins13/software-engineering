package com.dev.order.service;

import com.dev.order.model.OrderState;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.subscription.MultiEmitter;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Manages SSE connections for order status updates.
 * Each transaction can have multiple SSE subscribers.
 */
@ApplicationScoped
public class OrderSseManager {

    private final ConcurrentHashMap<String, List<MultiEmitter<? super OrderStatusEvent>>> emitters =
            new ConcurrentHashMap<>();

    /**
     * Creates a Multi stream for a given transaction.
     * The client subscribes to this to receive order status updates.
     */
    public Multi<OrderStatusEvent> subscribe(String transactionId) {
        return Multi.createFrom().emitter(emitter -> {
            emitters.computeIfAbsent(transactionId, k -> new CopyOnWriteArrayList<>()).add(emitter);
            emitter.onTermination(() -> {
                List<MultiEmitter<? super OrderStatusEvent>> list = emitters.get(transactionId);
                if (list != null) {
                    list.remove(emitter);
                    if (list.isEmpty()) {
                        emitters.remove(transactionId);
                    }
                }
            });
        });
    }

    /**
     * Broadcasts an order status update to all SSE subscribers for a transaction.
     */
    public void broadcast(String transactionId, OrderState state, String message) {
        List<MultiEmitter<? super OrderStatusEvent>> list = emitters.get(transactionId);
        if (list != null) {
            OrderStatusEvent event = new OrderStatusEvent(transactionId, state, message);
            for (MultiEmitter<? super OrderStatusEvent> emitter : list) {
                emitter.emit(event);
                // Complete the stream on terminal states
                if (state == OrderState.COMPLETED || state == OrderState.FAILED || state == OrderState.CANCELED) {
                    emitter.complete();
                }
            }
        }
    }
}
