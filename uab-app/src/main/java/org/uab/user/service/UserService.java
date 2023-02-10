package org.uab.user.service;

import org.uab.shop.model.Cart;
import org.uab.user.model.Role;
import org.uab.user.model.User;

public interface UserService {
    User addUser(String username, String password, Role role);

    String getUserRole();

    Cart getUserCart();

    User getCurrentUser();
}
