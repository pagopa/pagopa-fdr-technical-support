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
      DateRequest reDates, String flowName, String pspId, String organizationId) {

    // set standard clauses on query
    NoSQLQueryBuilder queryBuilder = NoSQLQueryBuilder.startQuery();
    queryBuilder
        .andInDateRange(
            "PartitionKey", "dateFrom", reDates.getFrom(), "dateTo", reDates.getTo().plusDays(1))
        .andEquals("flowId", "flowName", flowName)
        .andEquals("psp", "pspId", pspId)
        .andEquals("creditorInstitution", "orgId", organizationId)
        .andEquals("pspCreditorInstitution", "pspIdOrgId", pspId + organizationId);

    // finally, execute the complete query
    return FdR1MetadataEntity.findByQuery(queryBuilder.getQuery(), queryBuilder.getParameters())
        .project(FdR1MetadataEntity.class)
        .list();
  }
}
