package com.dev.user.service;

import com.dev.shop.model.Cart;
import com.dev.user.model.Role;
import com.dev.user.model.User;
import com.dev.user.repository.UserRepository;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;


@ApplicationScoped
public class UserServiceImpl implements UserService {

    @Inject
    UserRepository userRepository;

    @Inject
    SecurityIdentity securityIdentity;

    @Override
    public User addUser(String username, String password, Role role) {
        User user;
        if (Role.BASIC.equals(role)) {
            user = new User(username, password, role, new Cart());
        } else {
            user = new User(username, password, role);
        }
        userRepository.save(user);
        return user;
    }

    @Override
    public String getUserRole() {
        return getCurrentUser() != null ? getCurrentUser().getRole() : null;
    }

    @Override
    public Cart getUserCart() {
        return getCurrentUser().getCart();
    }

    @Override
    public User getCurrentUser() {
        return this.userRepository.getUserByName("basic");
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

}
