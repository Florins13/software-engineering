package com.dev.order.repository;

import com.dev.order.model.Order;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class OrderRepository implements PanacheRepository<Order> {

    public void save(Order order) {
        this.persist(order);
    }

    public List<Order> getOrdersByUserId(String userId) {
        return this.find("userId", userId).list();
    }

    public Optional<Order> findByTransaction(String transaction) {
        return this.find("transaction", transaction).firstResultOptional();
    }
}
