package it.gov.pagopa.fdrtechsupport.repository.nosql;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import io.quarkus.panache.common.Sort.Direction;
import it.gov.pagopa.fdrtechsupport.models.DateRequest;
import it.gov.pagopa.fdrtechsupport.repository.model.FdR1MetadataEntity;
import it.gov.pagopa.fdrtechsupport.repository.nosql.base.Repository;
import it.gov.pagopa.fdrtechsupport.repository.nosql.base.SortField;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class FdR1MetadataRepository extends Repository implements PanacheRepository<FdR1MetadataEntity> {

  public List<FdR1MetadataEntity> find(
      DateRequest reDates,
      Optional<String> flowName,
      Optional<String> pspId,
      Optional<String> organizationId) {

    // set standard clauses on query
    StringBuilder query = new StringBuilder("SELECT m FROM FdR1MetadataEntity m WHERE m.flowDate >= :dateFrom AND m.flowDate <= :dateTo");
    Parameters params = new Parameters().and("dateFrom", reDates.getFrom()).and("dateTo", reDates.getTo());

    // set flow name clause on query
    if (flowName.isPresent()) {
      query.append(" and fdr = :flowName");
      params.and("flowName", flowName.get());
    }

    // set PSP Identifier clause on query
    if (pspId.isPresent()) {
      query.append(" and pspId = :pspId");
      params.and("pspId", pspId.get());
    }

    // set organization Identifier clause on query
    if (organizationId.isPresent()) {
      query.append(" and organizationId = :organizationId");
      params.and("organizationId", organizationId.get());
    }

    // define sort order on query
    Sort sort = getSort(SortField.of("flowDate", Direction.Ascending));

    // finally, execute the complete query
    return FdR1MetadataEntity.findByQuery(query.toString(), sort, params)
        .project(FdR1MetadataEntity.class)
        .list();
  }
}
