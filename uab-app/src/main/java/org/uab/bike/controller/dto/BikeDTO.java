package org.uab.bike.controller.dto;

import org.uab.bike.model.Bike;

import java.math.BigDecimal;

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

    public boolean isInStock() {
        return isInStock;
    }
}
