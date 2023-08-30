package it.gov.pagopa.fdrtechsupport.service;

import com.azure.cosmos.implementation.apachecommons.lang.tuple.Pair;
import it.gov.pagopa.fdrtechsupport.clients.FdrOldRestClient;
import it.gov.pagopa.fdrtechsupport.clients.FdrRestClient;
import it.gov.pagopa.fdrtechsupport.exceptions.AppErrorCodeMessageEnum;
import it.gov.pagopa.fdrtechsupport.exceptions.AppException;
import it.gov.pagopa.fdrtechsupport.models.*;
import it.gov.pagopa.fdrtechsupport.repository.FdrTableRepository;
import it.gov.pagopa.fdrtechsupport.repository.model.FdrEventEntity;
import it.gov.pagopa.fdrtechsupport.resources.response.FrResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import org.openapi.quarkus.api_fdr_json.model.FdrByPspAndIuvResponse;
import org.openapi.quarkus.api_fdr_json.model.FdrInfo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class WorkerService {

  //private static final String outcomeOK = "OK";
  //private static final String outcomeKO = "KO";

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


    private List<FdrEventEntity> find(Pair<DateRequest, DateRequest> reDates,Optional<String> pspId, Optional<String> flowName, Optional<String> organizationId,Optional<List<String>> actions){
    List<FdrEventEntity> reStorageEvents = new ArrayList<>();
    if(reDates.getLeft()!=null){
      log.infof("Querying re table storage");
      reStorageEvents.addAll(
              fdrTableRepository.findWithParams(
                      reDates.getLeft().getFrom(), reDates.getLeft().getTo(), pspId, flowName, organizationId,actions
              )
      );
      log.infof("Done querying re table storage");
    }
    if(reDates.getRight()!=null){
      log.infof("Querying re cosmos");
      reStorageEvents.addAll(
              FdrEventEntity.findWithParams(
                      reDates.getRight().getFrom(),
                      reDates.getRight().getTo(),
                      pspId,
                      flowName,
                      organizationId,
                      actions
              ).stream().toList()
      );
      log.infof("Done querying re cosmos");
    }
    return reStorageEvents;
  }


  public FrResponse getFdrByParams(Optional<String> pspId, Optional<String> flowName, Optional<String> organizationId, LocalDate dateFrom, LocalDate dateTo) {

    DateRequest dateRequest = verifyDate(dateFrom, dateTo);
    Pair<DateRequest, DateRequest> reDates = getHistoryDates(dateRequest);
    List<FdrEventEntity> reStorageEvents = find(reDates,pspId, flowName, organizationId,Optional.empty());

    Map<String, List<FdrEventEntity>> reGroups =
            reStorageEvents.stream().collect(Collectors.groupingBy(FdrEventEntity::getFlowName));

    log.infof("found %d different flowNames in %d events", reGroups.size(),reStorageEvents.size());

    List<FdrBaseInfo> collect =
      reGroups.keySet().stream()
        .map(
          fn -> {
            List<FdrEventEntity> events = reGroups.get(fn);
            FdrBaseInfo fdrInfo = new FdrBaseInfo();
              List<FdrEventEntity> ordered = events.stream().sorted(Comparator.comparing(FdrEventEntity::getCreated)).toList();
              fdrInfo.setFlowName(ordered.get(0).getFlowName());
              fdrInfo.setCreated(ordered.get(0).getCreated());
              fdrInfo.setOrganizationId(ordered.stream().filter(s->s.getOrganizationId()!=null).findAny().map(FdrEventEntity::getOrganizationId).orElseGet(null));
            return fdrInfo;
          })
        .collect(Collectors.toList());

    return FrResponse.builder()
            .dateFrom(dateRequest.getFrom())
            .dateTo(dateRequest.getTo())
            .data(collect)
            .build();
  }


  public FrResponse getFdrByPspAndIuv(String pspId, String iuv, LocalDate dateFrom, LocalDate dateTo) {
    DateRequest dateRequest = verifyDate(dateFrom, dateTo);

    // Questo è fisso e serve a ottenere le pagine totali
    int pageNumber = 1;
    LocalDateTime from = dateFrom.atStartOfDay();
    LocalDateTime to = LocalDateTime.of(dateTo, LocalTime.MAX);
    // La prima chiamata serve a prendere il numero totale di pagine.
    FdrByPspAndIuvResponse reStorageEvents = fdrRestClient.getFlowByPspAndIuv(pspId, iuv, pageNumber, from, to);

    int totPages = reStorageEvents.getMetadata().getTotPage();
    String msg = String.format("Total pages %d", totPages);
    log.info(msg);

    List<FdrInfo> data = reStorageEvents.getData();

    for (int i = 2; i <= totPages; i++) {
      reStorageEvents = fdrRestClient.getFlowByPspAndIuv(pspId, iuv, i, from, to);
      data.addAll(reStorageEvents.getData());
    }

    List<FdrBaseInfo> dataResponse = data.stream()
            .map(fn -> new FdrBaseInfo(fn.getFdr(), fn.getCreated(), fn.getOrganizationId())).toList();

    return FrResponse.builder()
            .dateFrom(dateRequest.getFrom())
            .dateTo(dateRequest.getTo())
            .data(dataResponse.stream().sorted(Comparator.comparing(FdrBaseInfo::getCreated)).toList())
            .build();
  }


  public FrResponse getFdrActions(String pspId, Optional<String> flowName, Optional<String> organizationId, Optional<List<String>> actions,LocalDate dateFrom, LocalDate dateTo) {

    DateRequest dateRequest = verifyDate(dateFrom, dateTo);
    Pair<DateRequest, DateRequest> reDates = getHistoryDates(dateRequest);
    List<FdrEventEntity> reStorageEvents = find(reDates,Optional.of(pspId), flowName, organizationId,actions);

    if(reStorageEvents.isEmpty()){
      throw new AppException(
              AppErrorCodeMessageEnum.FLOW_NOT_FOUND
      );
    }

    Map<String, List<FdrEventEntity>> reGroups =
            reStorageEvents.stream().collect(Collectors.groupingBy(FdrEventEntity::getSessionId));

    log.infof("found %d different flowNames in %d events", reGroups.size(),reStorageEvents.size());

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
                              fdrInfo.setServiceIdentifier(ordered.get(0).getServiceIdentifier());
                              fdrInfo.setOrganizationId(ordered.get(0).getOrganizationId());
                              fdrInfo.setOrganizationId(ordered.stream().filter(s->s.getOrganizationId()!=null).findAny().map(FdrEventEntity::getOrganizationId).orElse(null));
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
              AppErrorCodeMessageEnum.DATE_BAD_REQUEST,
              "Date from and date to must be both defined");
    } else if (dateFrom != null && dateFrom.isAfter(dateTo)) {
      throw new AppException(
              AppErrorCodeMessageEnum.DATE_BAD_REQUEST,
              "Date from must be before date to");
    }
    if (dateFrom == null) {
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

    if(dateRequest.getFrom().isBefore(dateLimit)){
      historyDateFrom = dateRequest.getFrom();
      historyDateTo = Stream.of(dateLimit,dateRequest.getTo()).min(LocalDate::compareTo).get();
    }

    if(dateRequest.getTo().isAfter(dateLimit)){
      actualDateFrom = Stream.of(dateLimit,dateRequest.getFrom()).max(LocalDate::compareTo).get();
      if(historyDateTo!=null){
        actualDateFrom = actualDateFrom.plusDays(1);
      }
      actualDateTo = dateRequest.getTo();
    }

    return Pair.of(
                historyDateFrom!=null? DateRequest.builder().from(historyDateFrom).to(historyDateTo).build():null,
                actualDateFrom!=null? DateRequest.builder().from(actualDateFrom).to(actualDateTo).build():null
    );
  }

  public FrResponse getRevisions(String organizationId, String flowName, LocalDate dateFrom, LocalDate dateTo){

    DateRequest dateRequest = verifyDate(dateFrom, dateTo);
    Pair<DateRequest, DateRequest> reDates = getHistoryDates(dateRequest);
    List<FdrEventEntity> reStorageEvents = find(reDates,Optional.empty(), Optional.of(flowName), Optional.of(organizationId),Optional.empty());

    if(reStorageEvents.isEmpty()){
      throw new AppException(
              AppErrorCodeMessageEnum.FLOW_NOT_FOUND
      );
    }

    boolean isNew = reStorageEvents.stream().anyMatch(s -> s.getServiceIdentifier().equals("FDR003"));

    List<FdrEventEntity> flowEvents;
    if(isNew){
      flowEvents = reStorageEvents.stream()
              .filter(s->s.getRevision()!=null && s.getFlowName()!=null && "CREATE_FLOW".equals(s.getFlowAction())).sorted(Comparator.comparing(FdrEventEntity::getCreated)).toList();
    } else{
      flowEvents = reStorageEvents.stream()
              .filter(s->"REQ".equals(s.getHttpType()) && s.getFlowName()!=null && "nodoInviaFlussoRendicontazione".equals(s.getFlowAction())).sorted(Comparator.comparing(FdrEventEntity::getCreated)).toList();
    }

    FdrRevisionInfo fdrs = new FdrRevisionInfo();
    fdrs.setFlowName(flowName);
    fdrs.setOrganizationId(flowEvents.get(0).getOrganizationId());
    fdrs.setPspId(flowEvents.get(0).getPspId());
    fdrs.setCreated(flowEvents.get(0).getCreated());
    fdrs.setRevisions(new ArrayList<>());

    if(isNew){
      flowEvents.forEach(creation-> fdrs.getRevisions().add(
              new RevisionInfo(creation.getRevision().toString(), creation.getCreated())
      ));
    }else {
      flowEvents.forEach(creation-> fdrs.getRevisions().add(
              new RevisionInfo(creation.getCreated(), creation.getCreated())
      ));
    }


    return FrResponse.builder().dateFrom(dateFrom).dateTo(dateTo).data(List.of(fdrs)).build();
  }

  public String getFlow(String organizationId,String psp, String flowName,String revision, LocalDate dateFrom, LocalDate dateTo){

    DateRequest dateRequest = verifyDate(dateFrom, dateTo);
    Pair<DateRequest, DateRequest> reDates = getHistoryDates(dateRequest);
    List<FdrEventEntity> reStorageEvents = find(reDates,Optional.empty(), Optional.of(flowName), Optional.of(organizationId),Optional.empty());

    if(reStorageEvents.isEmpty()){
      throw new AppException(
              AppErrorCodeMessageEnum.FLOW_NOT_FOUND
      );
    }

    boolean isNew = reStorageEvents.stream().anyMatch(s -> s.getServiceIdentifier().equals("FDR003"));

    if(isNew){
      Optional<FdrEventEntity> max = reStorageEvents.stream().max(Comparator.comparingInt(FdrEventEntity::getRevision));
      return fdrRestClient.getFlow(organizationId,flowName);
    }else {
      String body = "";
      return fdrOldRestClient.nodoChiediFlussoRendicontazione(body);
    }
  }

}
