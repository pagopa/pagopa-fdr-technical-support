package it.gov.pagopa.fdrtechsupport.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import io.quarkiverse.mockserver.test.MockServerTestResource;
import io.quarkus.test.InjectMock;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import it.gov.pagopa.fdrtechsupport.controller.model.flow.response.FlowContentResponse;
import it.gov.pagopa.fdrtechsupport.repository.model.BlobRefEntity;
import it.gov.pagopa.fdrtechsupport.repository.model.FdR1MetadataEntity;
import it.gov.pagopa.fdrtechsupport.repository.nosql.FdR1MetadataRepository;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.zip.GZIPOutputStream;
import lombok.SneakyThrows;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import it.gov.pagopa.fdrtechsupport.util.AzuriteResource;

@QuarkusTest
@QuarkusTestResource(MockServerTestResource.class)
@QuarkusTestResource(AzuriteResource.class)
class OrganizationsFlowXmlTest {

  @ConfigProperty(name = "blob-storage.fdr1.connection-string")
  String blobConnString;

  @ConfigProperty(name = "blob-storage.fdr1.container-name")
  String fdr1ContainerName;

  @InjectMock
  FdR1MetadataRepository fdr1MetadataRepository;

  private BlobContainerClient container;

  @BeforeEach
  void setup() {
    BlobServiceClient blobServiceClient =
        new BlobServiceClientBuilder().connectionString(blobConnString).buildClient();
    blobServiceClient.createBlobContainerIfNotExists(fdr1ContainerName);
    container = blobServiceClient.getBlobContainerClient(fdr1ContainerName);
  }

  @SneakyThrows
  @Test
  @DisplayName("getFlow xml - revision 3 - OK (valid gzip)")
  void getFlowXmlRevision3_ok() {
    String flowName = "2025-10-06UNCRITMM-r17ty6c9thyu";
    String orgId = "06322711216";
    String pspId = "UNCRITMM";
    String revision = "3";

    String file1 = flowName + "_rev1.xml.zip";
    String file2 = flowName + "_rev2.xml.zip";
    String file3 = flowName + "_rev3.xml.zip";

    when(fdr1MetadataRepository.find(any(), eq(Optional.of(flowName)), eq(Optional.of(pspId)), eq(Optional.of(orgId))))
        .thenReturn(new ArrayList<>(List.of(
            meta("2025-10-06T07:53:27Z", file2),
            meta("2025-10-06T08:17:09Z", file3),
            meta("2025-10-06T06:34:31Z", file1)
        )));

    String soap = "<soapenv:Envelope>OK-XML</soapenv:Envelope>";
    container.getBlobClient(file3).upload(BinaryData.fromBytes(gzip(soap)), true);

    String url = "/organizations/%s/psps/%s/flows/%s/revisions/%s"
        .formatted(orgId, pspId, flowName, revision);

    FlowContentResponse res =
        given()
            .param("dateFrom", "2025-10-05")
            .param("dateTo", "2025-10-14")
            .param("fileType", "xml")
            .when()
            .get(url)
            .then()
            .statusCode(200)
            .extract()
            .as(new TypeRef<FlowContentResponse>() {});

    assertThat(res.getData(), containsString("soapenv:Envelope"));
    assertThat(res.getData(), containsString("OK-XML"));
  }

  @SneakyThrows
  @Test
  @DisplayName("getFlow xml - revision 3 - KO (non-gzip blob) -> application error, not 500")
  void getFlowXmlRevision3_notGzip_shouldNotReturn500() {
    String flowName = "2025-10-06UNCRITMM-r17ty6c9thyu";
    String orgId = "06322711216";
    String pspId = "UNCRITMM";
    String revision = "3";

    String file1 = flowName + "_rev1.xml.zip";
    String file2 = flowName + "_rev2.xml.zip";
    String file3 = flowName + "_rev3.xml.zip";

    when(fdr1MetadataRepository.find(any(), eq(Optional.of(flowName)), eq(Optional.of(pspId)), eq(Optional.of(orgId))))
        .thenReturn(new ArrayList<>(List.of(
            meta("2025-10-06T06:34:31Z", file1),
            meta("2025-10-06T07:53:27Z", file2),
            meta("2025-10-06T08:17:09Z", file3)
        )));

    container.getBlobClient(file3).upload(BinaryData.fromString("<xml>NOT_GZIP</xml>"), true);

    String url = "/organizations/%s/psps/%s/flows/%s/revisions/%s"
        .formatted(orgId, pspId, flowName, revision);

    given()
        .param("dateFrom", "2025-10-05")
        .param("dateTo", "2025-10-14")
        .param("fileType", "xml")
        .when()
        .get(url)
        .then()
        // error handled: NOT 500
        .statusCode(anyOf(is(404), is(400)));
  }

  private static byte[] gzip(String s) throws Exception {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    try (GZIPOutputStream gz = new GZIPOutputStream(bos)) {
      gz.write(s.getBytes(StandardCharsets.UTF_8));
    }
    return bos.toByteArray();
  }

  private static FdR1MetadataEntity meta(String flowDate, String fileName) {
    FdR1MetadataEntity e = new FdR1MetadataEntity();
    e.setFlowDate(flowDate);
    BlobRefEntity ref = new BlobRefEntity();
    ref.setFileName(fileName);
    e.setBlobBodyRef(ref);
    return e;
  }
}