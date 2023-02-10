package org.uab.shop.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.uab.shop.model.Order;

import javax.enterprise.context.ApplicationScoped;
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
