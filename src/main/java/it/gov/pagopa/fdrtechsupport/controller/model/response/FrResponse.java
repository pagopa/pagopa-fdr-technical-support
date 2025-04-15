package it.gov.pagopa.fdrtechsupport.controllers.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.gov.pagopa.fdrtechsupport.models.FdrBaseInfo;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FrResponse {
  private LocalDate dateFrom;
  private LocalDate dateTo;

  @JsonProperty("data")
  private List<FdrBaseInfo> data;
}
