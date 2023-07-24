package it.gov.pagopa.fdrtechsupport.service;

import it.gov.pagopa.fdrtechsupport.exceptions.AppErrorCodeMessageEnum;
import it.gov.pagopa.fdrtechsupport.exceptions.AppException;
import it.gov.pagopa.fdrtechsupport.models.DateRequest;
import it.gov.pagopa.fdrtechsupport.models.FdrInfo;
import it.gov.pagopa.fdrtechsupport.repository.CosmosReEventClient;
import it.gov.pagopa.fdrtechsupport.repository.model.FdrEventEntity;
import it.gov.pagopa.fdrtechsupport.resources.model.SearchRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.time.LocalDate;
import java.util.Optional;

@ApplicationScoped
public class WorkerService {

  private static String outcomeOK = "OK";
  private static String outcomeKO = "KO";

  @Inject Logger log;

  @Inject
  CosmosReEventClient fdrReClient;

  private FdrInfo eventToPaymentInfo(FdrEventEntity ee) {
    return FdrInfo.builder()
        .build();
  }


  public void getFdr01(
          String pspId,
          LocalDate dateFrom, LocalDate dateTo,
          SearchRequest searchRequest) {

    DateRequest dateRequest = verifyDate(dateFrom, dateTo);
      Optional<FdrEventEntity> pos =
        fdrReClient.findEventsFr01(
          pspId,
          dateRequest.getFrom(),
          dateRequest.getTo(),
          searchRequest.getCreditorInstitution(),
          searchRequest.getFlowId(),
          searchRequest.getIuv()
        )
        .stream()
        .findFirst();

//    return TransactionResponse.builder()
//        .dateFrom(dateRequest.getFrom())
//        .dateTo(dateRequest.getTo())
//        .payments(pais)
//        .build();
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
          AppErrorCodeMessageEnum.POSITION_SERVICE_DATE_BAD_REQUEST,
          "Date from and date to must be both defined");
    } else if (dateFrom != null && dateTo != null && dateFrom.isAfter(dateTo)) {
      throw new AppException(
          AppErrorCodeMessageEnum.POSITION_SERVICE_DATE_BAD_REQUEST,
          "Date from must be before date to");
    }
    if (dateFrom == null && dateTo == null) {
      dateTo = LocalDate.now();
      dateFrom = dateTo.minusDays(10);
    }
    return DateRequest.builder().from(dateFrom).to(dateTo).build();
  }
}
