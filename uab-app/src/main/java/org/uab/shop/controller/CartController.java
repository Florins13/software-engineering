package org.uab.shop.controller;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import org.uab.shop.controller.dto.CartDTO;
import org.uab.shop.controller.dto.CheckoutDTO;
import org.uab.shop.service.CartService;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/cart")
public class CartController {

    @Inject
    CartService cartServiceImpl;

    @Inject
    Template cart;

    @Inject
    Template checkout;

    @GET
    @RolesAllowed("BASIC")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance getCart() {
        return cart.data("role", cartServiceImpl.getUserRole(), "cart", new CartDTO(cartServiceImpl.getUserCart()));
    }

    @POST
    @Path("/add/{id}")
    @RolesAllowed("BASIC")
    @Transactional
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public void getAllBikes(@PathParam("id") Long id) {
        cartServiceImpl.addToCart(id);
    }


    @POST
    @Path("/updateQuantity/{type}")
    @RolesAllowed("BASIC")
    @Transactional
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public TemplateInstance updateQuantity(@FormParam("id") Long id, @PathParam("type") String type) throws Exception {
        System.out.println(type);
        cartServiceImpl.updateQuantity(id, type);
        return getCart();
    }

    @POST
    @Path("/deleteItem")
    @RolesAllowed("BASIC")
    @Transactional
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public TemplateInstance deleteCartItem(@FormParam("id") Long id) {
        cartServiceImpl.deleteCartItem(id);
        return getCart();
    }

    @GET
    @Path("/checkout")
    @RolesAllowed("BASIC")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance checkout() {
        if (cartServiceImpl.getUserCart().cartIsEmpty()) return getCart();
        return checkout.data("role", cartServiceImpl.getUserRole(), "checkout", new CheckoutDTO(cartServiceImpl.getUserCart(), cartServiceImpl.calculateRentTotal()));
    }


}
