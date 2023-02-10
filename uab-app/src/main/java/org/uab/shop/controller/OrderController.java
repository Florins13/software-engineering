package org.uab.shop.controller;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import org.uab.shop.controller.dto.OrderDTO;
import org.uab.shop.model.Order;
import org.uab.shop.service.OrderService;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.stream.Collectors;

@Path("/order")
public class OrderController {
    @Inject
    OrderService orderServiceImpl;
    @Inject
    Template order;

    @Inject
    Template orders;
    @Inject
    Template notification;

    @POST
    @Path("/finalise")
    @RolesAllowed("BASIC")
    @Transactional
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public TemplateInstance order(@FormParam("fullName") String fullName,
                                  @FormParam("address") String address,
                                  @FormParam("telephone") Integer telephone,
                                  @FormParam("zipCode") String zipCode,
                                  @FormParam("acquire") String acquireType) throws Exception {
        if (orderServiceImpl.getUserCart().cartIsEmpty())
            return notification.data("role", orderServiceImpl.getUserRole(), "message", "Your cart is empty, please add items and try again!");
        Order newOrder = orderServiceImpl.placeOrder(fullName, address, telephone, zipCode, acquireType);
        return order.data("newOrder", new OrderDTO(newOrder), "role", orderServiceImpl.getUserRole());
    }

    @GET
    @Path("/history")
    @RolesAllowed("BASIC")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance getOrders() {
        List<OrderDTO> orderDTOList = orderServiceImpl.getOrders().stream().map(order -> new OrderDTO(order)).collect(Collectors.toList());
        return orders.data("role", orderServiceImpl.getUserRole(), "orders", orderDTOList);
    }
}
