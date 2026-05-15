package com.dev.bike.controller;

import com.dev.bike.controller.dto.BikeDTO;
import com.dev.bike.service.BikeService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.stream.Collectors;

@Path("/bikes")
public class BikeController {

    @Inject
    BikeService bikeService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllBikes() {
        List<BikeDTO> bikeDTOList = bikeService.getAllBikes().stream()
                .map(BikeDTO::new)
                .collect(Collectors.toList());
        return Response.ok(bikeDTOList).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBikeById(@PathParam("id") Long id) {
        var bike = bikeService.getBikeById(id);
        if (bike == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(new BikeDTO(bike)).build();
    }
}
