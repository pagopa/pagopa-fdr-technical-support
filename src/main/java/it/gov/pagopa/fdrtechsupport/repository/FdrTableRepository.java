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
    private String dateFilter = "PartitionKey ge '%s' and PartitionKey le '%s'";
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
        ee.setAppVersion(getString(e.getProperty("appVersion")));
        ee.setCreated(getString(e.getProperty("created")));
        ee.setSessionId(getString(e.getProperty("sessionId")));
        ee.setEventType(getString(e.getProperty("eventType")));
        ee.setFlowName(getString(e.getProperty("flowName")));
        ee.setPspId(getString(e.getProperty("pspId")));
        ee.setOrganizationId(getString(e.getProperty("organizationId")));
        ee.setFlowAction(getString(e.getProperty("flowAction")));
        ee.setHttpType(getString(e.getProperty("httpType")));
        ee.setHttpMethod(getString(e.getProperty("httpMethod")));
        ee.setHttpUrl(getString(e.getProperty("httpUrl")));
//            ee.setBodyRef(e.getProperty("bodyRef")));
//            ee.setHeader(e.getProperty("header")));
        ee.setFlowPhisicalDelete(getString(e.getProperty("flowPhisicalDelete")));
        ee.setFlowStatus(getString(e.getProperty("flowStatus")));
        ee.setRevision(getInteger(e.getProperty("revision")));
        return ee;
    }

    public List<FdrEventEntity> findByPspId(
            LocalDate datefrom, LocalDate dateTo, String pspId, Optional<String> flowName, Optional<String> organizationFiscalCode) {

        String filter =
                String.format(dateFilter + " and pspId eq '%s'", Util.format(datefrom), Util.format(dateTo), pspId);
        if (flowName.isPresent()) {
            filter += String.format(" and flowName eq '%s'", flowName.get());
        }else{
            filter += " and flowName ne ''";
        }
        if (organizationFiscalCode.isPresent()) {
            filter += String.format(" and organizationId eq '%s'", organizationFiscalCode.get());
        }
        return runQuery(filter);
    }

    public List<FdrEventEntity> findByOrganizationIdAndFlowName(
            LocalDate datefrom, LocalDate dateTo, String organizationId, String flowName) {

        String filter =
                String.format(
                        dateFilter + " and organizationId eq '%s' and flowName eq '%s'",
                        Util.format(datefrom), Util.format(dateTo), organizationId, flowName);
        return runQuery(filter);
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
