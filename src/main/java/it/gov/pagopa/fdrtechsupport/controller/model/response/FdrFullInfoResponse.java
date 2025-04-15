package it.gov.pagopa.fdrtechsupport.controller.model.response;

import java.time.LocalDate;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FdrFullInfoResponse {
  private LocalDate dateFrom;
  private LocalDate dateTo;
  private String data;
}
