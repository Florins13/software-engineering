package com.dev.shop.service;



import com.dev.product.repository.BikeRepository;
import com.dev.shop.model.Cart;
import com.dev.shop.model.CartItem;
import com.dev.shop.repository.CartItemRepository;
import com.dev.user.service.UserService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

@ApplicationScoped
public class CartServiceImpl implements CartService {

    @Inject
    CartItemRepository cartItemRepository;
    @Inject
    BikeRepository bikeRepository;
    @Inject
    UserService userServiceImpl;

    @Override
    public void addToCart(Long id) {
        Cart currentCart = getUserCart();
        CartItem cartItem = new CartItem(bikeRepository.findById(id), 1, currentCart);
        if (currentCart.getCartItems().stream().map(item -> item.getBike().getId()).anyMatch(id::equals)) {
            for (CartItem item : currentCart.getCartItems()) {
                if (item.getBike().getId().equals(id)) {
                    item.increaseQuantity();
                }
            }
        } else {
            currentCart.getCartItems().add(cartItem);
        }
    }


    @Override
    public void updateQuantity(Long id, String type) throws Exception {
        try {
            CartItem cartItem = getUserCart().getCartItems().stream().filter(item -> item.getId().equals(id)).findAny().get();
            if (type.equals("increase")) {
                cartItem.increaseQuantity();
            } else if (type.equals("decrease")) {
                cartItem.decreaseQuantity();
            } else {
                throw new Exception("Type can be increase and decrease only!");
            }
        } catch (NoSuchElementException e) {
            // this normally is not possible, only if user opens 2 tabs
            throw new Exception("No such item in your cart!");
        }
    }


    @Override
    public BigDecimal calculateRentTotal() {
        List<CartItem> cartBikes = getUserCart().getCartItems();
        return BigDecimal.valueOf(cartBikes.stream().mapToInt(cartItem -> cartItem.getQuantity()).sum() * 5L);
    }


    @Override
    public void deleteCartItem(Long id) {
        cartItemRepository.deleteWhereCartIdAndCartItemId(id, getUserCart().getId());
    }

    @Override
    public Cart getUserCart() {
        return userServiceImpl.getUserCart();
    }

    @Override
    public String getUserRole() {
        return userServiceImpl.getUserRole();
    }

}
