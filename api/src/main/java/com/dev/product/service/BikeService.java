package com.dev.product.service;

import com.dev.product.model.Bike;
import com.dev.product.repository.BikeRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class BikeService {

    @Inject
    BikeRepository bikeRepository;

    public BikeRepository getBikeRepository() {
        return bikeRepository;
    }

    public List<Bike> getAllBikes() {
        return this.bikeRepository.getAllBikes();
    }

    public Bike getBikeById(Long id) {
        return this.getBikeRepository().getBikeById(id);
    }

}
