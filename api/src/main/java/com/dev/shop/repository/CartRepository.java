package com.dev.shop.repository;

import com.dev.shop.model.Cart;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CartRepository implements PanacheRepository<Cart> {

}
