package it.gov.pagopa.fdrtechsupport.repository;

import static it.gov.pagopa.fdrtechsupport.repository.model.FdrEventEntity.*;

import com.azure.data.tables.TableClient;
import com.azure.data.tables.TableServiceClient;
import com.azure.data.tables.TableServiceClientBuilder;
import com.azure.data.tables.models.ListEntitiesOptions;
import com.azure.data.tables.models.TableEntity;
import it.gov.pagopa.fdrtechsupport.repository.model.FdrEventEntity;
import it.gov.pagopa.fdrtechsupport.util.Util;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.eclipse.microprofile.config.inject.ConfigProperty;

// @Startup
@ApplicationScoped
// @UnlessBuildProfile("test")
public class FdrTableRepository {

  @ConfigProperty(name = "fdr-re-table-storage.connection-string")
  String connString;

  @ConfigProperty(name = "fdr-re-table-storage.table-name")
  String tableName;

  private TableServiceClient tableServiceClient = null;
  private String internalPublishedFilter =
      " and eventType eq 'INTERNAL' and fdrStatus eq 'PUBLISHED' ";
  private String interfaceFilter = " and eventType eq 'INTERFACE' ";
  private String dateFilterString = "%s ge '%s' and %s lt '%s'";

  private String dateFilter(LocalDate datefrom, LocalDate dateTo) {
    return String.format(
        dateFilterString,
        PARTITIONKEY,
        Util.format(datefrom),
        PARTITIONKEY,
        Util.format(dateTo.plusDays(1)));
  }

  private List<String> propertiesToSelect =
      Arrays.asList(
          NAME,
          ACTION,
          PSP,
          ORGANIZATION,
          CREATED,
          "serviceIdentifier",
          "sessionId",
          "eventType",
          "httpType",
          "httpMethod",
          "httpUrl",
          "bodyRef",
          "header",
          "fdrPhisicalDelete",
          "fdrStatus",
          "revision",
          "serviceIdentifier");

  public TableClient getTableClient() {
    if (tableServiceClient == null) {
      tableServiceClient =
          new TableServiceClientBuilder().connectionString(connString).buildClient();
      tableServiceClient.createTableIfNotExists(tableName);
    }
    return tableServiceClient.getTableClient(tableName);
  }

  private FdrEventEntity tableEntityToEventEntity(TableEntity e) {
    FdrEventEntity ee = new FdrEventEntity();
    ee.setFdr(getString(e.getProperty(NAME)));
    ee.setPspId(getString(e.getProperty(PSP)));
    ee.setOrganizationId(getString(e.getProperty(ORGANIZATION)));
    ee.setFdrAction(getString(e.getProperty(ACTION)));
    ee.setCreated(getString(e.getProperty(CREATED)));
    ee.setSessionId(getString(e.getProperty("sessionId")));
    ee.setEventType(getString(e.getProperty("eventType")));
    ee.setFdrStatus(getString(e.getProperty("fdrStatus")));
    ee.setRevision(getInteger(e.getProperty("revision")));
    ee.setServiceIdentifier(getString(e.getProperty("serviceIdentifier")));
    ee.setHttpType(getString(e.getProperty("httpType")));
    // ee.setHttpMethod(getString(e.getProperty("httpMethod")));
    // ee.setHttpUrl(getString(e.getProperty("httpUrl")));
    // ee.setFdrPhisicalDelete(getString(e.getProperty("fdrPhisicalDelete")));
    return ee;
  }

  public List<FdrEventEntity> findWithParams(
      LocalDate datefrom,
      LocalDate dateTo,
      Optional<String> pspId,
      Optional<String> flowName,
      Optional<String> organizationFiscalCode,
      Optional<List<String>> actions,
      Optional<String> eventAndStatus) {

    StringBuilder filterBuilder = new StringBuilder(dateFilter(datefrom, dateTo));
    if (eventAndStatus.isPresent()) {
      switch (eventAndStatus.get().toLowerCase()) {
        case "internalpublished":
          filterBuilder.append(internalPublishedFilter);
          break;
        case "interface":
          filterBuilder.append(interfaceFilter);
          break;
      }
    }

    pspId.map(psp -> filterBuilder.append(String.format(" and %s eq '%s'", PSP, psp)));
    organizationFiscalCode.map(
        orgId -> filterBuilder.append(String.format(" and %s eq '%s'", ORGANIZATION, orgId)));

    flowName.ifPresentOrElse(
        fn -> filterBuilder.append(String.format(" and %s eq '%s'", NAME, fn)),
        () -> filterBuilder.append(String.format(" and %s ne ''", NAME)));
    return runQuery(filterBuilder.toString());
  }

  private String getString(Object o) {
    if (o == null) return null;
    return (String) o;
  }

  private Integer getInteger(Object o) {
    if (o == null) return null;
    return (Integer) o;
  }

  private List<FdrEventEntity> runQuery(String filter) {
    ListEntitiesOptions options =
        new ListEntitiesOptions().setFilter(filter).setSelect(propertiesToSelect);
    return getTableClient().listEntities(options, null, null).stream()
        .map(
            e -> {
              return tableEntityToEventEntity(e);
            })
        .collect(Collectors.toList());
  }
}
