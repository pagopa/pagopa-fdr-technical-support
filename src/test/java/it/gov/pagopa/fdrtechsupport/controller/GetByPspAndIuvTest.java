package it.gov.pagopa.fdrtechsupport.controller;

import static io.restassured.RestAssured.given;
import static it.gov.pagopa.fdrtechsupport.util.AppConstantTestHelper.PSP_CODE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import it.gov.pagopa.fdrtechsupport.controller.model.flow.response.FlowBaseInfo;
import it.gov.pagopa.fdrtechsupport.controller.model.report.response.MultipleFlowsResponse;
import it.gov.pagopa.fdrtechsupport.util.ContainersTestResource;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;

@QuarkusTest
@QuarkusTestResource(value = ContainersTestResource.class)
public class GetByPspAndIuvTest {

  public static final String URL = "/psps/%s/iuv/%s";

  @Test
  void testGetFdrByPspAndIuv() {
    String flowName = "GetFdrByPspAndIuv_FDR_name";
    String iuv = RandomStringUtils.randomAlphabetic(20);
    LocalDate created = LocalDate.of(2022, Month.JULY, 27);

    MultipleFlowsResponse res =
        given()
            .param("dateFrom", created.toString())
            .param("dateTo", created.toString())
            .when()
            .get(URL.formatted(PSP_CODE, iuv))
            .then()
            .statusCode(200)
            .extract()
            .as(new TypeRef<>() {});

    List<FlowBaseInfo> data = res.getData();
    assertThat(data.size(), greaterThan(0));
    assertEquals(flowName, data.get(0).getFdr());
    assertEquals("2022-07-27T16:15:50Z", data.get(0).getCreated());
  }

  @Test
  void testGetFdrByPspAndIuvMalformedError() {
    given()
        .param("dateFrom", "27-07-2022")
        .param("dateTo", "27-07-2022")
        .when()
        .get(URL.formatted(25, 1))
        .then()
        .statusCode(equalTo(404));
  }

  @Test
  void testGetFdrByPspAndIuvEmptyParamError() {
    given()
        .param("dateFrom", "")
        .param("dateTo", "")
        .when()
        .get(URL.formatted(25, 1))
        .then()
        .statusCode(equalTo(404));
  }

  @Test
  void testGetFdrByPspAndIuvReverseDate() {
    given()
        .param("dateFrom", "27-07-2022")
        .param("dateTo", "27-05-2022")
        .when()
        .get(URL.formatted(25, 1))
        .then()
        .statusCode(equalTo(404));
  }
}
