package it.gov.pagopa.fdrtechsupport.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.quarkiverse.mockserver.test.MockServerTestResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import it.gov.pagopa.fdrtechsupport.controller.model.report.response.MultipleFlowsResponse;
import it.gov.pagopa.fdrtechsupport.util.AzuriteResource;
import it.gov.pagopa.fdrtechsupport.util.MongoTestResource;
import it.gov.pagopa.fdrtechsupport.util.common.DateUtil;
import java.time.LocalDate;
import java.time.Month;
import lombok.SneakyThrows;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@QuarkusTestResource(MockServerTestResource.class)
@QuarkusTestResource(AzuriteResource.class)
@QuarkusTestResource(MongoTestResource.class)
class PspTest {

  private static final String PSP_CODE = "88888888888";
  private static final String ORG_ID = "77777777777";

  @ConfigProperty(name = "blob-storage.fdr.connection-string")
  String connString;

  @SneakyThrows
  @Test
  @DisplayName("get fdr by psp table")
  void getByPspTable() {
    String url = "/psps/%s".formatted(PSP_CODE);
    LocalDate date = LocalDate.of(2026, Month.JANUARY, 17);

    MultipleFlowsResponse res =
        given()
            .param("dateFrom", DateUtil.format(date.minusDays(1)))
            .param("dateTo", DateUtil.format(date.plusDays(1)))
            .when()
            .get(url)
            .then()
            .statusCode(200)
            .extract()
            .as(new TypeRef<MultipleFlowsResponse>() {});

    assertNotNull(res);
    assertNotNull(res.getData());
    assertThat(res.getData().size(), greaterThan(0));
    assertNotNull(res.getData().get(0));
    assertEquals(ORG_ID, res.getData().get(0).getOrganizationId());
    assertNotNull(res.getData().get(0).getFdr());
  }
}
