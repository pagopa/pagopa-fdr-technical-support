package it.gov.pagopa.fdrtechsupport.controller.model.flow.response;

import java.time.LocalDate;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlowContentResponse {

  private LocalDate dateFrom;

  private LocalDate dateTo;

  private String data;
}
