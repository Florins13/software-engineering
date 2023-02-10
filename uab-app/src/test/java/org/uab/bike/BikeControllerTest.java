package org.uab.bike;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import org.junit.jupiter.api.Test;
import org.uab.bike.controller.BikeController;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;

@QuarkusTest
@TestHTTPEndpoint(BikeController.class)
public class BikeControllerTest {

    @Test
    public void shouldReturnBikes() throws IOException {
        given()
                .when().get("/")
                .then()
                .statusCode(200)
                .body(containsString(ResourceBikeController.headerNoLogin))
                .body(containsString(ResourceBikeController.bikesNoLogin))
                .body(containsString(ResourceBikeController.footer));

    }

    @Test
    @TestSecurity(user = "basic", roles = {"BASIC"})
    public void shouldReturnBikesOnBasicLogin() throws IOException {
        given()
                .when().get("/")
                .then()
                .statusCode(200)
                .body(containsString(ResourceBikeController.headerLoggedIn))
                .body(containsString(ResourceBikeController.bikesLoggedIn))
                .body(containsString(ResourceBikeController.footer));

    }

    @Test
    @TestSecurity(user = "man", roles = {"MANAGER"})
    public void shouldReturnBikesOnLogin() throws IOException {
        given()
                .when().get("/")
                .then()
                .statusCode(200)
                .body(not(containsString("<button type=\"submit\">Add to cart</button>")))
                .body(containsString(ResourceBikeController.footer));

    }
}
