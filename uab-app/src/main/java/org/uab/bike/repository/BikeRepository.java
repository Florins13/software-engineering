package org.uab.bike.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.uab.bike.model.Bike;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class BikeRepository implements PanacheRepository<Bike> {

    public Bike getBikeById(Long id) {
        return this.findById(id);
    }

    public List<Bike> getAllBikes() {
        return this.findAll().list();
    }
}
