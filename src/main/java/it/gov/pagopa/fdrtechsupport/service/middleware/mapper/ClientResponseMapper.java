package it.gov.pagopa.fdrtechsupport.service.middleware.mapper;

import it.gov.pagopa.fdrtechsupport.client.model.FlowBySenderAndReceiver;
import it.gov.pagopa.fdrtechsupport.models.FlowBaseInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = ComponentModel.JAKARTA)
public interface ClientResponseMapper {

  ClientResponseMapper INSTANCE = Mappers.getMapper(ClientResponseMapper.class);

  @Mapping(target = "created", expression = "java(element.getCreated().toString())")
  FlowBaseInfo toFlowBaseInfo(FlowBySenderAndReceiver element);
}
