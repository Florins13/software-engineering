package org.uab;

import io.quarkus.runtime.StartupEvent;
import org.uab.bike.model.Bike;
import org.uab.bike.service.BikeService;
import org.uab.shop.model.CartItem;
import org.uab.user.model.Role;
import org.uab.user.model.User;
import org.uab.user.service.UserServiceImpl;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;
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
        Bike bike = new Bike("CITY STAR ST 500", "bike_one.jpg", 4, "red, medium", new BigDecimal("24.99"), false);
        Bike bike2 = new Bike("SUPER STAR FS 400", "bike_two.jpg", 5, "big, black", new BigDecimal("50.45"), true);
        Bike bike3 = new Bike("ROCK STAR FS 400", "bike_three.jpg", 12, "red, medium", new BigDecimal("115.50"), false);
        bikeService.getBikeRepository().persist(bike);
        bikeService.getBikeRepository().persist(bike2);
        bikeService.getBikeRepository().persist(bike3);

        /* LOAD SOME USERS */
        this.userServiceImpl.getUserRepository().deleteAll();
        User user = this.userServiceImpl.addUser("basic", "1", Role.BASIC);
        this.userServiceImpl.addUser("basicc", "3", Role.BASIC);
        this.userServiceImpl.addUser("man", "2", Role.MANAGER);

        /* POPULATE CART WITH SOME BIKES */
        Bike anyBike = bikeService.getBikeRepository().findAll().firstResult();
        CartItem cartItem = new CartItem(anyBike, 1, user.getCart());
        List<CartItem> cartItemList = new ArrayList<>();
        cartItemList.add(cartItem);
        user.getCart().setCartItems(cartItemList);
    }


}