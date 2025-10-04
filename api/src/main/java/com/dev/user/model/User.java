package com.dev.user.model;


import com.dev.shop.model.Cart;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.security.jpa.Password;
import io.quarkus.security.jpa.Roles;
import io.quarkus.security.jpa.UserDefinition;
import io.quarkus.security.jpa.Username;

import jakarta.persistence.*;

@Entity
@Table(name = "uab_user")
@UserDefinition
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String name;
    private String email;
    @Username
    private String username;
    @Password
    private String password;

    @Roles
    private String role;
    @OneToOne(cascade = CascadeType.ALL)
    Cart cart;

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public User() {
    }

    public User(String username, String password, Role role) {
        this.username = username;
        this.password = BcryptUtil.bcryptHash(password);
        this.setRole(role);
    }

    public User(String username, String password, Role role, Cart cart) {
        this(username, password, role);
        this.cart = cart;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role.toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}