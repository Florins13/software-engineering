package org.uab.shop.controller;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static io.restassured.RestAssured.given;

@QuarkusTest
@TestHTTPEndpoint(OrderController.class)
public class OrderControllerTest {

    @Test
    @TestSecurity(user = "basic", roles = {"BASIC"})
    public void shouldReturnOrder() throws IOException {
        given().with().formParam("fullName","testName")
                .formParam("address", "testAddr")
                .formParam("telephone", 123)
                .formParam("zipCode", "zip")
                .formParam("acquire", "buy")
                .when().post("/finalise")
                .then()
                .statusCode(200);
    };

    @Test
    @TestSecurity(user = "man", roles = {"MANAGER"})
    public void shouldRReturn403() throws IOException {
        given().with().formParam("fullName","testName")
                .formParam("address", "testAddr")
                .formParam("telephone", 123)
                .formParam("zipCode", "zip")
                .formParam("acquire", "buy")
                .when().post("/finalise")
                .then()
                .statusCode(403);
    };
}
