package org.uab.shop.controller;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

@QuarkusTest
@TestHTTPEndpoint(CartController.class)
public class CartControllerTest {

    @Test
    @TestSecurity(user = "basic", roles = {"BASIC"})
    public void shouldUpdateCartQuantity() throws IOException {
        String increase = "increase";
        String decrease = "decrease";
        given().with().formParam("id",1)
                .when().post("/updateQuantity/{type}",increase)
                .then()
                .statusCode(200)
                .body(containsString(ResourceCartController.cartItemIncreased));
        given().with().formParam("id",1)
                .when().post("/updateQuantity/{type}", decrease)
                .then()
                .statusCode(200).body(containsString(ResourceCartController.cartItemDecreased));
        given().with().formParam("id",1)
                .when().post("/updateQuantity/{type}", "test")
                .then()
                .statusCode(500).body(containsString("Type can be increase and decrease only!"));

    };

    @Test
    @TestSecurity(user = "basic", roles = {"BASIC"})
    public void shouldDeleteCartItem() throws IOException {
        given().with().formParam("id",1)
                .when().post("/deleteItem")
                .then()
                .statusCode(200).body(containsString(ResourceCartController.cartItemDelete));
    }
}
