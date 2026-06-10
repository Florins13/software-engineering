package com.dev.bike;

import com.dev.bike.model.Bike;
import com.dev.bike.repository.BikeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class Startup implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(Startup.class);

    private final BikeRepository bikeRepository;

    public Startup(BikeRepository bikeRepository) {
        this.bikeRepository = bikeRepository;
    }

    @Override
    public void run(String... args) {
        LOG.info("Seeding bike data...");
        bikeRepository.deleteAll();
        bikeRepository.save(
                new Bike("CITY STAR ST 500", "bike_one.jpg", 100, "red, medium", new BigDecimal("24.99"), false));
        bikeRepository.save(
                new Bike("test", "bike_two.jpg", 5, "black, short", new BigDecimal("50"), true));
        bikeRepository.save(
                new Bike("ROCK STAR FS 400", "bike_three.jpg", 12, "red, medium", new BigDecimal("115.50"), false));
        bikeRepository.save(
                new Bike("ROCK STAR FS 500", "bike_three.jpg", 2, "red, medium", new BigDecimal("115.50"), false));
        long count = bikeRepository.count();
        LOG.info("Bike data seeded successfully. Total bikes: {}", count);
    }
}
