package com.dev.bike.service;

import com.dev.bike.model.Bike;
import com.dev.bike.repository.BikeRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.List;

@ApplicationScoped
public class BikeService {

    private static final Logger LOG = Logger.getLogger(BikeService.class);

    @Inject
    BikeRepository bikeRepository;

    @Inject
    EntityManager entityManager;

    public List<Bike> getAllBikes() {
        return bikeRepository.getAllBikes();
    }

    public Bike getBikeById(Long id) {
        return bikeRepository.getBikeById(id);
    }

    @Transactional
    public boolean reserveStock(Long bikeId, int quantity) {
        LOG.infof("reserveStock called: bikeId=%d, quantity=%d", bikeId, quantity);
        Bike bike = entityManager.find(Bike.class, bikeId, LockModeType.PESSIMISTIC_WRITE);
        if (bike == null) {
            LOG.errorf("Bike not found with id=%d", bikeId);
            return false;
        }
        LOG.infof("Bike found: id=%d, model=%s, availableStock=%d, reservedStock=%d",
                bike.getId(), bike.getModel(), bike.getAvailableStock(), bike.getReservedStock());
        if (bike.getAvailableStock() >= quantity) {
            bike.setAvailableStock(bike.getAvailableStock() - quantity);
            bike.setReservedStock(bike.getReservedStock() + quantity);
            LOG.infof("Stock reserved successfully for bikeId=%d", bikeId);
            return true;
        }
        LOG.warnf("Insufficient stock for bikeId=%d: available=%d, requested=%d",
                bikeId, bike.getAvailableStock(), quantity);
        return false;
    }

    /**
     * Confirms a reservation — the sale is final.
     * Removes quantity from reservedStock (bikes are sold).
     */
    @Transactional
    public void confirmReservation(Long bikeId, int quantity) {
        Bike bike = entityManager.find(Bike.class, bikeId, LockModeType.PESSIMISTIC_WRITE);
        if (bike != null) {
            bike.setReservedStock(bike.getReservedStock() - quantity);
        }
    }

    /**
     * Releases a reservation — saga rollback / compensation.
     * Moves quantity from reservedStock back to availableStock.
     */
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
