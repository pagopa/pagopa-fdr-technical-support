package it.gov.pagopa.fdrtechsupport.service.model;

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
