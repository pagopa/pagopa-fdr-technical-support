package it.gov.pagopa.fdrtechsupport.repository;

import static it.gov.pagopa.fdrtechsupport.repository.model.FdrHistoryBlobRefEntity.*;

import com.azure.data.tables.TableClient;
import com.azure.data.tables.TableServiceClient;
import com.azure.data.tables.TableServiceClientBuilder;
import com.azure.data.tables.models.ListEntitiesOptions;
import com.azure.data.tables.models.TableEntity;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import it.gov.pagopa.fdrtechsupport.util.error.enums.AppErrorCodeMessageEnum;
import it.gov.pagopa.fdrtechsupport.util.error.exception.AppException;
import it.gov.pagopa.fdrtechsupport.models.DateRequest;
import it.gov.pagopa.fdrtechsupport.repository.model.FdrHistoryBlobRefEntity;
import it.gov.pagopa.fdrtechsupport.util.common.DateUtil;
import jakarta.enterprise.context.ApplicationScoped;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.openapi.quarkus.api_fdr_json.model.FdrByPspIdIuvIurBase;

@ApplicationScoped
public class FdrHistoryTableRepository {
  private static final String REF_PSP = "ref_fdr_sender_psp_id";
  private static final String REF_ORG = "ref_fdr_receiver_organization_id";
  private static final String REF_FDR = "ref_fdr";
  private static final String REF_REV = "ref_fdr_revision";
  private static final String CREATED = "created";
  private static final String IUV = "iuv";
  private static final String IUR = "iur";

  @ConfigProperty(name = "fdr-history-table-storage.connection-string")
  String tableConnString;

  @ConfigProperty(name = "fdr-history-publish-table-storage.table-name")
  String publishTableName;

  @ConfigProperty(name = "fdr-history-payment-publish-table-storage.table-name")
  String paymentPublishTableName;

  @ConfigProperty(name = "fdr-history-blob-storage.connection-string")
  String blobConnString;

  private TableServiceClient tableServiceClient = null;
  private BlobServiceClient blobServiceClient = null;
  private String dateFilterString = "%s ge '%s' and %s lt '%s'";

  private String dateFilter(LocalDate datefrom, LocalDate dateTo) {
    return String.format(
        dateFilterString,
        PARTITIONKEY,
        DateUtil.format(datefrom),
        PARTITIONKEY,
        DateUtil.format(dateTo.plusDays(1)));
  }

  private List<String> publishPropertiesToSelect =
      Arrays.asList(STORAGE_ACCOUNT, CONTAINER_NAME, FILE_NAME, FILE_LENGTH, JSON_SCHEMA_VERSION);
  private List<String> paymentPublishPropertiesToSelect =
      Arrays.asList(REF_PSP, REF_ORG, REF_FDR, REF_REV, CREATED);

  private TableClient getPublishTableClient() {
    if (tableServiceClient == null) {
      tableServiceClient =
          new TableServiceClientBuilder().connectionString(tableConnString).buildClient();
      tableServiceClient.createTableIfNotExists(publishTableName);
      tableServiceClient.createTableIfNotExists(paymentPublishTableName);
    }
    return tableServiceClient.getTableClient(publishTableName);
  }

  private TableClient getPaymentPublishTableClient() {
    if (tableServiceClient == null) {
      tableServiceClient =
          new TableServiceClientBuilder().connectionString(tableConnString).buildClient();
      tableServiceClient.createTableIfNotExists(paymentPublishTableName);
      tableServiceClient.createTableIfNotExists(publishTableName);
    }
    return tableServiceClient.getTableClient(paymentPublishTableName);
  }

  private BlobContainerClient getBlobContainerClient(String containerName) {
    blobServiceClient =
        new BlobServiceClientBuilder().connectionString(blobConnString).buildClient();
    blobServiceClient.createBlobContainerIfNotExists(containerName);
    return blobServiceClient.getBlobContainerClient(containerName);
  }

  private FdrHistoryBlobRefEntity findFlowByNameAndRevision(
      DateRequest dates, String flowName, String revision) {
    StringBuilder filterBuilder = new StringBuilder(dateFilter(dates.getFrom(), dates.getTo()));
    filterBuilder.append(String.format(" and %s eq '%s' ", NAME, flowName));
    filterBuilder.append(String.format(" and %s eq %s ", REVISION, Integer.valueOf(revision)));
    ListEntitiesOptions options =
        new ListEntitiesOptions()
            .setFilter(filterBuilder.toString())
            .setSelect(publishPropertiesToSelect);
    Optional<TableEntity> entity =
        getPublishTableClient().listEntities(options, null, null).stream().findFirst();
    if (entity.isEmpty()) {
      throw new AppException(AppErrorCodeMessageEnum.FLOW_NOT_FOUND);
    }
    return tableEntityToHistoryEntity(entity.get());
  }

  public List<FdrByPspIdIuvIurBase> findFlowByPspAndIuvIur(
      DateRequest dates, String psp, Optional<String> iuv, Optional<String> iur) {
    StringBuilder filterBuilder = new StringBuilder(dateFilter(dates.getFrom(), dates.getTo()));
    filterBuilder.append(String.format(" and %s eq '%s' ", REF_PSP, psp));
    if (iuv.isPresent()) {
      filterBuilder.append(String.format(" and %s eq '%s' ", IUV, iuv.get()));
    } else {
      filterBuilder.append(String.format(" and %s eq '%s' ", IUR, iur.get()));
    }
    ListEntitiesOptions options =
        new ListEntitiesOptions()
            .setFilter(filterBuilder.toString())
            .setSelect(paymentPublishPropertiesToSelect);
    return getPaymentPublishTableClient().listEntities(options, null, null).stream()
        .map(
            e -> {
              return tableEntityToIuvIurBaseEntity(e);
            })
        .collect(Collectors.toList());
  }

  private String getString(Object o) {
    if (o == null) return null;
    return (String) o;
  }

  private Long getLong(Object o) {
    if (o == null) return null;
    return (Long) o;
  }

  private OffsetDateTime getDateTime(Object o) {
    if (o == null) return null;
    DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
    return OffsetDateTime.parse(o.toString(), formatter);
  }

  private FdrHistoryBlobRefEntity tableEntityToHistoryEntity(TableEntity e) {
    FdrHistoryBlobRefEntity blobRefEntity = new FdrHistoryBlobRefEntity();
    blobRefEntity.setStorageAccount(getString(e.getProperty(STORAGE_ACCOUNT)));
    blobRefEntity.setContainerName(getString(e.getProperty(CONTAINER_NAME)));
    blobRefEntity.setFileName(getString(e.getProperty(FILE_NAME)));
    blobRefEntity.setFileLength(getLong(e.getProperty(FILE_LENGTH)));
    blobRefEntity.setJsonSchemaVersion(getString(e.getProperty(JSON_SCHEMA_VERSION)));
    return blobRefEntity;
  }

  private FdrByPspIdIuvIurBase tableEntityToIuvIurBaseEntity(TableEntity e) {
    FdrByPspIdIuvIurBase baseEntity = new FdrByPspIdIuvIurBase();
    baseEntity.setPspId(getString(e.getProperty(REF_PSP)));
    baseEntity.setOrganizationId(getString(e.getProperty(REF_ORG)));
    baseEntity.fdr(getString(e.getProperty(REF_FDR)));
    baseEntity.revision(getLong(e.getProperty(REF_REV)));
    baseEntity.created(getDateTime(e.getProperty(CREATED)));
    return baseEntity;
  }

  public String getBlobByNameAndRevision(DateRequest dates, String name, String revision) {
    FdrHistoryBlobRefEntity refEntity = findFlowByNameAndRevision(dates, name, revision);
    byte[] byteArray =
        getBlobContainerClient(refEntity.getContainerName())
            .getBlobClient(refEntity.getFileName())
            .downloadContent()
            .toBytes();
    return new String(Base64.getEncoder().encode(byteArray), StandardCharsets.UTF_8);
  }
}
