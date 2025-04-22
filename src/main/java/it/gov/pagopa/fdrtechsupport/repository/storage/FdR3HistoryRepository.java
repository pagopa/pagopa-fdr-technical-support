package it.gov.pagopa.fdrtechsupport.repository.storage;

import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobStorageException;
import it.gov.pagopa.fdrtechsupport.util.common.StringUtil;
import it.gov.pagopa.fdrtechsupport.util.constant.AppConstant;
import it.gov.pagopa.fdrtechsupport.util.error.enums.AppErrorCodeMessageEnum;
import it.gov.pagopa.fdrtechsupport.util.error.exception.AppException;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
@Slf4j
public class FdR3HistoryRepository {

  @ConfigProperty(name = "blob-storage.fdr3.connection-string")
  private String blobConnectionString;

  @ConfigProperty(name = "blob-storage.fdr3.container-name")
  private String blobContainerName;

  private BlobServiceClient blobServiceClient;

  private BlobServiceClient getBlobServiceClient() {

    if (this.blobServiceClient == null) {
      this.blobServiceClient =
          new BlobServiceClientBuilder().connectionString(blobConnectionString).buildClient();
    }
    return this.blobServiceClient;
  }

  public String getByFlowNameAndPspIdAndRevision(String flowName, String pspId, String revision) {

    String fileName =
        String.format(AppConstant.HISTORICAL_FDR3_FILENAME_TEMPLATE, flowName, pspId, revision);
    String sanitizedFileName = fileName.replaceAll("[\\r\\n]", "_");
    log.debug("Executing query on [{}] BLOB storage for file [{}]", blobContainerName, sanitizedFileName);
    try {
      byte[] byteArray =
          getBlobServiceClient()
              .getBlobContainerClient(blobContainerName)
              .getBlobClient(fileName)
              .downloadContent()
              .toBytes();
      return StringUtil.decompressGZip(byteArray);
    } catch (BlobStorageException e) {
      throw new AppException(AppErrorCodeMessageEnum.FLOW_NOT_FOUND);
    }
  }
}
