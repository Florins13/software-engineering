package com.dev.cart.service;

import com.dev.cart.controller.dto.ProductDTO;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/bikes")
@RegisterRestClient(configKey = "bike-service")
public interface BikeRestClient {

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    ProductDTO getBikeById(@PathParam("id") Long id);
}
