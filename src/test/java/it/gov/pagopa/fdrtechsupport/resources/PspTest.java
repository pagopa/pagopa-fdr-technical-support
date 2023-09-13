package it.gov.pagopa.fdrtechsupport.resources;

import com.azure.data.tables.TableClient;
import com.azure.data.tables.TableServiceClient;
import com.azure.data.tables.TableServiceClientBuilder;
import io.quarkiverse.mockserver.test.MockServerTestResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import it.gov.pagopa.fdrtechsupport.resources.response.FdrFullInfoResponse;
import it.gov.pagopa.fdrtechsupport.util.AppConstantTestHelper;
import it.gov.pagopa.fdrtechsupport.util.AzuriteResource;
import it.gov.pagopa.fdrtechsupport.util.MongoResource;
import it.gov.pagopa.fdrtechsupport.util.Util;
import lombok.SneakyThrows;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openapi.quarkus.api_fdr_json.model.Payment;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;

import static io.restassured.RestAssured.given;
import static it.gov.pagopa.fdrtechsupport.util.AppConstantTestHelper.PA_CODE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
@QuarkusTestResource(MockServerTestResource.class)
@QuarkusTestResource(AzuriteResource.class)
@QuarkusTestResource(MongoResource.class)
class PspTest {

  @ConfigProperty(name = "fdr-re-table-storage.connection-string")
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
  @DisplayName("get fdr old")
  void getFdrOld() {
      String flowName="test1";
      String url = "/organizations/%s/psps/%s/flows/%s/revisions/%s".formatted(
              PA_CODE,"psp",flowName,1
      );
      getTableClient().createEntity(AppConstantTestHelper.newFdr(LocalDate.now().minusDays(100),PA_CODE, flowName,0,false));

      FdrFullInfoResponse res =
        given()
            .param("dateFrom", Util.format(LocalDate.now().minusDays(101)))
            .param("dateTo", Util.format(LocalDate.now().minusDays(99)))
            .when()
            .get(url)
            .then()
            .statusCode(200)
            .extract()
            .as(new TypeRef<FdrFullInfoResponse>() {});
      assertThat(res.getData(),equalTo("<xml>test</xml>"));
  }

    @SneakyThrows
    @Test
    @DisplayName("get fdr new")
    void getFdrNew() {
        String flowName="test2";
        String url = "/organizations/%s/psps/%s/flows/%s/revisions/%s".formatted(
                PA_CODE,"psp",flowName,1
        );
        getTableClient().createEntity(AppConstantTestHelper.newFdr(LocalDate.now().minusDays(100),PA_CODE, flowName,1,true));

        FdrFullInfoResponse res =
                given()
                        .param("dateFrom", Util.format(LocalDate.now().minusDays(101)))
                        .param("dateTo", Util.format(LocalDate.now().minusDays(99)))
                        .when()
                        .get(url)
                        .then()
                        .statusCode(200)
                        .extract()
                        .as(new TypeRef<FdrFullInfoResponse>() {});
        List<LinkedHashMap> data = (List<LinkedHashMap>)res.getData();
        assertThat(data.get(0).get("iuv"),equalTo("iuv"));
    }

}
