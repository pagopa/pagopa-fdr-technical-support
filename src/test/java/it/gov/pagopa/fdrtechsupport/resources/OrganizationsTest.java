package it.gov.pagopa.fdrtechsupport.resources;

import static io.restassured.RestAssured.given;
import static it.gov.pagopa.fdrtechsupport.util.AppConstantTestHelper.PA_CODE;
import static it.gov.pagopa.fdrtechsupport.util.AppConstantTestHelper.PSP_CODE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import com.azure.core.util.BinaryData;
import com.azure.data.tables.TableClient;
import com.azure.data.tables.TableServiceClient;
import com.azure.data.tables.TableServiceClientBuilder;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import io.quarkiverse.mockserver.test.MockServerTestResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import it.gov.pagopa.fdrtechsupport.resources.response.FdrFullInfoResponse;
import it.gov.pagopa.fdrtechsupport.util.AppConstantTestHelper;
import it.gov.pagopa.fdrtechsupport.util.AzuriteResource;
import it.gov.pagopa.fdrtechsupport.util.Util;
import java.time.LocalDate;
import java.util.Base64;
import lombok.SneakyThrows;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;

@QuarkusTest
@QuarkusTestResource(MockServerTestResource.class)
@QuarkusTestResource(AzuriteResource.class)
class OrganizationsTest {

  @ConfigProperty(name = "fdr-history-table-storage.connection-string")
  String connString;

  @ConfigProperty(name = "fdr-history-blob-storage.connection-string")
  String blobConnString;

  private TableClient tableClient;
  private BlobServiceClient blobServiceClient = null;

  private TableClient getTableClient() {
    if (tableClient == null) {
      TableServiceClient tableServiceClient =
          new TableServiceClientBuilder().connectionString(connString).buildClient();
      tableServiceClient.createTableIfNotExists("fdrpublish");
      tableClient = tableServiceClient.getTableClient("fdrpublish");
    }
    return tableClient;
  }

  private BlobContainerClient getBlobContainerClient(String containerName) {
    blobServiceClient =
        new BlobServiceClientBuilder().connectionString(blobConnString).buildClient();
    blobServiceClient.createBlobContainerIfNotExists(containerName);
    return blobServiceClient.getBlobContainerClient(containerName);
  }

  @SneakyThrows
  @Test
  @DisplayName("get fdr json")
  void getFdrJson() {
    String flowName = RandomStringUtils.randomAlphabetic(20);
    String containerName = "fdrhistory";
    String fileName = flowName + ".json.zip";
    String url =
        "/organizations/%s/psps/%s/flows/%s/revisions/%s".formatted(PA_CODE, PSP_CODE, flowName, 1);
    getTableClient()
        .createEntity(
            AppConstantTestHelper.newTableFdrPublish(
                LocalDate.now().minusDays(100),
                PA_CODE,
                PSP_CODE,
                flowName,
                1,
                containerName,
                fileName));
    getBlobContainerClient(containerName)
        .getBlobClient(fileName)
        .upload(BinaryData.fromString("jsonzip"));

    FdrFullInfoResponse res =
        given()
            .param("dateFrom", Util.format(LocalDate.now().minusDays(101)))
            .param("dateTo", Util.format(LocalDate.now().minusDays(99)))
            .param("fileType", "json")
            .when()
            .get(url)
            .then()
            .statusCode(200)
            .extract()
            .as(new TypeRef<FdrFullInfoResponse>() {});
    assertThat(new String(Base64.getDecoder().decode(res.getData())), equalTo("jsonzip"));
  }

  @SneakyThrows
  @Test
  @DisplayName("get fdr xml")
  void getFdrXml() {
    String flowName = RandomStringUtils.randomAlphabetic(20);
    String url =
        "/organizations/%s/psps/%s/flows/%s/revisions/%s".formatted(PA_CODE, PSP_CODE, flowName, 1);
    getTableClient()
        .createEntity(
            AppConstantTestHelper.newTableFdrPublish(
                LocalDate.now().minusDays(100), PA_CODE, PSP_CODE, flowName, 1, "", ""));

    FdrFullInfoResponse res =
        given()
            .param("dateFrom", Util.format(LocalDate.now().minusDays(101)))
            .param("dateTo", Util.format(LocalDate.now().minusDays(99)))
            .param("fileType", "xml")
            .when()
            .get(url)
            .then()
            .statusCode(200)
            .extract()
            .as(new TypeRef<FdrFullInfoResponse>() {});
    String data = res.getData();
    assertThat(res.getData(), equalTo("<xml>test</xml>"));
  }
}
