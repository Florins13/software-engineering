package com.dev.product.model;

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

    public Bike(String model, String imageSource, Integer stock, String details, BigDecimal price, boolean electric) {
        this.model = model;
        this.setImageSource(imageSource);
        this.setStock(stock);
        this.setDetails(details);
        this.setPrice(price);
        this.electric = electric;
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

    public Bike() {
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Long getId() {
        return id;
    }
}
