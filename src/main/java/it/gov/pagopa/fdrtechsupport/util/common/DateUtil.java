package it.gov.pagopa.fdrtechsupport.util.common;

import it.gov.pagopa.fdrtechsupport.models.DateRequest;
import it.gov.pagopa.fdrtechsupport.util.error.enums.AppErrorCodeMessageEnum;
import it.gov.pagopa.fdrtechsupport.util.error.exception.AppException;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@ApplicationScoped
public class DateUtil {

  private DateUtil() {}

  public static String format(LocalDate d) {
    return d.format(DateTimeFormatter.ISO_DATE);
  }

  /**
   * The method retrieve the DateRequest object regarding the dateFrom and dateTo input.<br>
   * The only accepted date combination are:
   *
   * <ul>
   *   <li>dateFrom and dateTo as <i>null</i>
   *   <li>dateFrom and dateTo as <i>not null</i>
   *   <li>dateFrom <i>after</i> dateTo
   * </ul>
   *
   * @param dateFrom the starting date boundary
   * @param dateTo the ending date boundary
   * @param dateRangeLimit the days to be subtracted to dateTo if dateFrom is null
   * @return the DateRequest object with valid dates
   */
  public static DateRequest getValidDateRequest(
      LocalDate dateFrom, LocalDate dateTo, Integer dateRangeLimit) {

    // throw an exception if both dateFrom and dateTo are not correctly defined
    if (dateFrom == null && dateTo != null || dateFrom != null && dateTo == null) {
      throw new AppException(
          AppErrorCodeMessageEnum.DATE_BAD_REQUEST, "DateFrom and DateTo must be both defined");
    }

    // throw an exception if dateFrom is after dateTo
    if (dateFrom != null && dateFrom.isAfter(dateTo)) {
      throw new AppException(
          AppErrorCodeMessageEnum.DATE_BAD_REQUEST, "DateFrom must be before DateTo");
    }

    // set dateFrom if no dater boundaries are set
    if (dateFrom == null) {
      dateTo = LocalDate.now();
      dateFrom = dateTo.minusDays(dateRangeLimit);
    }

    // finally, return the DateRequest object with the valid date boundaries
    return DateRequest.builder().from(dateFrom).to(dateTo).build();
  }
}
