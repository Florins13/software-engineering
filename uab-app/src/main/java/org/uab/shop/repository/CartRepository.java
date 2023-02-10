package org.uab.shop.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.uab.shop.model.Cart;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CartRepository implements PanacheRepository<Cart> {

}
