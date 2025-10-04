package com.dev;

import com.dev.product.model.Bike;
import com.dev.product.service.BikeService;
import com.dev.shop.model.CartItem;
import com.dev.user.model.Role;
import com.dev.user.model.User;
import com.dev.user.service.UserServiceImpl;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class Startup {
    @Inject
    UserServiceImpl userServiceImpl;

    @Inject
    BikeService bikeService;

    @Transactional
    public void loadData(@Observes StartupEvent evt) {
        /* LOAD SOME BIKES */
        bikeService.getBikeRepository().deleteAll();
        Bike bike = new Bike("CITY STAR ST 500", "bike_one.jpg", 100, "red, medium", new BigDecimal("24.99"), false);
        Bike bike2 = new Bike("test", "bike_two.jpg", 5, "black, short", new BigDecimal("50"), true);
        Bike bike3 = new Bike("ROCK STAR FS 400", "bike_three.jpg", 12, "red, medium", new BigDecimal("115.50"), false);
        Bike bike4= new Bike("ROCK STAR FS 500", "bike_three.jpg", 2, "red, medium", new BigDecimal("115.50"), false);
        bikeService.getBikeRepository().persist(bike);
        bikeService.getBikeRepository().persist(bike2);
        bikeService.getBikeRepository().persist(bike3);
        bikeService.getBikeRepository().persist(bike4);

        /* LOAD SOME USERS */
        this.userServiceImpl.getUserRepository().deleteAll();
        User user = this.userServiceImpl.addUser("basic", "1", Role.BASIC);
        this.userServiceImpl.addUser("basicc", "3", Role.BASIC);
        this.userServiceImpl.addUser("man", "2", Role.MANAGER);

        /* POPULATE CART WITH SOME BIKES */
        Bike anyBike = bikeService.getBikeRepository().findAll().firstResult();
        System.out.println(anyBike.getStock());
        CartItem cartItem = new CartItem(anyBike, 1, user.getCart());
        List<CartItem> cartItemList = new ArrayList<>();
        cartItemList.add(cartItem);
        user.getCart().setCartItems(cartItemList);
    }
}