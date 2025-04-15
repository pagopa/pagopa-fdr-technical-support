package it.gov.pagopa.fdrtechsupport.controller;

import com.azure.data.tables.TableClient;
import com.azure.data.tables.TableServiceClient;
import com.azure.data.tables.TableServiceClientBuilder;
import io.quarkiverse.mockserver.test.MockServerTestResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import it.gov.pagopa.fdrtechsupport.models.FdrBaseInfo;
import it.gov.pagopa.fdrtechsupport.controller.model.response.FrResponse;
import it.gov.pagopa.fdrtechsupport.util.AppConstantTestHelper;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static io.restassured.RestAssured.given;
import static it.gov.pagopa.fdrtechsupport.util.AppConstantTestHelper.PSP_CODE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

@QuarkusTest
@QuarkusTestResource(MockServerTestResource.class)
public class GetByPspAndIurTest {
  @ConfigProperty(name = "fdr-history-table-storage.connection-string")
  String connString;

  public static final String url = "/psps/%s/iur/%s";
  private TableClient tableClient;

  private TableClient getTableClient() {
    if (tableClient == null) {
      TableServiceClient tableServiceClient =
          new TableServiceClientBuilder().connectionString(connString).buildClient();
      tableServiceClient.createTableIfNotExists("fdrpaymentpublish");
      tableClient = tableServiceClient.getTableClient("fdrpaymentpublish");
    }
    return tableClient;
  }

  @Test
  void testGetFdrByPspAndIur() {
    String flowName = RandomStringUtils.randomAlphabetic(20);
    String orgId = RandomStringUtils.randomAlphabetic(10);
    String iur = RandomStringUtils.randomAlphabetic(20);
    LocalDate created = LocalDate.now();
    getTableClient()
        .createEntity(
            AppConstantTestHelper.newTableFdrPaymentPublish(
                created, PSP_CODE, orgId, flowName, iur));
    DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
    FrResponse res =
        given()
            .param("dateFrom", created.toString())
            .param("dateTo", created.toString())
            .when()
            .get(url.formatted(PSP_CODE, iur))
            .then()
            .statusCode(200)
            .extract()
            .as(new TypeRef<>() {});

    List<FdrBaseInfo> data = res.getData();
    assertThat(data.size(), greaterThan(0));
    assertThat(data.get(0).getFdr(), equalTo(flowName));
    assertThat(
        OffsetDateTime.parse(data.get(0).getCreated(), formatter),
        equalTo(created.atStartOfDay().atOffset(ZoneOffset.UTC)));
  }

  @Test
  void wideDateRange() {

    given()
            .param("dateFrom", "2021-07-01")
            .param("dateTo", "2022-07-01")
            .when()
            .get(url.formatted(25, 1))
            .then()
            .statusCode(equalTo(200));
  }
  @Test
  void testGetFdrByPspAndIurMalformedError() {

    given()
        .param("dateFrom", "27-07-2022")
        .param("dateTo", "27-07-2022")
        .when()
        .get(url.formatted(25, 1))
        .then()
        .statusCode(equalTo(404));
  }

  @Test
  void testGetFdrByPspAndIurEmptyParamError() {

    given()
        .param("dateFrom", "")
        .param("dateTo", "")
        .when()
        .get(url.formatted(25, 1))
        .then()
        .statusCode(equalTo(404));
  }

  @Test
  void testGetFdrByPspAndIurReverseDate() {

    given()
        .param("dateFrom", "27-07-2022")
        .param("dateTo", "27-05-2022")
        .when()
        .get(url.formatted(25, 1))
        .then()
        .statusCode(equalTo(404));
  }
}
