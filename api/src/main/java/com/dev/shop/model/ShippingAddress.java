package com.dev.shop.model;

import jakarta.persistence.*;

@Entity
@Table(name = "uab_shipping_address")
public class ShippingAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fullName;
    private String address;
    private Integer telephone;
    private String zipCode;

    public ShippingAddress() {
    }

    public ShippingAddress(String fullName, String address, Integer telephone, String zipCode) {
        this.setFullName(fullName);
        this.setAddress(address);
        this.setTelephone(telephone);
        this.setZipCode(zipCode);
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getTelephone() {
        return telephone;
    }

    public void setTelephone(Integer telephone) {
        this.telephone = telephone;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
