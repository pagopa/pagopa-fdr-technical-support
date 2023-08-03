package it.gov.pagopa.fdrtechsupport.resources.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.gov.pagopa.fdrtechsupport.models.FdrBaseInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@Jacksonized
public class FrResponse {
  private LocalDate dateFrom;
  private LocalDate dateTo;

  @JsonProperty("data")
  private List<FdrBaseInfo> data;
}