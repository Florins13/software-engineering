package com.dev.product.model;

import jakarta.persistence.MappedSuperclass;

import java.math.BigDecimal;

@MappedSuperclass
public abstract class Product {
    private String imageSource;
    private String details;
    private Integer stock;
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

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        if (stock >= 0) this.stock = stock;
    }

    public boolean isInStock() {
        return this.stock > 0;
    }
}
