package it.gov.pagopa.fdrtechsupport.resources;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
class GenericTest {

    @Test
    public void info() {
        given().when().get("/info").then().statusCode(200).contentType(MediaType.APPLICATION_JSON);
    }
}
