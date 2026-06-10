package com.dev.order.controller;

import com.dev.order.controller.dto.OrderDTO;
import com.dev.order.controller.dto.OrderRequestDTO;
import com.dev.order.model.Order;
import com.dev.order.model.ShippingItem;
import com.dev.order.service.OrderSagaOrchestrator;
import com.dev.order.service.OrderService;
import com.dev.order.service.CartRestClient;
import com.dev.order.view.OrderView;
import com.dev.cart.client.CartDTO;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import java.util.stream.Collectors;

@Path("/order")
public class OrderController {

    @Inject
    OrderService orderService;

    @Inject
    OrderSagaOrchestrator sagaOrchestrator;

    @Inject
    @RestClient
    CartRestClient cartRestClient;

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

    @GET
    @Path("/checkout/view")
    @Produces(MediaType.TEXT_HTML)
    public Response getCheckoutPage(@HeaderParam("X-User-Id") String userId) {
        String user = userId != null ? userId : "anonymous";
        CartDTO cart;
        try {
            cart = cartRestClient.getCart(user);
        } catch (Exception e) {
            cart = new CartDTO();
        }
        List<OrderDTO> orders = orderService.getOrdersByUserId(user).stream()
                .map(OrderDTO::new)
                .collect(Collectors.toList());
        OrderView.CheckoutData data = new OrderView.CheckoutData(cart, orders);
        String html = OrderView.checkoutView.render(data);
        return Response.ok(html).build();
    }

//    @GET
//    @Path("/history/view")
//    @Produces(MediaType.TEXT_HTML)
//    public Response getOrdersHtml(@HeaderParam("X-User-Id") String userId) {
//        String user = userId != null ? userId : "anonymous";
//        List<OrderDTO> orders = orderService.getOrdersByUserId(user).stream()
//                .map(OrderDTO::new)
//                .collect(Collectors.toList());
//        String html = OrderView.orderHistoryView.render(orders);
//        return Response.ok(html).build();
//    }

//    @GET
//    @Path("/history/stream")
//    @Produces(MediaType.TEXT_HTML)
//    public Response streamOrderHistory(@HeaderParam("X-User-Id") String userId) {
//        String user = userId != null ? userId : "anonymous";
//
//        StreamingOutput stream = (OutputStream output) -> {
//            Writer writer = new OutputStreamWriter(output);
//            List<Order> orders = orderService.getOrdersByUserId(user);
//
//            for (Order order : orders) {
//                String statusClass = switch (order.getOrderState()) {
//                    case CONFIRMED, COMPLETED -> "success";
//                    case FAILED, CANCELED -> "error";
//                    default -> "pending";
//                };
//
//                StringBuilder items = new StringBuilder();
//                for (ShippingItem item : order.getShippingItems()) {
//                    items.append(String.format(
//                            "<li>%s (x%d) — €%.2f</li>",
//                            item.getBikeModel(), item.getQuantity(), item.getPrice()
//                    ));
//                }
//
//                writer.write(String.format("""
//                 <div class="order-card">
//                     <div class="order-header">
//                         <span class="transaction">🧾 %s</span>
//                         <span class="status %s">%s</span>
//                     </div>
//                     <div class="order-details">
//                         <p>📦 %s | 📍 %s, %s</p>
//                         <ul>%s</ul>
//                         <p class="total">Total: €%.2f (%s)</p>
//                     </div>
//                 </div>
//                 """,
//                        order.getTransaction(),
//                        statusClass,
//                        order.getOrderState(),
//                        order.getShippingAddress().getFullName(),
//                        order.getShippingAddress().getAddress(),
//                        order.getShippingAddress().getZipCode(),
//                        items,
//                        order.getTotalPrice(),
//                        order.getAcquireType()
//                ));
//                writer.flush();
//                try {
//                    Thread.sleep(400); // simulate latency per order
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//            writer.close();
//        };
//        return Response.ok(stream).build();
//    }
}
