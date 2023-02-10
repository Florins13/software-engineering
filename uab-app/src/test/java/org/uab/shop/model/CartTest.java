package org.uab.shop.model;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.uab.bike.model.Bike;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@QuarkusTest
public class CartTest {

    Cart cart;
    List<CartItem> cartItems = new ArrayList<>();

    @BeforeEach
    void setup() {
        Cart mockCart = new Cart();
        mockCart.setId(1L);
        Bike testBike1 = new Bike("Test Model", "test", 3,"details", new BigDecimal(34.50), true);
        Bike testBike2 = new Bike("Test Model 2", "test2", 3,"details", new BigDecimal(50), true);
        CartItem cartItem1 = new CartItem(testBike1, 2, mockCart);
        CartItem cartItem2 = new CartItem(testBike2, 1, mockCart);
        cartItems.add(cartItem1);
        cartItems.add(cartItem2);
        mockCart.setCartItems(cartItems);
        this.cart = mockCart;
    }

    @Test
    public void shouldReturnTotalCost() throws Throwable {
        BigDecimal total = cart.calculateTotalCost(2, new BigDecimal(20.50));
        BigDecimal expected = new BigDecimal(41.0);
        Assertions.assertTrue(expected.compareTo(total)==0);
    }

    @Test
    public void shouldReturnCartTotal() throws Throwable {
        BigDecimal expected = new BigDecimal(119.00);
        Assertions.assertTrue(expected.compareTo(cart.getTotal())==0);
    }

    @Test
    public void shouldReturnCartItems() throws Throwable {
        Assertions.assertEquals(2,cart.getCartItems().size());
        Assertions.assertEquals(cartItems, cart.getCartItems());
    }

    @Test
    public void shouldSetCartItems() throws  Throwable{
        Bike newBike = new Bike("Test Model", "test", 3,"details", new BigDecimal(34.50), true);
        CartItem newCart = new CartItem(newBike, 2, cart);
        List<CartItem> newCartItems = new ArrayList<>();
        newCartItems.add(newCart);
        cart.setCartItems(newCartItems);
        Assertions.assertEquals(1,cart.getCartItems().size());
        Assertions.assertEquals(newCartItems, cart.getCartItems());
    }

    @Test
    public void shouldReturnIsNotEmpty(){
        Assertions.assertFalse(cart.cartIsEmpty());
    }

    @Test
    public void shouldReturnIsEmpty(){
        Bike newBike = new Bike("Test Model", "test", 3,"details", new BigDecimal(34.50), true);
        CartItem newCartItem = new CartItem(newBike, 0, cart);
        List<CartItem> cartItems = new ArrayList<>();
        cartItems.add(newCartItem);
        Cart cart = new Cart(cartItems);
        Assertions.assertTrue(cart.cartIsEmpty());
    }
    @Test
    public void shouldReturnCartId(){
        Assertions.assertEquals(1L,cart.getId());
    }
}
