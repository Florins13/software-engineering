package com.dev.product.repository;

import com.dev.product.model.Bike;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class BikeRepository implements PanacheRepository<Bike> {

    public Bike getBikeById(Long id) {
        return this.findById(id);
    }

    public List<Bike> getAllBikes() {
        return this.findAll(Sort.by("id")).list();
    }
}
