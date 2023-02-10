package org.uab.bike.controller;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import org.uab.bike.controller.dto.BikeDTO;
import org.uab.bike.model.Bike;
import org.uab.bike.service.BikeService;
import org.uab.user.service.UserServiceImpl;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Path("/bikes")
public class BikeController {

    @Inject
    BikeService bikeService;

    @Inject
    UserServiceImpl userServiceImpl;
    @Inject
    Template bikes;

    @Inject
    Template bike;

    @GET
    @PermitAll
    @Transactional
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance getAllBikes() {
        List<BikeDTO> bikeDTOList = bikeService.getAllBikes().stream().map(bike -> new BikeDTO(bike)).collect(Collectors.toList());
        return bikes.data("bikes", bikeDTOList, "role", userServiceImpl.getUserRole());
    }

    @GET
    @Path("/show/{id}")
    @RolesAllowed("MANAGER")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance showBike(@PathParam("id") Long id) {
        return bike.data("bike", new BikeDTO(bikeService.getBikeById(id)), "role", userServiceImpl.getUserRole());
    }

    @POST
    @Path("/edit")
    @RolesAllowed("MANAGER")
    @Transactional
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance Bike(@FormParam("bikeid") Long id,
                                 @FormParam("model") String model,
                                 @FormParam("imageSource") String imageSource,
                                 @FormParam("stock") Integer stock,
                                 @FormParam("details") String details,
                                 @FormParam("electric") boolean electric,
                                 @FormParam("price") BigDecimal price) {
        Bike editedBike = bikeService.editBike(id, model, imageSource, stock, details, electric, price);
        return this.bike.data("bike", new BikeDTO(editedBike), "role", userServiceImpl.getUserRole());
    }


}
