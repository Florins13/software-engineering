package org.uab.shop.service;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.uab.bike.model.Bike;
import org.uab.shop.model.AcquireType;
import org.uab.shop.model.Cart;
import org.uab.shop.model.CartItem;
import org.uab.shop.model.Order;
import org.uab.shop.repository.CartItemRepository;
import org.uab.shop.repository.OrderRepository;
import org.uab.user.model.User;
import org.uab.user.service.UserServiceImpl;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.doAnswer;

@QuarkusTest
public class OrderServiceTest {

    @Inject
    OrderServiceImpl orderServiceImpl;

    @InjectMock
    OrderRepository orderRepository;
    @InjectMock
    CartItemRepository cartItemRepository;
    @InjectMock
    UserServiceImpl userServiceImpl;
    Cart cart;
    Bike bike;
    List<CartItem> cartItems = new ArrayList<>();

    @BeforeEach
    void setup() {
        Cart mockCart = new Cart();
        mockCart.setId(1L);
        Bike testBike1 = new Bike("Test Model", "test", 3,"details", new BigDecimal(35.5), true);
        testBike1.setId(1L);
        CartItem cartItem1 = new CartItem(testBike1, 2, mockCart);
        cartItem1.setId(1L);
        cartItems.add(cartItem1);
        mockCart.setCartItems(cartItems);
        User user = new User();
        user.setCart(cart);
        Order mockOrder = new Order();
        mockOrder.setTransaction("test transaction");
        mockOrder.setAcquireType(AcquireType.BUY);
        mockOrder.setId(1L);
        /* mock cartItems deletion */
        doAnswer((i)-> {cartItems.remove(0);return null;}).when(cartItemRepository).deleteCartItemById(1L);
        this.cart = mockCart;
        this.bike = testBike1;
        Mockito.when(userServiceImpl.getCurrentUser()).thenReturn(user);
        Mockito.when(orderServiceImpl.getUserCart()).thenReturn(cart);
    };

    @Test
    public void shouldReturnSavedBuyOrder() {
        /* quantity is 2 which means 35.5x2 */
        Order testOrder = orderServiceImpl.createOrder("test", "cenas", 123, "zipCode","buy");
        BigDecimal expected = new BigDecimal(71.00);
        Assertions.assertTrue(expected.compareTo(testOrder.getTotalPrice())==0);
        Assertions.assertEquals(2, testOrder.getShippingItems().stream().mapToInt(item->item.getQuantity()).sum());
        Assertions.assertNotNull(testOrder.getShippingAddress().getFullName());

    };

    @Test
    public void shouldReturnSavedRentOrder() {
        /* quantity is 2 which means 5x2 */
        Order testOrder = orderServiceImpl.createOrder("test", "cenas", 123, "zipCode","rent");
        BigDecimal expected = new BigDecimal(10);
        Assertions.assertTrue(expected.compareTo(testOrder.getTotalPrice())==0);
        Assertions.assertEquals("test", testOrder.getShippingAddress().getFullName());
        Assertions.assertEquals(2, testOrder.getShippingItems().stream().mapToInt(item->item.getQuantity()).sum());
        Assertions.assertNotNull(testOrder.getShippingAddress().getFullName());
    };

    @Test
    public void shouldHaveCartEmptyAfterOrder() throws Exception {
        /* quantity is 2 which means 5x2 */
        orderServiceImpl.placeOrder("test", "cenas", 123, "zipCode","rent");
        Assertions.assertTrue(this.cart.cartIsEmpty());
    };

    @Test
    public void shouldUpdateStockAfterOrderPlaced() throws Exception {
        /* quantity is 2 which means 5x2 */
        orderServiceImpl.placeOrder("test", "cenas", 123, "zipCode","buy");
        Assertions.assertEquals(1, this.bike.getStock());
    };
}
