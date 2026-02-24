package it.gov.pagopa.fdrtechsupport.controller;

import static io.restassured.RestAssured.given;
import static it.gov.pagopa.fdrtechsupport.util.AppConstantTestHelper.PA_CODE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import it.gov.pagopa.fdrtechsupport.controller.model.flow.response.FlowContentResponse;
import it.gov.pagopa.fdrtechsupport.util.ContainersTestResource;
import it.gov.pagopa.fdrtechsupport.util.ObjectMapperUtil;
import it.gov.pagopa.fdrtechsupport.util.common.DateUtil;
import it.gov.pagopa.fdrtechsupport.util.constant.AppConstant;
import java.time.LocalDate;
import java.time.Month;
import lombok.SneakyThrows;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;

@QuarkusTest
@QuarkusTestResource(value = ContainersTestResource.class)
class OrganizationsTest {

  private static final String PSP_CODE = "88888888888";
  private static final String ORG_ID = "77777777777";

  @ConfigProperty(name = "blob-storage.fdr.connection-string")
  String connString;

  @ConfigProperty(name = "blob-storage.fdr3.container-name")
  String fdr3ContainerName;

  @ConfigProperty(name = "blob-storage.fdr1.container-name")
  String fdr1ContainerName;

  private BlobContainerClient getBlobContainerClient(String blobContainerName) {
    BlobServiceClient blobServiceClient =
        new BlobServiceClientBuilder().connectionString(connString).buildClient();
    blobServiceClient.createBlobContainerIfNotExists(blobContainerName);
    return blobServiceClient.getBlobContainerClient(blobContainerName);
  }

  @SneakyThrows
  @Test
  @DisplayName("get fdr json")
  void getFdrJson() {
    String flowName = RandomStringUtils.randomAlphabetic(20);
    String revision = "1";
    String fileName =
        String.format(AppConstant.HISTORICAL_FDR3_FILENAME_TEMPLATE, flowName, PSP_CODE, revision);
    String url =
        "/organizations/%s/psps/%s/flows/%s/revisions/%s"
            .formatted(PA_CODE, PSP_CODE, flowName, revision);

    getBlobContainerClient(fdr3ContainerName)
        .getBlobClient(fileName)
        .upload(BinaryData.fromStream(ObjectMapperUtil.readFromFile("FDR3.gz")));

    FlowContentResponse res =
        given()
            .param("dateFrom", DateUtil.format(LocalDate.now().minusDays(1)))
            .param("dateTo", DateUtil.format(LocalDate.now().minusDays(1)))
            .param("fileType", "json")
            .when()
            .get(url)
            .then()
            .statusCode(200)
            .extract()
            .as(new TypeRef<FlowContentResponse>() {});

    assertEquals("Test FDR 3", res.getData());
  }

  @SneakyThrows
  @Test
  @DisplayName("get fdr xml")
  void getFdrXml() {
    String flowName = "2026-01-1188888888888-664371785";
    String fileName = flowName + "_cf48692b-5269-4cf5-a6ed-8de6c83c6097.xml.zip";
    String url =
        "/organizations/%s/psps/%s/flows/%s/revisions/%s".formatted(ORG_ID, PSP_CODE, flowName, 1);
    LocalDate date = LocalDate.of(2026, Month.JANUARY, 11);

    getBlobContainerClient(fdr1ContainerName)
        .getBlobClient(fileName)
        .upload(BinaryData.fromStream(ObjectMapperUtil.readFromFile("FDR1.gz")));

    FlowContentResponse res =
        given()
            .param("dateFrom", DateUtil.format(date.minusDays(1)))
            .param("dateTo", DateUtil.format(date.plusDays(1)))
            .param("fileType", "xml")
            .when()
            .get(url)
            .then()
            .statusCode(200)
            .extract()
            .as(new TypeRef<FlowContentResponse>() {});

    assertEquals("Test FDR 1", res.getData());
  }
}
