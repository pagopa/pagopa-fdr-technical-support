package it.gov.pagopa.fdrtechsupport.repository;

import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.SqlParameter;
import com.azure.cosmos.models.SqlQuerySpec;
import com.azure.cosmos.util.CosmosPagedIterable;
import io.quarkus.runtime.Startup;
import it.gov.pagopa.fdrtechsupport.repository.model.FdrEventEntity;
import it.gov.pagopa.fdrtechsupport.util.Util;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Startup
public class CosmosReEventClient {

  @ConfigProperty(name = "fdr-re-cosmos.endpoint")
  private String endpoint;

  @ConfigProperty(name = "fdr-re-cosmos.key")
  private String key;

  public static String dbname = "fdr-re";
  public static String tablename = "events";

  private CosmosClient client;

  @Inject Logger log;

  private CosmosClient getClient() {
    if (client == null) {
      client = new CosmosClientBuilder().endpoint(endpoint).key(key).buildClient();
    }
    return client;
  }

  private CosmosPagedIterable<FdrEventEntity> query(SqlQuerySpec query) {
    log.info("executing query:" + query.getQueryText());
    CosmosContainer container = getClient().getDatabase(dbname).getContainer(tablename);
    return container.queryItems(query, new CosmosQueryRequestOptions(), FdrEventEntity.class);
  }

  public CosmosPagedIterable<FdrEventEntity> findEventsFr01(
          String pspId, LocalDate dateFrom, LocalDate dateTo, Optional<String> organizationFiscalCode,
          Optional<String> flowId,
          Optional<String> iuv
  ) {
    List<SqlParameter> paramList =
        Arrays.asList(
            new SqlParameter("@pspId", pspId),
            new SqlParameter("@from", Util.toMillis(dateFrom.atStartOfDay())),
            new SqlParameter("@to", Util.toMillis(LocalDateTime.of(dateTo, LocalTime.MAX))));



    StringBuilder query = new StringBuilder("SELECT * FROM c where");
    query.append(" c.pspId = @pspId");
    query.append(" and c.created > @from");
    query.append(" and c.created < @to");

    organizationFiscalCode.ifPresent(ofs->{
      query.append(" and c.organizationId = @organizationId");
      paramList.add(
              new SqlParameter("@organizationId", organizationFiscalCode)
      );
    });

    flowId.ifPresent(ofs->{
      query.append(" and c.flowId = @flowId");
      paramList.add(
              new SqlParameter("@flowId", flowId)
      );
    });

    SqlQuerySpec q = new SqlQuerySpec(query.toString()).setParameters(paramList);
    return query(q);
  }

  public CosmosPagedIterable<FdrEventEntity> findEventsFr03(
          String pspId, LocalDate dateFrom, LocalDate dateTo, Optional<String> organizationFiscalCode,
          Optional<String> flowId,
          Optional<String> iuv
  ) {
    List<SqlParameter> paramList =
            Arrays.asList(
                    new SqlParameter("@pspId", pspId),
                    new SqlParameter("@from", Util.toMillis(dateFrom.atStartOfDay())),
                    new SqlParameter("@to", Util.toMillis(LocalDateTime.of(dateTo, LocalTime.MAX))));



    StringBuilder query = new StringBuilder("SELECT * FROM c where");
    query.append(" c.pspId = @pspId");
    query.append(" and c.created > @from");
    query.append(" and c.created < @to");

    organizationFiscalCode.ifPresent(ofs->{
      query.append(" and c.organizationId = @organizationId");
      paramList.add(
              new SqlParameter("@organizationId", organizationFiscalCode)
      );
    });

    flowId.ifPresent(ofs->{
      query.append(" and c.flowId = @flowId");
      paramList.add(
              new SqlParameter("@flowId", flowId)
      );
    });

    SqlQuerySpec q = new SqlQuerySpec(query.toString()).setParameters(paramList);
    return query(q);
  }

}