package com.dev.shop.controller;


import com.dev.shop.controller.dto.CartDTO;
import com.dev.shop.controller.dto.CheckoutDTO;
import com.dev.shop.service.CartService;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/cart")
public class CartController {

    @Inject
    CartService cartServiceImpl;


    @GET
//    @RolesAllowed("BASIC")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCart() {
        CartDTO newCart = new CartDTO(cartServiceImpl.getUserCart());
        return Response.ok(newCart).build();
    }

    @POST
    @Path("/add/{id}")
//    @RolesAllowed("BASIC")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addToCart(@PathParam("id") Long id) {
        cartServiceImpl.addToCart(id);
        return Response.ok().build();
    }


    @POST
    @Path("/deleteItem/{id}")
//    @RolesAllowed("BASIC")
    @Transactional
    public Response deleteCartItem(@PathParam("id") Long id) {
        cartServiceImpl.deleteCartItem(id);
        return Response.ok().build();
    }

    @POST
    @Path("/updateQuantity/{id}/{type}")
//    @RolesAllowed("BASIC")
    @Transactional
    public Response updateQuantity(@PathParam("id") Long id, @PathParam("type") String type) throws Exception {
        System.out.println(type);
        cartServiceImpl.updateQuantity(id, type);
        return Response.ok().build();
    }


    @GET
    @Path("/checkout")
//    @RolesAllowed("BASIC")
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkout() {
        if (cartServiceImpl.getUserCart().cartIsEmpty()) return getCart();
        CheckoutDTO checkoutDTO = new CheckoutDTO(cartServiceImpl.getUserCart(), cartServiceImpl.calculateRentTotal());
        return Response.ok(checkoutDTO).build();
    }

}
