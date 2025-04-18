package it.gov.pagopa.fdrtechsupport.repository.nosql;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import it.gov.pagopa.fdrtechsupport.repository.model.ReEventEntity;
import it.gov.pagopa.fdrtechsupport.repository.nosql.base.NoSQLQueryBuilder;
import it.gov.pagopa.fdrtechsupport.repository.nosql.base.Repository;
import it.gov.pagopa.fdrtechsupport.service.model.DateRequest;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ReEventRepository extends Repository implements PanacheRepository<ReEventEntity> {

  public List<ReEventEntity> find(
      DateRequest reDates,
      Optional<String> pspId,
      Optional<String> flowName,
      Optional<String> organizationId,
      Optional<List<String>> actions,
      Optional<String> eventAndStatus) {

    // set standard clauses on query
    NoSQLQueryBuilder queryBuilder = NoSQLQueryBuilder.startQuery();
    queryBuilder.andInDateRange(
        "PartitionKey", "dateFrom", reDates.getFrom(), "dateTo", reDates.getTo().plusDays(1));

    // set 'event-and-status' clause on query
    if (eventAndStatus.isPresent()) {
      String lowerCaseEventAndStatus = eventAndStatus.get().toLowerCase();
      if (lowerCaseEventAndStatus.equals("internalpublished")) {
        queryBuilder
            .andEquals("eventType", "type", "INTERNAL")
            .andEquals("fdrStatus", "status", "PUBLISHED");
      } else if (lowerCaseEventAndStatus.equals("interface")) {
        queryBuilder.andEquals("eventType", "type", "INTERFACE");
      }
    }

    // set PSP Identifier clause on query
    pspId.ifPresent(value -> queryBuilder.andEquals("pspId", "pspId", value));

    // set organization Identifier clause on query
    organizationId.ifPresent(value -> queryBuilder.andEquals("organizationId", "orgId", value));

    // set action clause on query
    actions.ifPresent(value -> queryBuilder.andIn("fdrAction", "actions", value));

    // set flow name clause on query
    flowName.ifPresentOrElse(
        value -> queryBuilder.andEquals("fdr", "flowName", value),
        () -> queryBuilder.andNotNull("fdr"));

    // finally, execute the complete query
    return ReEventEntity.findByQuery(queryBuilder.getQuery(), queryBuilder.getParameters())
        .project(ReEventEntity.class)
        .list();
  }
}
