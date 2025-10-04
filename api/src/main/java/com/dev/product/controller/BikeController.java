package com.dev.product.controller;

import com.dev.product.controller.dto.BikeDTO;
import com.dev.product.service.BikeService;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Path("/bikes")
public class BikeController {

    @Inject
    BikeService bikeService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllBikes() {
        List<BikeDTO> bikeDTOList = bikeService.getAllBikes().stream().map(BikeDTO::new).collect(Collectors.toList());
        return Response.ok(bikeDTOList).build();
    }

}
