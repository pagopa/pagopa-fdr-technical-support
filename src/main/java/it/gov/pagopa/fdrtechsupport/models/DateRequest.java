package it.gov.pagopa.fdrtechsupport.models;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DateRequest {

  private LocalDate from;
  private LocalDate to;
}
