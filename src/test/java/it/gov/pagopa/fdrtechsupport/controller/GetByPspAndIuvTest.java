package it.gov.pagopa.fdrtechsupport.controller;

import static io.restassured.RestAssured.given;
import static it.gov.pagopa.fdrtechsupport.util.AppConstantTestHelper.PSP_CODE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

import com.azure.data.tables.TableClient;
import com.azure.data.tables.TableServiceClient;
import com.azure.data.tables.TableServiceClientBuilder;
import io.quarkiverse.mockserver.test.MockServerTestResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import it.gov.pagopa.fdrtechsupport.controller.model.flow.response.FlowBaseInfo;
import it.gov.pagopa.fdrtechsupport.controller.model.report.response.MultipleFlowsResponse;
import it.gov.pagopa.fdrtechsupport.util.AppConstantTestHelper;
import it.gov.pagopa.fdrtechsupport.util.AzuriteResource;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;

@QuarkusTest
@QuarkusTestResource(MockServerTestResource.class)
@QuarkusTestResource(AzuriteResource.class)
// @QuarkusTestResource(MongoResource.class)
public class GetByPspAndIuvTest {

  @ConfigProperty(name = "blob-storage.fdr1.connection-string")
  String connString;

  public static final String url = "/psps/%s/iuv/%s";
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
  void testGetFdrByPspAndIuv() {
    String flowName = RandomStringUtils.randomAlphabetic(20);
    String orgId = RandomStringUtils.randomAlphabetic(10);
    String iuv = RandomStringUtils.randomAlphabetic(20);
    LocalDate created = LocalDate.now();
    getTableClient()
        .createEntity(
            AppConstantTestHelper.newTableFdrPaymentPublish(
                created, PSP_CODE, orgId, flowName, iuv));
    DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
    MultipleFlowsResponse res =
        given()
            .param("dateFrom", created.toString())
            .param("dateTo", created.toString())
            .when()
            .get(url.formatted(PSP_CODE, iuv))
            .then()
            .statusCode(200)
            .extract()
            .as(new TypeRef<>() {});

    List<FlowBaseInfo> data = res.getData();
    assertThat(data.size(), greaterThan(0));
    assertThat(data.get(0).getFdr(), equalTo(flowName));
    assertThat(
        OffsetDateTime.parse(data.get(0).getCreated(), formatter),
        equalTo(created.atStartOfDay().atOffset(ZoneOffset.UTC)));
  }

  @Test
  void testGetFdrByPspAndIuvMalformedError() {

    given()
        .param("dateFrom", "27-07-2022")
        .param("dateTo", "27-07-2022")
        .when()
        .get(url.formatted(25, 1))
        .then()
        .statusCode(equalTo(404));
  }

  @Test
  void testGetFdrByPspAndIuvEmptyParamError() {

    given()
        .param("dateFrom", "")
        .param("dateTo", "")
        .when()
        .get(url.formatted(25, 1))
        .then()
        .statusCode(equalTo(404));
  }

  @Test
  void testGetFdrByPspAndIuvReverseDate() {

    given()
        .param("dateFrom", "27-07-2022")
        .param("dateTo", "27-05-2022")
        .when()
        .get(url.formatted(25, 1))
        .then()
        .statusCode(equalTo(404));
  }
}
