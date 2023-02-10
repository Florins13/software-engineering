package org.uab.shop.model;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.uab.bike.model.Bike;
import org.uab.user.model.User;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@QuarkusTest
public class OrderTest {

    Order order;

    @BeforeEach
    void setup() {
        Order mockOrder = new Order();
        User user = new User();
        user.setUsername("test");
        Bike testBike = new Bike("Test Model", "test", 0,"details", new BigDecimal(34.50), true);
        ShippingAddress shippingAddress = new ShippingAddress();
        shippingAddress.setFullName("Test address name");
        ShippingItem shippingItem = new ShippingItem(testBike, 1);
        List<ShippingItem> shippingItemList = new ArrayList<>();
        shippingItemList.add(shippingItem);
        mockOrder.setId(1L);
        mockOrder.setShippingAddress(shippingAddress);
        mockOrder.setShippingItems(shippingItemList);
        mockOrder.setUser(user);
        mockOrder.setAcquireType(AcquireType.BUY);
        mockOrder.setOrderState(OrderState.NEW);
        mockOrder.setTransaction(UUID.randomUUID().toString());
        mockOrder.setTotalPrice(new BigDecimal(25.50));
        this.order = mockOrder;
    }

    @Test
    public void shouldHaveInitialValues() throws Throwable{
        Assertions.assertEquals(1L,order.getId());
        Assertions.assertEquals("Test address name",order.getShippingAddress().getFullName());
        Assertions.assertEquals("Test Model",order.getShippingItems().stream().findFirst().get().getBike().getModel());
        Assertions.assertEquals("test",order.getUser().getUsername());
        Assertions.assertEquals(AcquireType.BUY,order.getAcquireType());
        Assertions.assertEquals(OrderState.NEW,order.getOrderState());
        Assertions.assertFalse(order.getTransaction().isEmpty());
        Assertions.assertTrue((new BigDecimal(25.50)).compareTo(order.getTotalPrice())==0);
    }

    @Test
    public void shouldNotSetTotalPrice() {
        this.order.setTotalPrice(new BigDecimal(0));
        Assertions.assertTrue((new BigDecimal(25.50)).compareTo(order.getTotalPrice())==0);
    }

    @Test
    public void shouldConstructOrder(){
        User user = new User();
        user.setUsername("test");
        ShippingAddress shippingAddress = new ShippingAddress();
        shippingAddress.setFullName("Test address name");
        Order constructorTestOrder = new Order(user, shippingAddress,AcquireType.RENT,OrderState.NEW,new BigDecimal(25));
        Assertions.assertEquals("Test address name",constructorTestOrder.getShippingAddress().getFullName());
        Assertions.assertEquals("test",constructorTestOrder.getUser().getUsername());
        Assertions.assertEquals(AcquireType.RENT,constructorTestOrder.getAcquireType());
        Assertions.assertEquals(OrderState.NEW,constructorTestOrder.getOrderState());
        Assertions.assertFalse(constructorTestOrder.getTransaction().isEmpty());
        Assertions.assertTrue((new BigDecimal(25)).compareTo(constructorTestOrder.getTotalPrice())==0);
    }
}
