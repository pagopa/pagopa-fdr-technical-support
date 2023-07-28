package it.gov.pagopa.fdrtechsupport.repository;

import com.azure.data.tables.TableClient;
import com.azure.data.tables.TableServiceClient;
import com.azure.data.tables.TableServiceClientBuilder;
import com.azure.data.tables.models.ListEntitiesOptions;
import com.azure.data.tables.models.TableEntity;
import it.gov.pagopa.fdrtechsupport.repository.model.FdrEventEntity;
import it.gov.pagopa.fdrtechsupport.util.Util;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// @Startup
@ApplicationScoped
// @UnlessBuildProfile("test")
public class FdrTableRepository {

  @ConfigProperty(name = "fdr-re-table-storage.connection-string")
  String connString;

  @ConfigProperty(name = "fdr-re-table-storage.table-name")
  String tableName;

  private TableServiceClient tableServiceClient = null;

  public TableClient getTableClient() {
    if (tableServiceClient == null) {
      tableServiceClient =
          new TableServiceClientBuilder().connectionString(connString).buildClient();
      tableServiceClient.createTableIfNotExists(tableName);
    }
    return tableServiceClient.getTableClient(tableName);
  }

  private List<String> propertiesToSelect =
      Arrays.asList(
              "appVersion",
    "created",
    "sessionId",
    "eventType",
    "flowName",
    "pspId",
    "organizationId",
    "flowAction",
    "httpType",
    "httpMethod",
    "httpUrl",
    "bodyRef",
    "header",
    "flowPhisicalDelete",
    "flowStatus",
    "revision"
    );

  private FdrEventEntity tableEntityToEventEntity(TableEntity e) {
      FdrEventEntity ee = new FdrEventEntity();
    return ee;
  }

  public List<FdrEventEntity> findByPspId(
          LocalDate datefrom, LocalDate dateTo, String pspId, Optional<String> flowName) {

    String filter =
        String.format(
            "PartitionKey ge '%s' and PartitionKey le '%s' and pspId eq '%s'",
            Util.format(datefrom), Util.format(dateTo), pspId);
    if(flowName.isPresent()){
      filter+= String.format(" and flowName like '%s'",flowName.get());
    }
    ListEntitiesOptions options =
        new ListEntitiesOptions().setFilter(filter).setSelect(propertiesToSelect);
    return getTableClient().listEntities(options, null, null).stream()
        .map(
            e -> {
              return tableEntityToEventEntity(e);
            })
        .collect(Collectors.toList());
  }

  public List<FdrEventEntity> findByOrganizationIdAndFlowName(
          LocalDate datefrom, LocalDate dateTo, String organizationId, String flowName) {

    String filter =
            String.format(
                    "PartitionKey ge '%s' and PartitionKey le '%s' and organizationId eq '%s' and flowName = '%s'",
                    Util.format(datefrom), Util.format(dateTo), organizationId,flowName);
    ListEntitiesOptions options =
            new ListEntitiesOptions().setFilter(filter).setSelect(propertiesToSelect);
    return getTableClient().listEntities(options, null, null).stream()
            .map(
                    e -> {
                      return tableEntityToEventEntity(e);
                    })
            .collect(Collectors.toList());
  }

  private String getString(Object o) {
    if (o == null) return null;
    return (String) o;
  }
}
