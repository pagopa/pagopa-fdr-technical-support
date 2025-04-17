package it.gov.pagopa.fdrtechsupport.repository.storage;

import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import it.gov.pagopa.fdrtechsupport.repository.model.BlobRefEntity;
import it.gov.pagopa.fdrtechsupport.repository.model.FdR1MetadataEntity;
import it.gov.pagopa.fdrtechsupport.repository.nosql.FdR1MetadataRepository;
import it.gov.pagopa.fdrtechsupport.service.model.DateRequest;
import it.gov.pagopa.fdrtechsupport.util.common.StringUtil;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
@Slf4j
public class FdR1HistoryRepository {

  @ConfigProperty(name = "blob-storage.fdr1.connection-string")
  private String blobConnectionString;

  @ConfigProperty(name = "blob-storage.fdr1.container-name")
  private String blobContainerName;

  private BlobServiceClient blobServiceClient;

  private final FdR1MetadataRepository fdr1MetadataRepository;

  public FdR1HistoryRepository(FdR1MetadataRepository fdr1MetadataRepository) {

    this.fdr1MetadataRepository = fdr1MetadataRepository;
  }

  private BlobServiceClient getBlobServiceClient() {

    if (this.blobServiceClient == null) {
      this.blobServiceClient = new BlobServiceClientBuilder()
          .connectionString(blobConnectionString)
          .buildClient();
    }
    return this.blobServiceClient;
  }

  public String getByFlowNameAndPspIdAndRevision(
      DateRequest dateRequest,
      String flowName,
      String pspId,
      String organizationId,
      String revision) {

    String fileContent = null;
    String fileName =
        getFileNameFromMetadata(
            dateRequest, flowName, pspId, organizationId, Integer.parseInt(revision));
    if (fileName != null) {
      log.debug("Executing query on [{}] BLOB storage for file [{}]", blobContainerName, fileName);
      byte[] byteArray =
          getBlobServiceClient()
              .getBlobContainerClient(blobContainerName)
              .getBlobClient(fileName)
              .downloadContent()
              .toBytes();
      fileContent = StringUtil.decompressGZip(byteArray);
    }
    return fileContent;
  }

  private String getFileNameFromMetadata(
      DateRequest dateRequest,
      String flowName,
      String pspId,
      String organizationId,
      Integer revision) {

    List<FdR1MetadataEntity> entities =
        fdr1MetadataRepository.find(
            dateRequest,
            Optional.ofNullable(flowName),
            Optional.ofNullable(pspId),
            Optional.ofNullable(organizationId));

    entities.sort(Comparator.comparing(FdR1MetadataEntity::getFlowDate));

    String fileName = null;
    if (entities.size() >= revision) {
      BlobRefEntity blobRefEntity = entities.get(revision).getBlobBodyRef();
      if (blobRefEntity != null) {
        fileName = blobRefEntity.getFileName();
      }
    }

    return fileName;
  }
}
