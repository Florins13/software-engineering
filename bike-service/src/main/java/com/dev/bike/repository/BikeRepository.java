package com.dev.bike.repository;

import com.dev.bike.model.Bike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BikeRepository extends JpaRepository<Bike, Long> {
    List<Bike> findAllByOrderByIdAsc();
}
