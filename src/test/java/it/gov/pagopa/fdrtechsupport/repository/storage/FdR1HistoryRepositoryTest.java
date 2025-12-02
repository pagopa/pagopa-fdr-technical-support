package it.gov.pagopa.fdrtechsupport.repository.storage;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;
import it.gov.pagopa.fdrtechsupport.repository.model.BlobRefEntity;
import it.gov.pagopa.fdrtechsupport.repository.model.FdR1MetadataEntity;
import it.gov.pagopa.fdrtechsupport.repository.nosql.FdR1MetadataRepository;
import it.gov.pagopa.fdrtechsupport.service.model.DateRequest;
import it.gov.pagopa.fdrtechsupport.util.AzuriteResource;
import it.gov.pagopa.fdrtechsupport.util.common.DateUtil;
import it.gov.pagopa.fdrtechsupport.util.error.enums.AppErrorCodeMessageEnum;
import it.gov.pagopa.fdrtechsupport.util.error.exception.AppException;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.zip.GZIPOutputStream;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@QuarkusTestResource(AzuriteResource.class)
class FdR1HistoryRepositoryTest {

  @ConfigProperty(name = "blob-storage.fdr1.connection-string")
  String blobConnString;

  @ConfigProperty(name = "blob-storage.fdr1.container-name")
  String containerName;

  @Inject
  FdR1HistoryRepository fdr1HistoryRepository;

  @InjectMock
  FdR1MetadataRepository fdr1MetadataRepository;

  private BlobContainerClient container;

  @BeforeEach
  void setup() {
    BlobServiceClient blobServiceClient =
        new BlobServiceClientBuilder().connectionString(blobConnString).buildClient();
    blobServiceClient.createBlobContainerIfNotExists(containerName);
    container = blobServiceClient.getBlobContainerClient(containerName);
  }

  @Test
  @DisplayName("rev3 OK: select the 3rd element by flowDate and unpack GZIP")
  void revision3_ok_gzip() throws Exception {
    String flowName = "2025-10-06UNCRITMM-r17ty6c9thyu";
    String pspId = "UNCRITMM";
    String orgId = "06322711216";

    DateRequest dr = DateUtil.getValidDateRequest(
        LocalDate.of(2025, 10, 5),
        LocalDate.of(2025, 10, 14),
        30
    );

    String file1 = flowName + "_rev1.xml.zip";
    String file2 = flowName + "_rev2.xml.zip";
    String file3 = flowName + "_rev3.xml.zip";

    // only rev3 is loaded to blob (that's the one used)
    String soap = mockSoapEnv(flowName, innerXml(flowName, "5.00"));
    container.getBlobClient(file3).upload(BinaryData.fromBytes(gzip(soap)), true);

    when(fdr1MetadataRepository.find(
        any(DateRequest.class),
        eq(Optional.of(flowName)),
        eq(Optional.of(pspId)),
        eq(Optional.of(orgId))
    )).thenReturn(new ArrayList<>(List.of(
        meta("2025-10-06T06:34:31Z", file1),
        meta("2025-10-06T07:53:27Z", file2),
        meta("2025-10-06T08:17:09Z", file3)
    )));

    String out = fdr1HistoryRepository.getByFlowNameAndPspIdAndRevision(
        dr, flowName, pspId, orgId, "3"
    );

    assertTrue(out.contains("soapenv:Envelope"));
    assertTrue(out.contains("nodoInviaFlussoRendicontazione"));
    assertTrue(out.contains("xmlRendicontazione"));
  }

  @Test
  @DisplayName("rev3 KO: non-gzip blob -> decompressGZip and exception INVALID_BLOB_CONTENT")
  void revision3_ko_notGzip_exception() {
    String flowName = "2025-10-06UNCRITMM-r17ty6c9thyu";
    String pspId = "UNCRITMM";
    String orgId = "06322711216";

    DateRequest dr = DateUtil.getValidDateRequest(
        LocalDate.of(2025, 10, 5),
        LocalDate.of(2025, 10, 14),
        30
    );

    String file1 = flowName + "_rev1.xml.zip";
    String file2 = flowName + "_rev2.xml.zip";
    String file3 = flowName + "_rev3.xml.zip";

    // rev3 NOT GZIP file
    String raw = "<xml>NOT_GZIP</xml>";
    container.getBlobClient(file3).upload(BinaryData.fromString(raw), true);

    when(fdr1MetadataRepository.find(
        any(DateRequest.class),
        eq(Optional.of(flowName)),
        eq(Optional.of(pspId)),
        eq(Optional.of(orgId))
    )).thenReturn(new ArrayList<>(List.of(
        meta("2025-10-06T06:34:31Z", file1),
        meta("2025-10-06T07:53:27Z", file2),
        meta("2025-10-06T08:17:09Z", file3)
    )));

    // decompressGZip fails --> exception
    AppException ex = assertThrows(AppException.class, () ->
    fdr1HistoryRepository.getByFlowNameAndPspIdAndRevision(dr, flowName, pspId, orgId, "3")
    		);
    assertNotNull(ex);
    assertEquals(AppErrorCodeMessageEnum.INVALID_BLOB_CONTENT, ex.getCodeMessage());
    assertTrue(container.getBlobClient(file3).exists());
  }


  @Test
  @DisplayName("rev3 KO: metadata size < 3 -> AppException (FLOW_NOT_FOUND)")
  void revision3_ko_metadataTooShort() {
    String flowName = "flow";
    String pspId = "psp";
    String orgId = "org";

    DateRequest dr = DateUtil.getValidDateRequest(LocalDate.now().minusDays(2), LocalDate.now(), 30);

    when(fdr1MetadataRepository.find(any(), eq(Optional.of(flowName)), eq(Optional.of(pspId)), eq(Optional.of(orgId))))
        .thenReturn(new ArrayList<>(List.of(
            meta("2025-10-06T06:34:31Z", "a.xml.zip"),
            meta("2025-10-06T07:53:27Z", "b.xml.zip")
        )));

    assertThrows(AppException.class, () ->
        fdr1HistoryRepository.getByFlowNameAndPspIdAndRevision(dr, flowName, pspId, orgId, "3")
    );
  }
  
  @Test
  @DisplayName("rev3: messy metadata -> sort by flowDate still selects the 3rd (most recent)")
  void revision3_selectsNewestAfterSort() throws Exception {
    String flowName = "2025-10-06UNCRITMM-r17ty6c9thyu";
    String pspId = "UNCRITMM";
    String orgId = "06322711216";

    DateRequest dr = DateUtil.getValidDateRequest(LocalDate.of(2025,10,5), LocalDate.of(2025,10,14), 30);

    String file1 = flowName + "_rev1.xml.zip";
    String file2 = flowName + "_rev2.xml.zip";
    String file3 = flowName + "_rev3.xml.zip";

    container.getBlobClient(file3).upload(BinaryData.fromBytes(gzip("<soap>REV3</soap>")), true);

    // deliberately disordered list
    when(fdr1MetadataRepository.find(any(), eq(Optional.of(flowName)), eq(Optional.of(pspId)), eq(Optional.of(orgId))))
        .thenReturn(new ArrayList<>(List.of(
            meta("2025-10-06T07:53:27Z", file2),
            meta("2025-10-06T08:17:09Z", file3),
            meta("2025-10-06T06:34:31Z", file1)
        )));

    String out = fdr1HistoryRepository.getByFlowNameAndPspIdAndRevision(dr, flowName, pspId, orgId, "3");
    assertTrue(out.contains("REV3"));
  }
  
  @Test
  @DisplayName("flowDate: null -> the record is ignored (revision=2 selects the last valid one)")
  void flowDateNull_ignored_revision2_selectsRev3() throws Exception {
    String flowName = "2025-10-06UNCRITMM-r17ty6c9thyu";
    String pspId = "UNCRITMM";
    String orgId = "06322711216";

    DateRequest dr = DateUtil.getValidDateRequest(LocalDate.of(2025,10,5), LocalDate.of(2025,10,14), 30);

    String file1 = flowName + "_rev1.xml.zip";
    String file2 = flowName + "_rev2.xml.zip";
    String file3 = flowName + "_rev3.xml.zip";

    container.getBlobClient(file3).upload(BinaryData.fromBytes(gzip("<soap>REV3</soap>")), true);

    when(fdr1MetadataRepository.find(any(), eq(Optional.of(flowName)), eq(Optional.of(pspId)), eq(Optional.of(orgId))))
        .thenReturn(new ArrayList<>(List.of(
            meta(null, file1), // record sporco
            meta("2025-10-06T07:53:27Z", file2),
            meta("2025-10-06T08:17:09Z", file3)
        )));

    String out = fdr1HistoryRepository.getByFlowNameAndPspIdAndRevision(dr, flowName, pspId, orgId, "2");
    assertTrue(out.contains("REV3"));
  }
  
  @Test
  @DisplayName("flowDate: null -> the record is ignored (revision=3 throws FLOW_NOT_FOUND)")
  void flowDateNull_ignored_revision3_notFound() {

    String flowName = "2025-10-06UNCRITMM-r17ty6c9thyu";
    String pspId = "UNCRITMM";
    String orgId = "06322711216";

    DateRequest dr = DateUtil.getValidDateRequest(
        LocalDate.of(2025, 10, 5),
        LocalDate.of(2025, 10, 14),
        30
    );

    String file1 = flowName + "_rev1.xml.zip";
    String file2 = flowName + "_rev2.xml.zip";
    String file3 = flowName + "_rev3.xml.zip";

    // Mock metadata:
    // - a record with null flowDate (ignore)
    // - two valid records → therefore there are only 2 valid revisions
    when(fdr1MetadataRepository.find(
        any(DateRequest.class),
        eq(Optional.of(flowName)),
        eq(Optional.of(pspId)),
        eq(Optional.of(orgId))
    )).thenReturn(new ArrayList<>(List.of(
        meta(null, file1), // corrupt record → ignored
        meta("2025-10-06T07:53:27Z", file2),
        meta("2025-10-06T08:17:09Z", file3)
    )));

    // Even if the rev3 blob existed, revision 3 shouldn't exist due to only 2 valid records
    AppException ex = assertThrows(AppException.class, () ->
        fdr1HistoryRepository.getByFlowNameAndPspIdAndRevision(
            dr, flowName, pspId, orgId, "3"
        )
    );

    assertEquals(AppErrorCodeMessageEnum.FLOW_NOT_FOUND, ex.getCodeMessage());
  }



  // ---------------- helpers ----------------

  private static FdR1MetadataEntity meta(String flowDate, String fileName) {
    FdR1MetadataEntity e = new FdR1MetadataEntity();
    e.setFlowDate(flowDate);
    BlobRefEntity ref = new BlobRefEntity();
    ref.setFileName(fileName);
    e.setBlobBodyRef(ref);
    return e;
  }

  private static byte[] gzip(String s) throws Exception {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    try (GZIPOutputStream gz = new GZIPOutputStream(bos)) {
      gz.write(s.getBytes(StandardCharsets.UTF_8));
    }
    return bos.toByteArray();
  }

  private static String mockSoapEnv(String flowId, String innerXml) {
    String b64 = Base64.getEncoder().encodeToString(innerXml.getBytes(StandardCharsets.UTF_8));
    return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>"
        + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" "
        + "xmlns:ppt=\"http://ws.pagamenti.telematici.gov/\">"
        + "<soapenv:Body>"
        + "<ppt:nodoInviaFlussoRendicontazione>"
        + "<identificativoFlusso>" + flowId + "</identificativoFlusso>"
        + "<xmlRendicontazione>" + b64 + "</xmlRendicontazione>"
        + "</ppt:nodoInviaFlussoRendicontazione>"
        + "</soapenv:Body>"
        + "</soapenv:Envelope>";
  }

  private static String innerXml(String flowId, String total) {
    return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>"
        + "<FlussoRiversamento xmlns=\"http://www.digitpa.gov.it/schemas/2011/Pagamenti/\">"
        + "<versioneOggetto>1.0</versioneOggetto>"
        + "<identificativoFlusso>" + flowId + "</identificativoFlusso>"
        + "<numeroTotalePagamenti>1</numeroTotalePagamenti>"
        + "<importoTotalePagamenti>" + total + "</importoTotalePagamenti>"
        + "</FlussoRiversamento>";
  }
}