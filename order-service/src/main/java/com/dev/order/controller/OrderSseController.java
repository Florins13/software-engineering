package com.dev.order.controller;

import com.dev.order.service.OrderSseManager;
import com.dev.order.service.OrderStatusEvent;
import io.smallrye.mutiny.Multi;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.RestStreamElementType;

@Path("/order/status")
public class OrderSseController {

    @Inject
    OrderSseManager sseManager;

    @GET
    @Path("/{transactionId}")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @RestStreamElementType(MediaType.APPLICATION_JSON)
    public Multi<OrderStatusEvent> streamOrderStatus(@PathParam("transactionId") String transactionId) {
        return sseManager.subscribe(transactionId);
    }
}
