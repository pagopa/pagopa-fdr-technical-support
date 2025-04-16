package it.gov.pagopa.fdrtechsupport.service;

import it.gov.pagopa.fdrtechsupport.client.FdrRestClient;
import it.gov.pagopa.fdrtechsupport.client.model.PaginatedFlowsBySenderAndReceiverResponse;
import it.gov.pagopa.fdrtechsupport.controller.model.response.FlowContentResponse;
import it.gov.pagopa.fdrtechsupport.controller.model.response.MultipleFlowsOnSingleDateResponse;
import it.gov.pagopa.fdrtechsupport.controller.model.response.MultipleFlowsResponse;
import it.gov.pagopa.fdrtechsupport.models.*;
import it.gov.pagopa.fdrtechsupport.repository.FdrTableRepository;
import it.gov.pagopa.fdrtechsupport.repository.model.ReEventEntity;
import it.gov.pagopa.fdrtechsupport.repository.nosql.ReEventRepository;
import it.gov.pagopa.fdrtechsupport.repository.storage.FdR1HistoryRepository;
import it.gov.pagopa.fdrtechsupport.repository.storage.FdR3HistoryRepository;
import it.gov.pagopa.fdrtechsupport.service.middleware.mapper.ClientResponseMapper;
import it.gov.pagopa.fdrtechsupport.service.middleware.mapper.ReEventEntityMapper;
import it.gov.pagopa.fdrtechsupport.util.common.DateUtil;
import it.gov.pagopa.fdrtechsupport.util.error.enums.AppErrorCodeMessageEnum;
import it.gov.pagopa.fdrtechsupport.util.error.exception.AppException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
@Slf4j
public class WorkerService {

  private static final String outcomeOK = "OK";
  private static final String outcomeKO = "KO";

  @ConfigProperty(name = "re-cosmos.day-limit")
  Integer reCosmosDayLimit;

  @ConfigProperty(name = "date-range-limit")
  Integer dateRangeLimit;

  @Inject ReEventRepository reEventRepository;

  @Inject FdR1HistoryRepository fdr1HistoryRepository;

  @Inject FdR3HistoryRepository fdr3HistoryRepository;

  @Inject FdrTableRepository fdrTableRepository;

  @RestClient FdrRestClient fdrClient;

  @Inject ReEventEntityMapper reEventMapper;

  @Inject ClientResponseMapper clientResponseMapper;

  public MultipleFlowsResponse searchFlowByPsp(
      String pspId, String flowName, String organizationId, LocalDate dateFrom, LocalDate dateTo) {

    // check dates and get valid ones
    DateRequest dateRequest = DateUtil.getValidDateRequest(dateFrom, dateTo, dateRangeLimit);

    // retrieve RE events from MongoDB, then check if something is found
    List<ReEventEntity> reEvents =
        reEventRepository.find(
            dateRequest,
            Optional.ofNullable(pspId),
            Optional.ofNullable(flowName),
            Optional.ofNullable(organizationId),
            Optional.of(List.of("PUBLISH")),
            Optional.of("internalPublished"));
    if (reEvents.isEmpty()) {
      throw new AppException(AppErrorCodeMessageEnum.FLOW_NOT_FOUND);
    }

    // grouping RE events by flow name and sort them by creation date
    Map<String, List<ReEventEntity>> reEventGroups =
        reEvents.stream().collect(Collectors.groupingBy(ReEventEntity::getFdr));
    reEventGroups
        .values()
        .forEach(eventGroup -> eventGroup.sort(Comparator.comparing(ReEventEntity::getCreated)));
    log.debug(
        "Found [{}] different flow names in [{}] total events!",
        reEventGroups.size(),
        reEvents.size());

    // map the element and return the required result
    List<FlowBaseInfo> collect =
        reEventGroups.values().stream()
            .map(eventGroup -> reEventMapper.toFlowBaseInfo(eventGroup))
            .collect(Collectors.toList());
    return MultipleFlowsResponse.builder()
        .dateFrom(dateRequest.getFrom())
        .dateTo(dateRequest.getTo())
        .data(collect)
        .build();
  }

  public MultipleFlowsResponse searchFlowByPspAndIuv(
      String pspId, String iuv, LocalDate dateFrom, LocalDate dateTo) {

    // check dates and get valid ones
    DateTimeRequest dateTimeRequest =
        DateUtil.getValidDateTimeRequest(dateFrom, dateTo, dateRangeLimit);

    // call FdR-Fase3 API in order to retrieve required response, searching by IUV
    PaginatedFlowsBySenderAndReceiverResponse response =
        fdrClient.getFlowByIuv(
            pspId, iuv, dateTimeRequest.getFrom(), dateTimeRequest.getTo(), 0, 1000);

    // map the element and return the required result
    List<FlowBaseInfo> dataResponse =
        response.getData().stream()
            .map(e -> clientResponseMapper.toFlowBaseInfo(e))
            .sorted(Comparator.comparing(FlowBaseInfo::getCreated))
            .toList();
    return MultipleFlowsResponse.builder()
        .dateFrom(dateTimeRequest.getFrom().toLocalDate())
        .dateTo(dateTimeRequest.getTo().toLocalDate())
        .data(dataResponse)
        .build();
  }

  public MultipleFlowsResponse searchFlowByPspAndIur(
      String pspId, String iur, LocalDate dateFrom, LocalDate dateTo) {

    // check dates and get valid ones
    DateTimeRequest dateTimeRequest =
        DateUtil.getValidDateTimeRequest(dateFrom, dateTo, dateRangeLimit);

    // call FdR-Fase3 API in order to retrieve required response, searching by IUR
    PaginatedFlowsBySenderAndReceiverResponse response =
        fdrClient.getFlowByIur(
            pspId, iur, dateTimeRequest.getFrom(), dateTimeRequest.getTo(), 0, 1000);

    // map the element and return the required result
    List<FlowBaseInfo> dataResponse =
        response.getData().stream()
            .map(e -> clientResponseMapper.toFlowBaseInfo(e))
            .sorted(Comparator.comparing(FlowBaseInfo::getCreated))
            .toList();
    return MultipleFlowsResponse.builder()
        .dateFrom(dateTimeRequest.getFrom().toLocalDate())
        .dateTo(dateTimeRequest.getTo().toLocalDate())
        .data(dataResponse)
        .build();
  }

  public MultipleFlowsOnSingleDateResponse searchFlowOperations(
      String pspId, String organizationId, List<String> actions, String flowName, LocalDate date) {

    // check dates and get valid ones
    DateRequest dateRequest = DateUtil.getValidDateRequest(date, date, dateRangeLimit);

    // retrieve RE events from MongoDB, then check if something is found
    List<ReEventEntity> reEvents =
        reEventRepository.find(
            dateRequest,
            Optional.ofNullable(pspId),
            Optional.ofNullable(flowName),
            Optional.ofNullable(organizationId),
            Optional.of(actions),
            Optional.of("interface"));
    if (reEvents.isEmpty()) {
      throw new AppException(AppErrorCodeMessageEnum.FLOW_NOT_FOUND);
    }

    // grouping RE events by flow name and sort them by creation date
    Map<String, List<ReEventEntity>> reEventGroups =
        reEvents.stream().collect(Collectors.groupingBy(ReEventEntity::getFdr));
    reEventGroups
        .values()
        .forEach(eventGroup -> eventGroup.sort(Comparator.comparing(ReEventEntity::getCreated)));
    log.debug(
        "Found [{}] different flow names in [{}] total events!",
        reEventGroups.size(),
        reEvents.size());

    // map the element and return the required result
    List<FlowBaseInfo> collect =
        reEventGroups.values().stream()
            .map(eventGroup -> reEventMapper.toFlowActionInfo(eventGroup))
            .collect(Collectors.toList());
    return MultipleFlowsOnSingleDateResponse.builder()
        .date(dateRequest.getFrom())
        .data(collect)
        .build();
  }

  public MultipleFlowsResponse getRevisions(
      String organizationId, String flowName, LocalDate dateFrom, LocalDate dateTo) {

    // check dates and get valid ones
    DateRequest dateRequest = DateUtil.getValidDateRequest(dateFrom, dateTo, dateRangeLimit);

    // retrieve RE events from MongoDB, then check if something is found
    List<ReEventEntity> reEvents =
        reEventRepository.find(
            dateRequest,
            Optional.empty(),
            Optional.ofNullable(flowName),
            Optional.ofNullable(organizationId),
            Optional.of(List.of("PUBLISH")),
            Optional.of("internalPublished"));
    if (reEvents.isEmpty()) {
      throw new AppException(AppErrorCodeMessageEnum.FLOW_NOT_FOUND);
    }

    // check if RE events found are generated not by FDR003 service (a.k.a. FdR-Fase3)
    boolean isOld = reEvents.stream().anyMatch(s -> !"FDR003".equals(s.getServiceIdentifier()));

    // If RE events are generated by FdR1 service, get only the INTERN ones
    List<ReEventEntity> flowEvents;
    if (isOld) {
      flowEvents =
          reEvents.stream()
              .filter(s -> "INTERN".equals(s.getHttpType()))
              .sorted(Comparator.comparing(ReEventEntity::getCreated))
              .toList();
    }
    // If RE events are generated by FdR3 service, get only the ones with valid revision
    else {
      flowEvents =
          reEvents.stream()
              .filter(s -> s.getRevision() != null)
              .sorted(Comparator.comparing(ReEventEntity::getCreated))
              .toList();
    }

    // If no valid flow is filtered, throw an exception
    if (flowEvents.isEmpty()) {
      throw new AppException(AppErrorCodeMessageEnum.FLOW_NOT_FOUND);
    }

    // map the element and return the required result
    FlowRevisionInfo flowRevisionInfo =
        reEventMapper.toFlowRevisionInfo(flowName, flowEvents, isOld);
    return MultipleFlowsResponse.builder()
        .dateFrom(dateRequest.getFrom())
        .dateTo(dateRequest.getTo())
        .data(List.of(flowRevisionInfo))
        .build();
  }

  public FlowContentResponse getFlow(
      String organizationId,
      String pspId,
      String flowName,
      String revision,
      LocalDate dateFrom,
      LocalDate dateTo,
      String fileType) {

    // check dates and get valid ones
    DateRequest dateRequest = DateUtil.getValidDateRequest(dateFrom, dateTo, dateRangeLimit);

    // extract file type default if not set
    fileType = (fileType == null || fileType.isBlank()) ? "json" : fileType;

    // retrieve flow content based on passed file type
    String flowContent;
    if (fileType.equalsIgnoreCase("json")) {
      flowContent =
          fdr3HistoryRepository.getByFlowNameAndPspIdAndRevision(flowName, pspId, revision);

    } else if (fileType.equalsIgnoreCase("xml")) {
      flowContent =
          fdr1HistoryRepository.getByFlowNameAndPspIdAndRevision(
              dateRequest, flowName, pspId, organizationId, revision);

    } else {
      throw new AppException(AppErrorCodeMessageEnum.INVALID_FILE_TYPE);
    }

    // map the element and return the required result
    return FlowContentResponse.builder()
        .dateFrom(dateRequest.getFrom())
        .dateTo(dateRequest.getTo())
        .data(flowContent)
        .build();
  }
}
