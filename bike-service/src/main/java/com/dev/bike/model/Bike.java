package com.dev.bike.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "uab_bike")
public class Bike extends Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String model;
    private boolean electric;

    public Bike(String model, String imageSource, Integer availableStock, String details, BigDecimal price, boolean electric) {
        this.model = model;
        this.setImageSource(imageSource);
        this.setAvailableStock(availableStock);
        this.setReservedStock(0);
        this.setDetails(details);
        this.setPrice(price);
        this.electric = electric;
    }

    public Bike() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public boolean isElectric() {
        return electric;
    }

    public void setElectric(boolean electric) {
        this.electric = electric;
    }
}
