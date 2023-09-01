package it.gov.pagopa.fdrtechsupport.resources.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.gov.pagopa.fdrtechsupport.models.FdrBaseInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

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
