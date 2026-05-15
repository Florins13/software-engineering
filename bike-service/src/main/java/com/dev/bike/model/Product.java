package com.dev.bike.model;

import jakarta.persistence.MappedSuperclass;

import java.math.BigDecimal;

@MappedSuperclass
public abstract class Product {
    private String imageSource;
    private String details;
    private Integer availableStock;
    private Integer reservedStock = 0;
    private BigDecimal price;

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        if (price.compareTo(BigDecimal.ZERO) > 0) this.price = price;
    }

    public String getImageSource() {
        return imageSource;
    }

    public void setImageSource(String imagePath) {
        this.imageSource = imagePath;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Integer getAvailableStock() {
        return availableStock;
    }

    public void setAvailableStock(Integer availableStock) {
        if (availableStock >= 0) this.availableStock = availableStock;
    }

    public Integer getReservedStock() {
        return reservedStock;
    }

    public void setReservedStock(Integer reservedStock) {
        if (reservedStock >= 0) this.reservedStock = reservedStock;
    }

    public boolean isInStock() {
        return this.availableStock > 0;
    }
}
