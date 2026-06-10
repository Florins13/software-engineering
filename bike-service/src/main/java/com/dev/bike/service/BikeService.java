package com.dev.bike.service;

import com.dev.bike.model.Bike;
import com.dev.bike.repository.BikeRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BikeService {

    private static final Logger LOG = LoggerFactory.getLogger(BikeService.class);

    private final BikeRepository bikeRepository;
    private final EntityManager entityManager;

    public BikeService(BikeRepository bikeRepository, EntityManager entityManager) {
        this.bikeRepository = bikeRepository;
        this.entityManager = entityManager;
    }

    public List<Bike> getAllBikes() {
        return bikeRepository.findAllByOrderByIdAsc();
    }

    public Bike getBikeById(Long id) {
        return bikeRepository.findById(id).orElse(null);
    }

    @Transactional
    public boolean reserveStock(Long bikeId, int quantity) {
        LOG.info("reserveStock called: bikeId={}, quantity={}", bikeId, quantity);
        Bike bike = entityManager.find(Bike.class, bikeId, LockModeType.PESSIMISTIC_WRITE);
        if (bike == null) {
            LOG.error("Bike not found with id={}", bikeId);
            return false;
        }
        LOG.info("Bike found: id={}, model={}, availableStock={}, reservedStock={}",
                bike.getId(), bike.getModel(), bike.getAvailableStock(), bike.getReservedStock());
        if (bike.getAvailableStock() >= quantity) {
            bike.setAvailableStock(bike.getAvailableStock() - quantity);
            bike.setReservedStock(bike.getReservedStock() + quantity);
            LOG.info("Stock reserved successfully for bikeId={}", bikeId);
            return true;
        }
        LOG.warn("Insufficient stock for bikeId={}: available={}, requested={}",
                bikeId, bike.getAvailableStock(), quantity);
        return false;
    }

    @Transactional
    public void confirmReservation(Long bikeId, int quantity) {
        Bike bike = entityManager.find(Bike.class, bikeId, LockModeType.PESSIMISTIC_WRITE);
        if (bike != null) {
            bike.setReservedStock(bike.getReservedStock() - quantity);
        }
    }

    @Transactional
    public void releaseStock(Long bikeId, int quantity) {
        Bike bike = entityManager.find(Bike.class, bikeId, LockModeType.PESSIMISTIC_WRITE);
        if (bike != null) {
            bike.setReservedStock(bike.getReservedStock() - quantity);
            bike.setAvailableStock(bike.getAvailableStock() + quantity);
        }
    }

    public BikeRepository getBikeRepository() {
        return bikeRepository;
    }
}
