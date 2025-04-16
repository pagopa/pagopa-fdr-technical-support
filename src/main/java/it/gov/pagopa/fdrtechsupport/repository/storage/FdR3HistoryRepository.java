package it.gov.pagopa.fdrtechsupport.repository.storage;

import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import it.gov.pagopa.fdrtechsupport.util.common.StringUtil;
import it.gov.pagopa.fdrtechsupport.util.constant.AppConstant;
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

  private final BlobServiceClient blobServiceClient;

  public FdR3HistoryRepository() {

    this.blobServiceClient = new BlobServiceClientBuilder()
        .connectionString(blobConnectionString)
        .buildClient();
  }

  public String getByFlowNameAndPspIdAndRevision(String flowName, String pspId, String revision) {

    String fileName = String.format(AppConstant.HISTORICAL_FDR3_FILENAME_TEMPLATE, flowName, pspId, revision);
    log.debug("Executing query on [{}] BLOB storage for file [{}]", blobContainerName, fileName);
    byte[] byteArray = this.blobServiceClient.getBlobContainerClient(blobContainerName)
        .getBlobClient(fileName)
        .downloadContent()
        .toBytes();
    return StringUtil.decompressGZip(byteArray);
  }
}
