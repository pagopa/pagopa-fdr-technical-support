package it.gov.pagopa.fdrtechsupport.controllers;

import static io.restassured.RestAssured.given;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.Test;

@QuarkusTest
class GenericTest {

  @Test
  public void info() {
    given().when().get("/info").then().statusCode(200).contentType(MediaType.APPLICATION_JSON);
  }
}
