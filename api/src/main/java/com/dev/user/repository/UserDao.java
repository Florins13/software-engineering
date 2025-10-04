package com.dev.user.repository;

import com.dev.shop.model.Cart;
import com.dev.user.model.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

public interface UserDao extends PanacheRepository<User> {
    User getById(Long id);

    User findUserByName(String username);

    Cart getUserCart(String username);
}
