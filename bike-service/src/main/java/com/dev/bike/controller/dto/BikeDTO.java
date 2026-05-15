package com.dev.bike.controller.dto;

import com.dev.bike.model.Bike;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BikeDTO {
    public Long id;
    public String model;
    public boolean electric;
    public String imageSource;
    public String details;
    public Integer availableStock;
    public BigDecimal price;
    public boolean inStock;

    public BikeDTO(Bike bike) {
        this.id = bike.getId();
        this.model = bike.getModel();
        this.electric = bike.isElectric();
        this.imageSource = bike.getImageSource();
        this.details = bike.getDetails();
        this.availableStock = bike.getAvailableStock();
        this.price = bike.getPrice();
        this.inStock = bike.isInStock();
    }
}
