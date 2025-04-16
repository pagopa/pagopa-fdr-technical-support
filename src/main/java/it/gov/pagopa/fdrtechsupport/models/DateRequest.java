package it.gov.pagopa.fdrtechsupport.models;

import java.time.LocalDate;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DateRequest {

  private LocalDate from;

  private LocalDate to;
}
