package org.uab.bike;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.uab.bike.model.Bike;

import java.math.BigDecimal;

@QuarkusTest
public class BikeTest {

    Bike bike1;
    Bike bike2;

    @BeforeEach
    void setup() {
        Bike testBike1 = new Bike("Test Model", "test", 0,"details", new BigDecimal(34.50), true);
        Bike testBike2 = new Bike("Test Model 2", "test2", 3,"details", new BigDecimal(50), false);
        testBike1.setId(1L);
        testBike2.setId(2L);
        this.bike1 = testBike1;
        this.bike2 = testBike2;
    }

    @Test
    public void shouldHaveInitialValues() throws Throwable {
        BigDecimal expectedPrice = new BigDecimal(34.50);
        Assertions.assertEquals(1L, bike1.getId());
        Assertions.assertEquals("Test Model", bike1.getModel());
        Assertions.assertEquals("test", bike1.getImageSource());
        Assertions.assertEquals(0, bike1.getStock());
        Assertions.assertEquals("details", bike1.getDetails());
        Assertions.assertTrue(bike1.isElectric());
        Assertions.assertTrue(expectedPrice.compareTo(bike1.getPrice())==0);
    };

    @Test
    public void shouldSetElectric() throws Throwable {
        bike2.setElectric(true);
        Assertions.assertTrue(bike2.isElectric());
    };

    @Test
    public void shouldSetModel() throws Throwable {
        bike2.setModel("test");
        Assertions.assertEquals("test",bike2.getModel());
    };
    @Test
    public void checkInStock() throws Throwable {
        Assertions.assertTrue(bike2.isInStock());
        Assertions.assertFalse(bike1.isInStock());
    };

    @Test
    public void shouldSetStock() throws Throwable {
        bike1.setStock(3);
        Assertions.assertEquals(3, bike2.getStock());
        bike1.setStock(-1);
        Assertions.assertEquals(3, bike2.getStock());
    };

    @Test
    public void shouldSetPrice() throws Throwable {
        BigDecimal price = new BigDecimal(23.99);
        bike1.setPrice(price);
        Assertions.assertEquals(price, bike1.getPrice());
        BigDecimal secondPrice = new BigDecimal(0);
        bike1.setPrice(secondPrice);
        Assertions.assertTrue(price.compareTo(bike1.getPrice())==0);
    };

    @Test
    public void shouldSetDetails() throws Throwable {
        bike1.setDetails("test");
        Assertions.assertEquals("test", bike1.getDetails());
    };

    @Test
    public void shouldSetImageSource() throws Throwable {
        bike1.setImageSource("img");
        Assertions.assertEquals("img", bike1.getImageSource());
    };

}
