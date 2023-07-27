package it.gov.pagopa.fdrtechsupport.service;

import com.azure.cosmos.implementation.apachecommons.lang.tuple.Pair;
import it.gov.pagopa.fdrtechsupport.exceptions.AppErrorCodeMessageEnum;
import it.gov.pagopa.fdrtechsupport.exceptions.AppException;
import it.gov.pagopa.fdrtechsupport.models.DateRequest;
import it.gov.pagopa.fdrtechsupport.models.FdrInfo;
import it.gov.pagopa.fdrtechsupport.repository.FdrTableRepository;
import it.gov.pagopa.fdrtechsupport.repository.model.FdrEventEntity;
import it.gov.pagopa.fdrtechsupport.resources.model.SearchRequest;
import it.gov.pagopa.fdrtechsupport.resources.response.Fr01Response;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

  private FdrInfo eventTFdrInfo(FdrEventEntity ee) {
    return FdrInfo.builder()
        .build();
  }


  public Fr01Response getFdr01(
          String pspId, SearchRequest body, LocalDate dateFrom, LocalDate dateTo) {

    DateRequest dateRequest = verifyDate(dateFrom, dateTo);

    Pair<DateRequest, DateRequest> reDates = getHistoryDates(dateRequest);
    List<FdrEventEntity> reStorageEvents = new ArrayList<>();
    if(reDates.getLeft()!=null){
      log.infof("Querying re table storage");
      reStorageEvents.addAll(
        fdrTableRepository.findByPspId(
                reDates.getLeft().getFrom(), reDates.getLeft().getTo(), pspId
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
          pspId
        ).stream().toList()
      );
      log.infof("Done querying re cosmos");
    }

    Map<String, List<FdrEventEntity>> reGroups =
            reStorageEvents.stream().collect(Collectors.groupingBy(FdrEventEntity::getFlowName));

    log.infof("found %d different flowNames", reGroups.size());

    List<FdrInfo> collect =
            reGroups.keySet().stream()
                    .map(
                            flowName -> {
                              List<FdrEventEntity> events = reGroups.get(flowName);
                              FdrEventEntity firstEvent = events.get(0);
                              return eventTFdrInfo(firstEvent);
                            })
                    .collect(Collectors.toList());

    return Fr01Response.builder()
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
    if(ChronoUnit.DAYS.between(dateFrom, dateTo)>dateRangeLimit){
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
}
