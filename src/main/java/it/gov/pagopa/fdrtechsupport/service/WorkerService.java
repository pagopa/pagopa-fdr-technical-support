package it.gov.pagopa.fdrtechsupport.service;

import com.azure.cosmos.implementation.apachecommons.lang.tuple.Pair;
import it.gov.pagopa.fdrtechsupport.clients.FdrOldRestClient;
import it.gov.pagopa.fdrtechsupport.clients.FdrRestClient;
import it.gov.pagopa.fdrtechsupport.exceptions.AppErrorCodeMessageEnum;
import it.gov.pagopa.fdrtechsupport.exceptions.AppException;
import it.gov.pagopa.fdrtechsupport.models.DateRequest;
import it.gov.pagopa.fdrtechsupport.models.FdrActionInfo;
import it.gov.pagopa.fdrtechsupport.models.FdrBaseInfo;
import it.gov.pagopa.fdrtechsupport.models.FdrRevisionInfo;
import it.gov.pagopa.fdrtechsupport.repository.FdrTableRepository;
import it.gov.pagopa.fdrtechsupport.repository.model.FdrEventEntity;
import it.gov.pagopa.fdrtechsupport.resources.response.FrResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class WorkerService {

  private static String outcomeOK = "OK";
  private static String outcomeKO = "KO";

  @Inject Logger log;

  @ConfigProperty(name = "re-cosmos.day-limit")
  Integer reCosmosDayLimit;

  @ConfigProperty(name = "date-range-limit")
  Integer dateRangeLimit;

  @Inject
  FdrTableRepository fdrTableRepository;

  @RestClient
  FdrOldRestClient fdrOldRestClient;

  @RestClient
  FdrRestClient fdrRestClient;

  private FdrRevisionInfo eventTFdrRevisionInfo(FdrEventEntity e) {
    return FdrRevisionInfo.builder()
            .build();
  }


  public FrResponse getFdrListByPsp(String pspId, Optional<String> flowName, Optional<String> organizationId, LocalDate dateFrom, LocalDate dateTo) {

    DateRequest dateRequest = verifyDate(dateFrom, dateTo);

    Pair<DateRequest, DateRequest> reDates = getHistoryDates(dateRequest);
    List<FdrEventEntity> reStorageEvents = new ArrayList<>();
    if(reDates.getLeft()!=null){
      log.infof("Querying re table storage");
      reStorageEvents.addAll(
        fdrTableRepository.findByPspId(
                reDates.getLeft().getFrom(), reDates.getLeft().getTo(), pspId, flowName, organizationId
        )
      );
      log.infof("Done querying re table storage");
    }
    if(reDates.getRight()!=null){
      log.infof("Querying re cosmos");
      reStorageEvents.addAll(
        FdrEventEntity.findByPspId(
          reDates.getRight().getFrom(),
          reDates.getRight().getTo(),
          pspId,
          flowName,
          organizationId
        ).stream().toList()
      );
      log.infof("Done querying re cosmos");
    }

    Map<String, List<FdrEventEntity>> reGroups =
            reStorageEvents.stream().collect(Collectors.groupingBy(FdrEventEntity::getFlowName));

    log.infof("found %d different flowNames", reGroups.size());

    List<FdrBaseInfo> collect =
      reGroups.keySet().stream()
        .map(
          fn -> {
            List<FdrEventEntity> events = reGroups.get(fn);
            FdrBaseInfo fdrInfo = new FdrBaseInfo();
              List<FdrEventEntity> ordered = events.stream().sorted(Comparator.comparing(FdrEventEntity::getCreated)).toList();
              fdrInfo.setFlowName(ordered.get(0).getFlowName());
              fdrInfo.setCreated(ordered.get(0).getCreated());
            return fdrInfo;
          })
        .collect(Collectors.toList());

    return FrResponse.builder()
            .dateFrom(dateRequest.getFrom())
            .dateTo(dateRequest.getTo())
            .data(collect)
            .build();
  }

  public FrResponse getFdrDetail(String pspId, String flowName, LocalDate dateFrom, LocalDate dateTo) {

    DateRequest dateRequest = verifyDate(dateFrom, dateTo);

    Pair<DateRequest, DateRequest> reDates = getHistoryDates(dateRequest);
    List<FdrEventEntity> reStorageEvents = new ArrayList<>();
    if(reDates.getLeft()!=null){
      log.infof("Querying re table storage");
      reStorageEvents.addAll(
              fdrTableRepository.findByPspId(
                      reDates.getLeft().getFrom(), reDates.getLeft().getTo(), pspId, Optional.of(flowName), Optional.empty()
              )
      );
      log.infof("Done querying re table storage");
    }
    if(reDates.getRight()!=null){
      log.infof("Querying re cosmos");
      reStorageEvents.addAll(
              FdrEventEntity.findByPspId(
                      reDates.getRight().getFrom(),
                      reDates.getRight().getTo(),
                      pspId,
                      Optional.of(flowName),
                      Optional.empty()
              ).stream().toList()
      );
      log.infof("Done querying re cosmos");
    }

    Map<String, List<FdrEventEntity>> reGroups =
            reStorageEvents.stream().collect(Collectors.groupingBy(FdrEventEntity::getSessionId));

    log.infof("found %d different flowNames", reGroups.size());

    List<FdrBaseInfo> collect =
            reGroups.keySet().stream()
                    .map(
                            fn -> {
                              List<FdrEventEntity> events = reGroups.get(fn);
                              FdrActionInfo fdrInfo = new FdrActionInfo();
                              List<FdrEventEntity> ordered = events.stream().sorted(Comparator.comparing(FdrEventEntity::getCreated)).toList();
                              fdrInfo.setFlowName(ordered.get(0).getFlowName());
                              fdrInfo.setCreated(ordered.get(0).getCreated());
                              fdrInfo.setFlowAction(ordered.get(0).getFlowAction());
                              fdrInfo.setAppVersion(ordered.get(0).getAppVersion());
                              return fdrInfo;
                            })
                    .collect(Collectors.toList());

    return FrResponse.builder()
            .dateFrom(dateRequest.getFrom())
            .dateTo(dateRequest.getTo())
            .data(collect.stream().sorted(Comparator.comparing(FdrBaseInfo::getCreated)).toList())
            .build();
  }


  public FrResponse getFdr04(
          String organizationId, String flowName, LocalDate dateFrom, LocalDate dateTo) {

    DateRequest dateRequest = verifyDate(dateFrom, dateTo);

    Pair<DateRequest, DateRequest> reDates = getHistoryDates(dateRequest);
    List<FdrEventEntity> reStorageEvents = new ArrayList<>();
    if(reDates.getLeft()!=null){
      log.infof("Querying re table storage");
      reStorageEvents.addAll(
              fdrTableRepository.findByOrganizationIdAndFlowName(
                      reDates.getLeft().getFrom(), reDates.getLeft().getTo(), organizationId, flowName
              )
      );
      log.infof("Done querying re table storage");
    }
    if(reDates.getRight()!=null){
      log.infof("Querying re cosmos");
      reStorageEvents.addAll(
              FdrEventEntity.findByOrganizationIdAndFlowName(
                      reDates.getRight().getFrom(),
                      reDates.getRight().getTo(),
                      organizationId,
                      flowName
              ).stream().toList()
      );
      log.infof("Done querying re cosmos");
    }


    log.infof("found %d different revisions", reStorageEvents.size());

    List<FdrBaseInfo> collect =
            reStorageEvents.stream()
                    .map(fn -> eventTFdrRevisionInfo(fn))
                    .collect(Collectors.toList());

    return FrResponse.builder()
            .dateFrom(dateRequest.getFrom())
            .dateTo(dateRequest.getTo())
            .data(collect)
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
              AppErrorCodeMessageEnum.DATE_BAD_REQUEST,
              "Date from and date to must be both defined");
    } else if (dateFrom != null && dateTo != null && dateFrom.isAfter(dateTo)) {
      throw new AppException(
              AppErrorCodeMessageEnum.DATE_BAD_REQUEST,
              "Date from must be before date to");
    }
    if (dateFrom == null && dateTo == null) {
      dateTo = LocalDate.now();
      dateFrom = dateTo.minusDays(dateRangeLimit);
    }
    if (ChronoUnit.DAYS.between(dateFrom, dateTo) > dateRangeLimit - 1) {
      throw new AppException(
              AppErrorCodeMessageEnum.INTERVAL_TOO_LARGE,
              dateRangeLimit);
    }
    return DateRequest.builder().from(dateFrom).to(dateTo).build();
  }

  private Pair<DateRequest, DateRequest> getHistoryDates(DateRequest dateRequest) {


    LocalDate dateLimit = LocalDate.now().minusDays(reCosmosDayLimit);
    LocalDate historyDateFrom = null;
    LocalDate historyDateTo = null;
    LocalDate actualDateFrom = null;
    LocalDate actualDateTo = null;

    if(dateRequest.getFrom().isBefore(dateLimit) && dateRequest.getTo().isBefore(dateLimit)){
      return Pair.of(
              DateRequest.builder().from(dateRequest.getFrom()).to(dateRequest.getTo()).build(),
              null
      );
    }else if(dateRequest.getFrom().isBefore(dateLimit) && dateRequest.getTo().isAfter(dateLimit)){
      return Pair.of(
              DateRequest.builder().from(dateRequest.getFrom()).to(dateLimit).build(),
              DateRequest.builder().from(dateLimit).to(dateRequest.getTo()).build()
      );
    }else{
      return Pair.of(
              null,
              DateRequest.builder().from(dateRequest.getFrom()).to(dateRequest.getTo()).build()
      );
    }
  }


  public String getFlow(String flowId){
    String body = "";
    if(true){
      return fdrRestClient.getFlow("","");
    }else {
      return fdrOldRestClient.nodoChiediFlussoRendicontazione(body);
    }
  }
}
