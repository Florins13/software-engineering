package com.dev.shop.repository;

import com.dev.shop.model.Order;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class OrderRepository implements PanacheRepository<Order> {

    public void save(Order order){
        this.persist(order);
    }

    public List<Order> getAllOrders(){
        return this.findAll().list();
    };
}
