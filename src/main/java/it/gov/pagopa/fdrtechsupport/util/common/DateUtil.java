package it.gov.pagopa.fdrtechsupport.util.common;

import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@ApplicationScoped
public class DateUtil {

  private DateUtil() {}

  public static String format(LocalDate d) {
    return d.format(DateTimeFormatter.ISO_DATE);
  }
}
