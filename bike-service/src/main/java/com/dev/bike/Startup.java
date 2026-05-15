package com.dev.bike;

import com.dev.bike.model.Bike;
import com.dev.bike.service.BikeService;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Singleton;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.math.BigDecimal;

@Singleton
public class Startup {

    private static final Logger LOG = Logger.getLogger(Startup.class);

    @Inject
    BikeService bikeService;

    @Transactional
    public void loadData(@Observes StartupEvent evt) {
        LOG.info("Seeding bike data...");
        bikeService.getBikeRepository().deleteAll();
        bikeService.getBikeRepository().persist(
                new Bike("CITY STAR ST 500", "bike_one.jpg", 100, "red, medium", new BigDecimal("24.99"), false));
        bikeService.getBikeRepository().persist(
                new Bike("test", "bike_two.jpg", 5, "black, short", new BigDecimal("50"), true));
        bikeService.getBikeRepository().persist(
                new Bike("ROCK STAR FS 400", "bike_three.jpg", 12, "red, medium", new BigDecimal("115.50"), false));
        bikeService.getBikeRepository().persist(
                new Bike("ROCK STAR FS 500", "bike_three.jpg", 2, "red, medium", new BigDecimal("115.50"), false));
        long count = bikeService.getBikeRepository().count();
        LOG.infof("Bike data seeded successfully. Total bikes: %d", count);
    }
}
