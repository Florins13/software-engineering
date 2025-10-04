package com.dev.product.controller.dto;


import com.dev.product.model.Bike;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BikeDTO {
    public Long id;
    public String model;
    public boolean electric;
    public String imageSource;
    public String details;
    public Integer stock;
    public BigDecimal price;
    public boolean isInStock;


    public BikeDTO(Bike bike) {
        this.id = bike.getId();
        this.model = bike.getModel();
        this.electric = bike.isElectric();
        this.imageSource = bike.getImageSource();
        this.details = bike.getDetails();
        this.stock = bike.getStock();
        this.price = bike.getPrice();
        this.isInStock = bike.isInStock();
    }

    public Long getId() {
        return id;
    }

    public String getModel() {
        return model;
    }

    public boolean isElectric() {
        return electric;
    }

    public String getImageSource() {
        return imageSource;
    }

    public String getDetails() {
        return details;
    }

    public Integer getStock() {
        return stock;
    }

    public BigDecimal getPrice() {
        return price;
    }

    @JsonIgnore
    public boolean isInStock() {
        return isInStock;
    }
}
