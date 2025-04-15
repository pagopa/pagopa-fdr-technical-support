package it.gov.pagopa.fdrtechsupport.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import it.gov.pagopa.fdrtechsupport.models.DateRequest;
import it.gov.pagopa.fdrtechsupport.repository.model.ReEventEntity;
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

    StringBuilder query = new StringBuilder("SELECT e FROM ReEventEntity e WHERE p.created >= :dateFrom AND p.created <= :dateTo");
    Parameters params = new Parameters().and("dateFrom", reDates.getFrom()).and("dateTo", reDates.getTo());

    if (eventAndStatus.isPresent()) {
      String lowerCaseEventAndStatus = eventAndStatus.get().toLowerCase();
      if (lowerCaseEventAndStatus.equals("internalpublished")) {
        query.append(" and eventType = :eventType and fdrStatus = :fdrStatus");
        params.and("eventType", "INTERNAL");
        params.and("fdrStatus", "PUBLISHED");
      } else if (lowerCaseEventAndStatus.equals("interface")) {
        query.append(" and eventType = :eventType");
        params.and("eventType", "INTERFACE");
      }
    }

    if (pspId.isPresent()) {
      query.append(" and pspId = :pspId");
      params.and("pspId", pspId.get());
    }

    if (organizationId.isPresent()) {
      query.append(" and organizationId = :organizationId");
      params.and("organizationId", organizationId.get());
    }

    if (actions.isPresent()) {
      query.append(" and action in :actions");
      params.and("actions", actions.get());
    }

    if (flowName.isPresent()) {
      query.append(" and fdr = :flowName");
      params.and("flowName", flowName.get());
    } else {
      query.append(" and fdr != :flowName");
      params.and("flowName", "");
    }


    return ReEventEntity.findByQuery(query.toString(), params)
        .project(ReEventEntity.class)
        .list();
  }
}
