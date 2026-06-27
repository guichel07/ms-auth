// SellerControllerTest.java
package org.acme.Controller;

import static io.restassured.RestAssured.given;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.security.TestSecurity;
import org.acme.NoDbProfile;
import org.junit.jupiter.api.Test;

@QuarkusTest
@TestProfile(NoDbProfile.class)
class SellerControllerTest {

    @Test
    void getSellerById_shouldReturn401_whenNotAuthenticated() {
        given().when().get("/ms-sellers/1").then().statusCode(401);
    }

    @Test
    void getAllSellers_shouldReturn401_whenNotAuthenticated() {
        given().when().get("/ms-sellers").then().statusCode(401);
    }

    @Test
    @TestSecurity(user = "john@acme.org", roles = "SELLER")
    void getAllSellers_shouldReturn403_whenNotAdmin() {
        given().when().get("/ms-sellers").then().statusCode(403);
    }

    @Test
    void updateSeller_shouldReturn401_whenNotAuthenticated() {
        given().when().put("/ms-sellers/1").then().statusCode(401);
    }

    @Test
    void deleteSeller_shouldReturn401_whenNotAuthenticated() {
        given().when().delete("/ms-sellers/1").then().statusCode(401);
    }

    @Test
    @TestSecurity(user = "john@acme.org", roles = "SELLER")
    void deleteSeller_shouldReturn403_whenNotAdmin() {
        given().when().delete("/ms-sellers/1").then().statusCode(403);
    }
}
