package org.uab.user.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.uab.user.model.User;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {

    public void save(User user) {
        this.persist(user);
    }

    public User getUserByName(String username) {
        return this.find("username", username).firstResult();
    }
}
