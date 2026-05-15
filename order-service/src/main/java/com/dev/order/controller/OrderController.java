package com.dev.order.controller;

import com.dev.order.controller.dto.OrderDTO;
import com.dev.order.controller.dto.OrderRequestDTO;
import com.dev.order.model.Order;
import com.dev.order.service.OrderSagaOrchestrator;
import com.dev.order.service.OrderService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.stream.Collectors;

@Path("/order")
public class OrderController {

    @Inject
    OrderService orderService;

    @Inject
    OrderSagaOrchestrator sagaOrchestrator;

    @POST
    @Path("/finalise")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response placeOrder(@HeaderParam("X-User-Id") String userId, OrderRequestDTO request) {
        try {
            String user = userId != null ? userId : "anonymous";
            Order order = orderService.placeOrder(
                    user,
                    request.getFullName(),
                    request.getAddress(),
                    request.getTelephone(),
                    request.getZipCode(),
                    request.getAcquireType()
            );

            // Start saga AFTER the transaction has committed
            sagaOrchestrator.startStockReservation(order, user);

            return Response.accepted(new OrderDTO(order)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @GET
    @Path("/history")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrders(@HeaderParam("X-User-Id") String userId) {
        String user = userId != null ? userId : "anonymous";
        List<OrderDTO> orders = orderService.getOrdersByUserId(user).stream()
                .map(OrderDTO::new)
                .collect(Collectors.toList());
        return Response.ok(orders).build();
    }
}
