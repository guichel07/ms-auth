// AuthControllerTest.java
package org.acme.Controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import org.acme.NoDbProfile;
import org.junit.jupiter.api.Test;

@QuarkusTest
@TestProfile(NoDbProfile.class)
class AuthControllerTest {

    @Test
    void logout_shouldReturn200AndExpireCookie() {
        given()
            .when()
            .post("/ms-auth/logout")
            .then()
            .statusCode(200)
            .cookie("jwt", equalTo(""));
    }
}
