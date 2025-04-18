package it.gov.pagopa.fdrtechsupport.repository.nosql;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import it.gov.pagopa.fdrtechsupport.repository.model.FdR1MetadataEntity;
import it.gov.pagopa.fdrtechsupport.repository.nosql.base.NoSQLQueryBuilder;
import it.gov.pagopa.fdrtechsupport.repository.nosql.base.Repository;
import it.gov.pagopa.fdrtechsupport.service.model.DateRequest;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class FdR1MetadataRepository extends Repository
    implements PanacheRepository<FdR1MetadataEntity> {

  public List<FdR1MetadataEntity> find(
      DateRequest reDates,
      Optional<String> flowName,
      Optional<String> pspId,
      Optional<String> organizationId) {

    // set standard clauses on query
    NoSQLQueryBuilder queryBuilder = NoSQLQueryBuilder.startQuery();
    queryBuilder.andInDateRange(
        "PartitionKey", "dateFrom", reDates.getFrom(), "dateTo", reDates.getTo().plusDays(1));

    // set flow name clause on query
    flowName.ifPresent(value -> queryBuilder.andEquals("flowId", "flowName", value));

    // set PSP Identifier clause on query
    pspId.ifPresent(value -> queryBuilder.andEquals("psp", "pspId", value));

    // set organization Identifier clause on query
    organizationId.ifPresent(
        value -> queryBuilder.andEquals("creditorInstitution", "orgId", value));

    // finally, execute the complete query
    return FdR1MetadataEntity.findByQuery(queryBuilder.getQuery(), queryBuilder.getParameters())
        .project(FdR1MetadataEntity.class)
        .list();
  }
}
