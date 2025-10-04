package com.dev.user.repository;

import com.dev.user.model.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {

    public void save(User user) {
        this.persist(user);
    }

    public User getUserByName(String username) {
        return this.find("username", username).firstResult();
    }
}
