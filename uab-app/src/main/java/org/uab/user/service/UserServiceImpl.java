package org.uab.user.service;

import io.quarkus.security.identity.SecurityIdentity;
import org.uab.shop.model.Cart;
import org.uab.user.model.Role;
import org.uab.user.model.User;
import org.uab.user.repository.UserRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

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
        return this.userRepository.getUserByName(securityIdentity.getPrincipal().getName());
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

}
