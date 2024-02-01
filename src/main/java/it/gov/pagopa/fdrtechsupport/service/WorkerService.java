package it.gov.pagopa.fdrtechsupport.service;

import it.gov.pagopa.fdrtechsupport.clients.FdrOldRestClient;
import it.gov.pagopa.fdrtechsupport.clients.FdrRestClient;
import it.gov.pagopa.fdrtechsupport.exceptions.AppErrorCodeMessageEnum;
import it.gov.pagopa.fdrtechsupport.exceptions.AppException;
import it.gov.pagopa.fdrtechsupport.models.*;
import it.gov.pagopa.fdrtechsupport.repository.FdrHistoryTableRepository;
import it.gov.pagopa.fdrtechsupport.repository.FdrTableRepository;
import it.gov.pagopa.fdrtechsupport.repository.model.FdrEventEntity;
import it.gov.pagopa.fdrtechsupport.resources.response.FdrFullInfoResponse;
import it.gov.pagopa.fdrtechsupport.resources.response.FrResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import org.openapi.quarkus.api_fdr_json.model.FdrByPspIdIuvIurBase;
import org.openapi.quarkus.api_fdr_nodo_json.model.GetXmlRendicontazioneResponse;

@ApplicationScoped
public class WorkerService {

  private static final String outcomeOK = "OK";
  private static final String outcomeKO = "KO";

  @Inject Logger log;

  @ConfigProperty(name = "re-cosmos.day-limit")
  Integer reCosmosDayLimit;

  @ConfigProperty(name = "date-range-limit")
  Integer dateRangeLimit;

  @Inject FdrTableRepository fdrTableRepository;
  @Inject FdrHistoryTableRepository fdrHistoryTableRepository;

  @RestClient FdrOldRestClient fdrOldRestClient;

  @RestClient FdrRestClient fdrRestClient;

  private FdrRevisionInfo eventTFdrRevisionInfo(FdrEventEntity e) {
    return FdrRevisionInfo.builder().build();
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

  public FrResponse getFdrByPsp(
      Optional<String> pspId,
      Optional<String> flowName,
      Optional<String> organizationId,
      LocalDate dateFrom,
      LocalDate dateTo) {

    DateRequest dateRequest = verifyDate(dateFrom, dateTo);
    List<FdrEventEntity> reStorageEvents =
        find(
            dateRequest,
            pspId,
            flowName,
            organizationId,
            Optional.of(List.of("PUBLISH")),
            Optional.of("internalPublished"));

    Map<String, List<FdrEventEntity>> reGroups =
        reStorageEvents.stream().collect(Collectors.groupingBy(FdrEventEntity::getFdr));

    log.infof("found %d different flowNames in %d events", reGroups.size(), reStorageEvents.size());

    List<FdrBaseInfo> collect =
        reGroups.keySet().stream()
            .map(
                fn -> {
                  List<FdrEventEntity> events = reGroups.get(fn);
                  FdrBaseInfo fdrInfo = new FdrBaseInfo();
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

    return FrResponse.builder()
        .dateFrom(dateRequest.getFrom())
        .dateTo(dateRequest.getTo())
        .data(collect)
        .build();
  }

  public FrResponse getFdrByPspAndIuv(
      String pspId, String iuv, LocalDate dateFrom, LocalDate dateTo) {
    DateRequest dateRequest = verifyDate(dateFrom, dateTo);
    List<FdrByPspIdIuvIurBase> data =
        fdrHistoryTableRepository.findFlowByPspAndIuvIur(
            dateRequest, pspId, Optional.of(iuv), Optional.empty());

    List<FdrBaseInfo> dataResponse =
        data.stream()
            .map(
                fn ->
                    new FdrBaseInfo(
                        fn.getFdr(), fn.getCreated().toString(), fn.getOrganizationId()))
            .toList();

    return FrResponse.builder()
        .dateFrom(dateRequest.getFrom())
        .dateTo(dateRequest.getTo())
        .data(dataResponse.stream().sorted(Comparator.comparing(FdrBaseInfo::getCreated)).toList())
        .build();
  }

  public FrResponse getFdrByPspAndIur(
      String pspId, String iur, LocalDate dateFrom, LocalDate dateTo) {
    DateRequest dateRequest = verifyDate(dateFrom, dateTo);
    List<FdrByPspIdIuvIurBase> data =
        fdrHistoryTableRepository.findFlowByPspAndIuvIur(
            dateRequest, pspId, Optional.empty(), Optional.of(iur));

    List<FdrBaseInfo> dataResponse =
        data.stream()
            .map(
                fn ->
                    new FdrBaseInfo(
                        fn.getFdr(), fn.getCreated().toString(), fn.getOrganizationId()))
            .toList();

    return FrResponse.builder()
        .dateFrom(dateRequest.getFrom())
        .dateTo(dateRequest.getTo())
        .data(dataResponse.stream().sorted(Comparator.comparing(FdrBaseInfo::getCreated)).toList())
        .build();
  }

  public FrResponse getFdrActions(
      String pspId,
      Optional<String> flowName,
      Optional<String> organizationId,
      Optional<List<String>> actions,
      LocalDate dateFrom,
      LocalDate dateTo) {

    DateRequest dateRequest = verifyDate(dateFrom, dateTo);
    List<FdrEventEntity> reStorageEvents =
        find(
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

    List<FdrBaseInfo> collect =
        reGroups.keySet().stream()
            .map(
                fn -> {
                  List<FdrEventEntity> events = reGroups.get(fn);
                  FdrActionInfo fdrInfo = new FdrActionInfo();
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

    return FrResponse.builder()
        .dateFrom(dateRequest.getFrom())
        .dateTo(dateRequest.getTo())
        .data(collect.stream().sorted(Comparator.comparing(FdrBaseInfo::getCreated)).toList())
        .build();
  }

  /**
   * Check dates validity
   *
   * @param dateFrom
   * @param dateTo
   */
  private DateRequest verifyDate(LocalDate dateFrom, LocalDate dateTo) {
    if (dateFrom == null && dateTo != null || dateFrom != null && dateTo == null) {
      throw new AppException(
          AppErrorCodeMessageEnum.DATE_BAD_REQUEST, "Date from and date to must be both defined");
    } else if (dateFrom != null && dateFrom.isAfter(dateTo)) {
      throw new AppException(
          AppErrorCodeMessageEnum.DATE_BAD_REQUEST, "Date from must be before date to");
    }
    if (dateFrom == null) {
      dateTo = LocalDate.now();
      dateFrom = dateTo.minusDays(dateRangeLimit);
    }
    if (ChronoUnit.DAYS.between(dateFrom, dateTo) > dateRangeLimit - 1) {
      throw new AppException(AppErrorCodeMessageEnum.INTERVAL_TOO_LARGE, dateRangeLimit);
    }
    return DateRequest.builder().from(dateFrom).to(dateTo).build();
  }

  public FrResponse getRevisions(
      String organizationId, String flowName, LocalDate dateFrom, LocalDate dateTo) {
    DateRequest dateRequest = verifyDate(dateFrom, dateTo);
    List<FdrEventEntity> reStorageEvents =
        find(
            dateRequest,
            Optional.empty(),
            Optional.of(flowName),
            Optional.of(organizationId),
            Optional.of(List.of("PUBLISH")),
            Optional.of("internalPublished"));

    if (reStorageEvents.isEmpty()) {
      throw new AppException(AppErrorCodeMessageEnum.FLOW_NOT_FOUND);
    }

    boolean isOld =
        reStorageEvents.stream()
            .anyMatch(
                s ->
                    s.getServiceIdentifier() != null && !s.getServiceIdentifier().equals("FDR003"));

    List<FdrEventEntity> flowEvents;
    if (!isOld) {
      flowEvents =
          reStorageEvents.stream()
              .filter(s -> s.getRevision() != null)
              .sorted(Comparator.comparing(FdrEventEntity::getCreated))
              .toList();
    } else {
      flowEvents =
          reStorageEvents.stream()
              .filter(s -> "REQ".equals(s.getHttpType()))
              .sorted(Comparator.comparing(FdrEventEntity::getCreated))
              .toList();
    }

    if (flowEvents.isEmpty()) {
      throw new AppException(AppErrorCodeMessageEnum.FLOW_NOT_FOUND);
    }

    FdrRevisionInfo fdrs = new FdrRevisionInfo();
    fdrs.setFdr(flowName);
    fdrs.setOrganizationId(flowEvents.get(0).getOrganizationId());
    fdrs.setPspId(flowEvents.get(0).getPspId());
    fdrs.setCreated(flowEvents.get(0).getCreated());
    fdrs.setRevisions(new ArrayList<>());

    if (!isOld) {
      flowEvents.forEach(
          creation ->
              fdrs.getRevisions()
                  .add(new RevisionInfo(creation.getRevision().toString(), creation.getCreated())));
    } else {
      flowEvents.forEach(
          creation ->
              fdrs.getRevisions()
                  .add(new RevisionInfo(creation.getCreated(), creation.getCreated())));
    }

    return FrResponse.builder().dateFrom(dateFrom).dateTo(dateTo).data(List.of(fdrs)).build();
  }

  public FdrFullInfoResponse getFlow(
      String organizationId,
      String psp,
      String flowName,
      String revision,
      LocalDate dateFrom,
      LocalDate dateTo,
      String fileType) {
    DateRequest dateRequest = verifyDate(dateFrom, dateTo);
    if (fileType.equalsIgnoreCase("json")) {
      log.infof("Querying history table storage");
      String flow =
          fdrHistoryTableRepository.getBlobByNameAndRevision(dateRequest, flowName, revision);
      log.infof("Done querying history table storage");
      return FdrFullInfoResponse.builder().dateFrom(dateFrom).dateTo(dateTo).data(flow).build();
    } else if (fileType.equalsIgnoreCase("xml")) {
      GetXmlRendicontazioneResponse getXmlRendicontazioneResponse =
          fdrOldRestClient.nodoChiediFlussoRendicontazione(organizationId, flowName);
      return FdrFullInfoResponse.builder()
          .dateFrom(dateFrom)
          .dateTo(dateTo)
          .data(getXmlRendicontazioneResponse.getXmlRendicontazione())
          .build();
    } else {
      throw new AppException(AppErrorCodeMessageEnum.INVALID_FILE_TYPE);
    }
  }
}
