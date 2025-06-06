package it.gov.pagopa.fdrtechsupport.service.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DateTimeRequest {

  private LocalDateTime from;

  private LocalDateTime to;
}
