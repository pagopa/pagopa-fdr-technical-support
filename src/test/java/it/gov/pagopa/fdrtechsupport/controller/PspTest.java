package it.gov.pagopa.fdrtechsupport.controller;

import static io.restassured.RestAssured.given;
import static it.gov.pagopa.fdrtechsupport.util.AppConstantTestHelper.PA_CODE;
import static it.gov.pagopa.fdrtechsupport.util.AppConstantTestHelper.PSP_CODE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

import com.azure.core.http.rest.PagedIterable;
import com.azure.data.tables.TableClient;
import com.azure.data.tables.TableServiceClient;
import com.azure.data.tables.TableServiceClientBuilder;
import com.azure.data.tables.models.TableEntity;
import io.quarkiverse.mockserver.test.MockServerTestResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import it.gov.pagopa.fdrtechsupport.controller.model.report.response.MultipleFlowsResponse;
import it.gov.pagopa.fdrtechsupport.util.AppConstantTestHelper;
import it.gov.pagopa.fdrtechsupport.util.AzuriteResource;
import it.gov.pagopa.fdrtechsupport.util.common.DateUtil;
import java.time.LocalDate;
import lombok.SneakyThrows;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;

@QuarkusTest
@QuarkusTestResource(MockServerTestResource.class)
@QuarkusTestResource(AzuriteResource.class)
class PspTest {

  @ConfigProperty(name = "blob-storage.fdr1.connection-string")
  String connString;

  private TableClient tableClient;

  private TableClient getTableClient() {
    if (tableClient == null) {
      TableServiceClient tableServiceClient =
          new TableServiceClientBuilder().connectionString(connString).buildClient();
      tableServiceClient.createTableIfNotExists("events");
      tableClient = tableServiceClient.getTableClient("events");
    }
    return tableClient;
  }

  @SneakyThrows
  @Test
  @DisplayName("get fdr by psp table")
  void getByPspTable() {
    String flowName = RandomStringUtils.randomAlphabetic(20);
    String url = "/psps/%s".formatted(PSP_CODE);

    getTableClient()
        .createEntity(
            AppConstantTestHelper.newTableFdr(
                LocalDate.now().minusDays(100), PA_CODE, PSP_CODE, flowName, 1, true));

    PagedIterable<TableEntity> tableEntities = getTableClient().listEntities();
    MultipleFlowsResponse res =
        given()
            .param("dateFrom", DateUtil.format(LocalDate.now().minusDays(102)))
            .param("dateTo", DateUtil.format(LocalDate.now().minusDays(98)))
            .when()
            .get(url)
            .then()
            .statusCode(200)
            .extract()
            .as(new TypeRef<MultipleFlowsResponse>() {});
    assertThat(res.getData().size(), greaterThan(0));
  }
}
