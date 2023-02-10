package org.uab.bike.service;

import org.uab.bike.model.Bike;
import org.uab.bike.repository.BikeRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.math.BigDecimal;
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

    public Bike editBike(Long id, String model, String imageSource, Integer stock, String details, boolean electric, BigDecimal price) {
        Bike editBike = getBikeById(id);
        editBike.setModel(model);
        editBike.setImageSource(imageSource);
        editBike.setStock(stock);
        editBike.setDetails(details);
        editBike.setElectric(electric);
        editBike.setPrice(price);
        this.getBikeRepository().persist(editBike);
        return editBike;
    }

}
