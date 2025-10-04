package com.dev.user.service;


import com.dev.shop.model.Cart;
import com.dev.user.model.Role;
import com.dev.user.model.User;

public interface UserService {
    User addUser(String username, String password, Role role);

    String getUserRole();

    Cart getUserCart();

    User getCurrentUser();
}
