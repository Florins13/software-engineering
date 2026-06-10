package com.dev.cart.controller;

import com.dev.cart.controller.dto.ProductDTO;
import com.dev.cart.controller.dto.CartDTO;
import com.dev.cart.controller.dto.CheckoutDTO;
import com.dev.cart.model.Cart;
import com.dev.cart.model.CartItem;
import com.dev.cart.service.CartService;
import io.quarkus.qute.Template;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Path("/cart")
public class CartController {

    @Inject
    CartService cartService;

    @Inject
    Template cart;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCart(@HeaderParam("X-User-Id") String userId) {
        Cart cartModel = cartService.getCart(resolveUser(userId));
        return Response.ok(new CartDTO(cartModel)).build();
    }

    @GET
    @Path("/view")
    @Produces(MediaType.TEXT_HTML)
    public Response getCartView(@HeaderParam("X-User-Id") String userId) {
        Cart cartModel = cartService.getCart(resolveUser(userId));
        CartDTO cartDTO = new CartDTO(cartModel);
        return Response.ok(cart
                .data("cart", cartDTO)
                .data("bikeImageBase", "http://localhost:8082/")
                .render()).build();
    }

    @POST
    @Path("/add/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addToCart(@HeaderParam("X-User-Id") String userId, @PathParam("id") Long productId) {
        cartService.addToCart(resolveUser(userId), productId);
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
        Cart cartModel = cartService.getCart(resolveUser(userId));
        if (cartModel.isEmpty()) {
            return Response.ok(new CartDTO(cartModel)).build();
        }
        return Response.ok(new CheckoutDTO(cartModel)).build();
    }

    private String resolveUser(String userId) {
        return userId != null ? userId : "anonymous";
    }
}
