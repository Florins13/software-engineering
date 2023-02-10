package org.uab.user.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.uab.shop.model.Cart;
import org.uab.user.model.User;

public interface UserDao extends PanacheRepository<User> {
    User getById(Long id);

    User findUserByName(String username);

    Cart getUserCart(String username);
}
