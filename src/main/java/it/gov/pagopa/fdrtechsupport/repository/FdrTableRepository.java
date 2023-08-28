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
    private String dateFilterString = "PartitionKey ge '%s' and PartitionKey lt '%s'";
    private String dateFilter(LocalDate datefrom,LocalDate dateTo){
        return String.format(dateFilterString, Util.format(datefrom), Util.format(dateTo.plusDays(1)));
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
        ee.setServiceIdentifier(getString(e.getProperty("appVersion")));
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

    public List<FdrEventEntity> findWithParams(
            LocalDate datefrom, LocalDate dateTo,  Optional<String> pspId, Optional<String> flowName, Optional<String> organizationFiscalCode,
            Optional<List<String>> actions) {

        StringBuilder filterBuilder = new StringBuilder(dateFilter(datefrom,dateTo));

        pspId.map(psp->filterBuilder.append(String.format(" and pspId eq '%s'", psp)));
        organizationFiscalCode.map(orgId->filterBuilder.append(String.format(" and organizationId eq '%s'", orgId)));
        actions.ifPresent(act->{
            filterBuilder.append(" and ( ");
            filterBuilder.append(String.join(" or ",act.stream().map(a->String.format(" flowAction eq '%s' ",a)).toList()));
            filterBuilder.append(" )");
        });

        flowName.ifPresentOrElse(
                fn->filterBuilder.append(String.format(" and flowName eq '%s'", fn)),
                ()->filterBuilder.append(" and flowName ne ''"));
        return runQuery(filterBuilder.toString());
    }


    public List<FdrEventEntity> findSpecificWithParams(LocalDate dateFrom, LocalDate dateTo, Optional<String> pspId, Optional<String> iuv, Optional<String> flowName, Optional<String> organizationFiscalCode) {

        StringBuilder filterBuilder = new StringBuilder(dateFilter(dateFrom, dateTo));

        pspId.ifPresent(psp -> filterBuilder.append(String.format(" and pspId eq '%s'", psp)));
        iuv.ifPresent(i -> filterBuilder.append(String.format(" and iuv eq '%s'", i)));
        organizationFiscalCode.ifPresent(orgId -> filterBuilder.append(String.format(" and organizationId eq '%s'", orgId)));

        flowName.ifPresentOrElse(
                fn -> filterBuilder.append(String.format(" and flowName eq '%s'", fn)),
                () -> filterBuilder.append(" and flowName ne ''"));

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
                        this::tableEntityToEventEntity)
                .collect(Collectors.toList());
    }
}
