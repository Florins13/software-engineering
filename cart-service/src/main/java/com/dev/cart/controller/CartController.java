package com.dev.cart.controller;

import com.dev.cart.controller.dto.ProductDTO;
import com.dev.cart.controller.dto.CartDTO;
import com.dev.cart.controller.dto.CheckoutDTO;
import com.dev.cart.model.Cart;
import com.dev.cart.model.CartItem;
import com.dev.cart.service.CartService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/cart")
public class CartController {

    @Inject
    CartService cartService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCart(@HeaderParam("X-User-Id") String userId) {
        Cart cart = cartService.getCart(resolveUser(userId));
        return Response.ok(new CartDTO(cart)).build();
    }

    @POST
    @Path("/add")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addToCart(@HeaderParam("X-User-Id") String userId, ProductDTO request) {
        cartService.addToCart(resolveUser(userId), request);
        return Response.ok().build();
    }

    @POST
    @Path("/delete/{id}")
    public Response deleteCartItem(@HeaderParam("X-User-Id") String userId, @PathParam("id") Long productId) {
        cartService.deleteCartItem(resolveUser(userId), productId);
        return Response.ok().build();
    }

    @POST
    @Path("/updateQuantity/{id}/{type}")
    public Response updateQuantity(
            @HeaderParam("X-User-Id") String userId,
            @PathParam("id") Long productId,
            @PathParam("type") String type) {
        cartService.updateQuantity(resolveUser(userId), productId, type);
        return Response.ok().build();
    }

    @GET
    @Path("/checkout")
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkout(@HeaderParam("X-User-Id") String userId) {
        Cart cart = cartService.getCart(resolveUser(userId));
        if (cart.isEmpty()) {
            return Response.ok(new CartDTO(cart)).build();
        }
        return Response.ok(new CheckoutDTO(cart)).build();
    }

    private String resolveUser(String userId) {
        return userId != null ? userId : "anonymous";
    }
}
