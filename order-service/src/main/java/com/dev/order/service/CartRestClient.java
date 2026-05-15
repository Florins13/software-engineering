package com.dev.order.service;

import com.dev.cart.client.CartDTO;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/cart")
@RegisterRestClient(configKey = "cart-service")
public interface CartRestClient {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    CartDTO getCart(@HeaderParam("X-User-Id") String userId);
}
