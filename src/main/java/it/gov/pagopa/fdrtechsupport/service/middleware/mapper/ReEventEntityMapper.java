package it.gov.pagopa.fdrtechsupport.service.middleware.mapper;

import it.gov.pagopa.fdrtechsupport.models.FlowRevisionInfo;
import it.gov.pagopa.fdrtechsupport.models.RevisionInfo;
import it.gov.pagopa.fdrtechsupport.repository.model.ReEventEntity;
import java.util.ArrayList;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = ComponentModel.JAKARTA)
public interface ReEventEntityMapper {


  ReEventEntityMapper INSTANCE = Mappers.getMapper(ReEventEntityMapper.class);

  default FlowRevisionInfo toFlowRevisionInfo(String flowName, List<ReEventEntity> reEvents, boolean isOld) {

    FlowRevisionInfo result = new FlowRevisionInfo();
    result.setFdr(flowName);
    result.setOrganizationId(reEvents.get(0).getOrganizationId());
    result.setPspId(reEvents.get(0).getPspId());
    result.setCreated(reEvents.get(0).getCreated());
    result.setRevisions(new ArrayList<>());

    List<RevisionInfo> revisions;
    if (isOld) {
      revisions = reEvents.stream()
          .map(reEvent -> new RevisionInfo("NA", reEvent.getCreated()))
          .toList();
    } else {
      revisions = reEvents.stream()
          .map(reEvent -> new RevisionInfo(reEvent.getRevision().toString(), reEvent.getCreated()))
          .toList();
    }
    result.getRevisions().addAll(revisions);

    return result;
  }
}
