package org.uab.shop.service;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.uab.bike.model.Bike;
import org.uab.bike.repository.BikeRepository;
import org.uab.shop.model.Cart;
import org.uab.shop.model.CartItem;
import org.uab.shop.repository.CartItemRepository;
import org.uab.user.model.User;
import org.uab.user.service.UserServiceImpl;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doAnswer;

@QuarkusTest
public class CartServiceTest {

    @Inject
    CartServiceImpl cartServiceImpl;

    @InjectMock
    BikeRepository bikeRepository;

    @InjectMock
    CartItemRepository cartItemRepository;

    @InjectMock
    UserServiceImpl userServiceImpl;

    @BeforeEach
    void setUp() {
        PanacheQuery query = Mockito.mock(PanacheQuery.class);
        List<Bike> bikes = new ArrayList<>();
        Bike bike = new Bike();
        bike.setId(1L);
        bike.setStock(3);
        bike.setModel("test");
        bike.setPrice(new BigDecimal(2));
        bikes.add(bike);
        bikes.get(0).setId(1L);
        User user = new User();
        Cart cart = new Cart();
        cart.setId(1L);
        CartItem cartItem = new CartItem(bike,1,cart);
        cartItem.setId(1L);
        List<CartItem> cartItems = new ArrayList<>();
        cartItems.add(cartItem);
        cart.setCartItems(cartItems);
        user.setCart(cart);
        Mockito.when(bikeRepository.findAll()).thenReturn(query);
        Mockito.when(query.list()).thenReturn(bikes);
        Mockito.when(userServiceImpl.getUserCart()).thenReturn(cart);
        doAnswer((i)-> {cartItems.remove(0);return null;}).when(cartItemRepository).deleteWhereCartIdAndCartItemId(1L,1L);
    }
    @Test
    public void shouldIncreaseQuantityIfCartItemExist() {
        cartServiceImpl.addToCart(1L);
        Assertions.assertEquals(2, cartServiceImpl.getUserCart().getCartItems().stream().findFirst().get().getQuantity());
        cartServiceImpl.addToCart(1L);
        Assertions.assertEquals(3, cartServiceImpl.getUserCart().getCartItems().stream().findFirst().get().getQuantity());
        Assertions.assertEquals("test", cartServiceImpl.getUserCart().getCartItems().stream().findFirst().get().getBike().getModel());
    };

    @Test
    public void shouldAddCartItem() {
        Cart cart = new Cart();
        List<CartItem> cartItems = new ArrayList<>();
        cart.setCartItems(cartItems);
        cart.setId(1L);
        Mockito.when(userServiceImpl.getUserCart()).thenReturn(cart);
        cartServiceImpl.addToCart(1L);
        Assertions.assertEquals(1, cartServiceImpl.getUserCart().getCartItems().stream().findFirst().get().getQuantity());
    };



    @Test
    public void shouldUpdateQuantity() throws Exception {
        cartServiceImpl.updateQuantity(1L,"increase");
        Assertions.assertEquals(2, cartServiceImpl.getUserCart().getCartItems().stream().findFirst().get().getQuantity());
        cartServiceImpl.updateQuantity(1L,"increase");
        Assertions.assertEquals(3, cartServiceImpl.getUserCart().getCartItems().stream().findFirst().get().getQuantity());
        cartServiceImpl.updateQuantity(1L,"increase");
        Assertions.assertEquals(3, cartServiceImpl.getUserCart().getCartItems().stream().findFirst().get().getQuantity());
        cartServiceImpl.updateQuantity(1L,"decrease");
        Assertions.assertEquals(2, cartServiceImpl.getUserCart().getCartItems().stream().findFirst().get().getQuantity());
    };

    @Test
    public void shouldDeleteCartItem() throws Exception {
        cartServiceImpl.deleteCartItem(1L);
        Assertions.assertEquals(0, cartServiceImpl.getUserCart().getCartItems().size());
        Assertions.assertTrue(cartServiceImpl.getUserCart().cartIsEmpty());
    };

    @Test
    public void shouldThrowExceptionOnUpdateQuantity() {
        Exception exception = assertThrows(Exception.class, () -> {
            cartServiceImpl.updateQuantity(1L,"test");});
        Assertions.assertEquals("Type can be increase and decrease only!", exception.getMessage());
    };

    @Test
    public void shouldCalculateTotalRent() {
        BigDecimal expected = new BigDecimal(5);
        Assertions.assertTrue(expected.compareTo(cartServiceImpl.calculateRentTotal())==0);
    };


}
