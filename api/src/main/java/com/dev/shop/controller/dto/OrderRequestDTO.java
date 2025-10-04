package com.dev.shop.controller.dto;


public class OrderRequestDTO {
    private String fullName;
    private String address;
    private int telephone;
    private String zipCode;
    private String acquireType;

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

    public int getTelephone() {
        return telephone;
    }

    public void setTelephone(int telephone) {
        this.telephone = telephone;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getAcquireType() {
        return acquireType;
    }

    public void setAcquireType(String acquireType) {
        this.acquireType = acquireType;
    }
}
