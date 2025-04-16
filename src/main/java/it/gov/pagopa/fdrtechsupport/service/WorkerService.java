package it.gov.pagopa.fdrtechsupport.service;

import it.gov.pagopa.fdrtechsupport.client.FdrOldRestClient;
import it.gov.pagopa.fdrtechsupport.client.FdrRestClient;
import it.gov.pagopa.fdrtechsupport.controller.model.response.FlowContentResponse;
import it.gov.pagopa.fdrtechsupport.controller.model.response.MultipleFlowsResponse;
import it.gov.pagopa.fdrtechsupport.models.*;
import it.gov.pagopa.fdrtechsupport.repository.storage.FdR1HistoryRepository;
import it.gov.pagopa.fdrtechsupport.repository.storage.FdR3HistoryRepository;
import it.gov.pagopa.fdrtechsupport.repository.FdrHistoryTableRepository;
import it.gov.pagopa.fdrtechsupport.repository.FdrTableRepository;
import it.gov.pagopa.fdrtechsupport.repository.nosql.ReEventRepository;
import it.gov.pagopa.fdrtechsupport.repository.model.FdrEventEntity;
import it.gov.pagopa.fdrtechsupport.repository.model.ReEventEntity;
import it.gov.pagopa.fdrtechsupport.service.middleware.mapper.ReEventEntityMapper;
import it.gov.pagopa.fdrtechsupport.util.common.DateUtil;
import it.gov.pagopa.fdrtechsupport.util.error.enums.AppErrorCodeMessageEnum;
import it.gov.pagopa.fdrtechsupport.util.error.exception.AppException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import org.openapi.quarkus.api_fdr_json.model.FdrByPspIdIuvIurBase;

@ApplicationScoped
public class WorkerService {

  private static final String outcomeOK = "OK";
  private static final String outcomeKO = "KO";

  @Inject Logger log;

  @ConfigProperty(name = "re-cosmos.day-limit")
  Integer reCosmosDayLimit;

  @ConfigProperty(name = "date-range-limit")
  Integer dateRangeLimit;

  @Inject
  ReEventRepository reEventRepository;

  @Inject
  FdR1HistoryRepository fdr1HistoryRepository;

  @Inject
  FdR3HistoryRepository fdr3HistoryRepository;



  @Inject FdrTableRepository fdrTableRepository;


  @Inject FdrHistoryTableRepository fdrHistoryTableRepository;

  @RestClient FdrOldRestClient fdrOldRestClient;

  @RestClient FdrRestClient fdrRestClient;

  @Inject
  ReEventEntityMapper reEventMapper;

  private FlowRevisionInfo eventTFdrRevisionInfo(FdrEventEntity e) {
    return FlowRevisionInfo.builder().build();
  }

  private List<FdrEventEntity> find(
      DateRequest reDates,
      Optional<String> pspId,
      Optional<String> flowName,
      Optional<String> organizationId,
      Optional<List<String>> actions,
      Optional<String> eventAndStatus) {
    List<FdrEventEntity> reStorageEvents = new ArrayList<>();
    log.infof("Querying re table storage");
    reStorageEvents.addAll(
        fdrTableRepository.findWithParams(
            reDates.getFrom(),
            reDates.getTo(),
            pspId,
            flowName,
            organizationId,
            actions,
            eventAndStatus));
    log.infof("Done querying re table storage");
    return reStorageEvents;
  }

  public MultipleFlowsResponse getFdrByPsp(
      Optional<String> pspId,
      Optional<String> flowName,
      Optional<String> organizationId,
      LocalDate dateFrom,
      LocalDate dateTo) {

    DateRequest dateRequest = DateUtil.getValidDateRequest(dateFrom, dateTo, dateRangeLimit);
    List<FdrEventEntity> reStorageEvents = find(
            dateRequest,
            pspId,
            flowName,
            organizationId,
            Optional.of(List.of("PUBLISH")),
            Optional.of("internalPublished"));

    Map<String, List<FdrEventEntity>> reGroups =
        reStorageEvents.stream().collect(Collectors.groupingBy(FdrEventEntity::getFdr));

    log.infof("found %d different flowNames in %d events", reGroups.size(), reStorageEvents.size());

    List<FlowBaseInfo> collect =
        reGroups.keySet().stream()
            .map(
                fn -> {
                  List<FdrEventEntity> events = reGroups.get(fn);
                  FlowBaseInfo fdrInfo = new FlowBaseInfo();
                  List<FdrEventEntity> ordered =
                      events.stream()
                          .sorted(Comparator.comparing(FdrEventEntity::getCreated))
                          .toList();
                  fdrInfo.setFdr(ordered.get(0).getFdr());
                  fdrInfo.setCreated(ordered.get(0).getCreated());
                  fdrInfo.setOrganizationId(
                      ordered.stream()
                          .filter(s -> s.getOrganizationId() != null)
                          .findAny()
                          .map(s -> s.getOrganizationId())
                          .orElseGet(() -> null));
                  return fdrInfo;
                })
            .collect(Collectors.toList());

    return MultipleFlowsResponse.builder()
        .dateFrom(dateRequest.getFrom())
        .dateTo(dateRequest.getTo())
        .data(collect)
        .build();
  }

  public MultipleFlowsResponse getFdrByPspAndIuv(
      String pspId, String iuv, LocalDate dateFrom, LocalDate dateTo) {
    DateRequest dateRequest = DateUtil.getValidDateRequest(dateFrom, dateTo, dateRangeLimit);
    List<FdrByPspIdIuvIurBase> data =
        fdrHistoryTableRepository.findFlowByPspAndIuvIur(
            dateRequest, pspId, Optional.of(iuv), Optional.empty());

    List<FlowBaseInfo> dataResponse =
        data.stream()
            .map(
                fn ->
                    new FlowBaseInfo(
                        fn.getFdr(), fn.getCreated().toString(), fn.getOrganizationId()))
            .toList();

    return MultipleFlowsResponse.builder()
        .dateFrom(dateRequest.getFrom())
        .dateTo(dateRequest.getTo())
        .data(dataResponse.stream().sorted(Comparator.comparing(FlowBaseInfo::getCreated)).toList())
        .build();
  }

  public MultipleFlowsResponse getFdrByPspAndIur(
      String pspId, String iur, LocalDate dateFrom, LocalDate dateTo) {
    DateRequest dateRequest = DateUtil.getValidDateRequest(dateFrom, dateTo, dateRangeLimit);
    List<FdrByPspIdIuvIurBase> data =
        fdrHistoryTableRepository.findFlowByPspAndIuvIur(
            dateRequest, pspId, Optional.empty(), Optional.of(iur));

    List<FlowBaseInfo> dataResponse =
        data.stream()
            .map(
                fn ->
                    new FlowBaseInfo(
                        fn.getFdr(), fn.getCreated().toString(), fn.getOrganizationId()))
            .toList();

    return MultipleFlowsResponse.builder()
        .dateFrom(dateRequest.getFrom())
        .dateTo(dateRequest.getTo())
        .data(dataResponse.stream().sorted(Comparator.comparing(FlowBaseInfo::getCreated)).toList())
        .build();
  }

  public MultipleFlowsResponse getFdrActions(
      String pspId,
      Optional<String> flowName,
      Optional<String> organizationId,
      Optional<List<String>> actions,
      LocalDate dateFrom,
      LocalDate dateTo) {

    DateRequest dateRequest = DateUtil.getValidDateRequest(dateFrom, dateTo, dateRangeLimit);
    List<FdrEventEntity> reStorageEvents = find(
            dateRequest,
            Optional.of(pspId),
            flowName,
            organizationId,
            actions,
            Optional.of("interface"));

    if (reStorageEvents.isEmpty()) {
      throw new AppException(AppErrorCodeMessageEnum.FLOW_NOT_FOUND);
    }

    Map<String, List<FdrEventEntity>> reGroups =
        reStorageEvents.stream().collect(Collectors.groupingBy(FdrEventEntity::getSessionId));

    log.infof("found %d different flowNames in %d events", reGroups.size(), reStorageEvents.size());

    List<FlowBaseInfo> collect =
        reGroups.keySet().stream()
            .map(
                fn -> {
                  List<FdrEventEntity> events = reGroups.get(fn);
                  FlowActionInfo fdrInfo = new FlowActionInfo();
                  List<FdrEventEntity> ordered =
                      events.stream()
                          .sorted(Comparator.comparing(FdrEventEntity::getCreated))
                          .toList();
                  fdrInfo.setFdr(ordered.get(0).getFdr());
                  fdrInfo.setCreated(ordered.get(0).getCreated());
                  fdrInfo.setFlowAction(ordered.get(0).getFdrAction());
                  fdrInfo.setServiceIdentifier(ordered.get(0).getServiceIdentifier());
                  fdrInfo.setOrganizationId(ordered.get(0).getOrganizationId());
                  fdrInfo.setOrganizationId(
                      ordered.stream()
                          .filter(s -> s.getOrganizationId() != null)
                          .findAny()
                          .map(FdrEventEntity::getOrganizationId)
                          .orElse(null));
                  return fdrInfo;
                })
            .collect(Collectors.toList());

    return MultipleFlowsResponse.builder()
        .dateFrom(dateRequest.getFrom())
        .dateTo(dateRequest.getTo())
        .data(collect.stream().sorted(Comparator.comparing(FlowBaseInfo::getCreated)).toList())
        .build();
  }

  public MultipleFlowsResponse getRevisions(
      String organizationId, String flowName, LocalDate dateFrom, LocalDate dateTo) {

    // check dates and get valid ones
    DateRequest dateRequest = DateUtil.getValidDateRequest(dateFrom, dateTo, dateRangeLimit);

    // retrieve RE events from MongoDB, then check if something is found
    List<ReEventEntity> reEvents = reEventRepository.find(
        dateRequest,
        Optional.empty(),
        Optional.of(flowName),
        Optional.of(organizationId),
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
      flowEvents = reEvents.stream()
          .filter(s -> "INTERN".equals(s.getHttpType()))
          .sorted(Comparator.comparing(ReEventEntity::getCreated))
          .toList();
    }
    // If RE events are generated by FdR3 service, get only the ones with valid revision
    else {
      flowEvents = reEvents.stream()
          .filter(s -> s.getRevision() != null)
          .sorted(Comparator.comparing(ReEventEntity::getCreated))
          .toList();
    }

    // If no valid flow is filtered, throw an exception
    if (flowEvents.isEmpty()) {
      throw new AppException(AppErrorCodeMessageEnum.FLOW_NOT_FOUND);
    }

    // map the element and return the required result
    FlowRevisionInfo flowRevisionInfo = reEventMapper.toFlowRevisionInfo(flowName, flowEvents, isOld);
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
      flowContent = fdr3HistoryRepository.getByFlowNameAndPspIdAndRevision(flowName, pspId, revision);

    } else if (fileType.equalsIgnoreCase("xml")) {
      flowContent = fdr1HistoryRepository.getByFlowNameAndPspIdAndRevision(dateRequest, flowName, pspId, organizationId, revision);

    } else {
      throw new AppException(AppErrorCodeMessageEnum.INVALID_FILE_TYPE);
    }

    return FlowContentResponse.builder()
        .dateFrom(dateRequest.getFrom())
        .dateTo(dateRequest.getTo())
        .data(flowContent)
        .build();
  }
}
