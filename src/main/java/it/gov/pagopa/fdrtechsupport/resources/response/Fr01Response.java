package it.gov.pagopa.fdrtechsupport.resources.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.gov.pagopa.fdrtechsupport.models.FdrInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@Jacksonized
public class Fr01Response {
  private LocalDate dateFrom;
  private LocalDate dateTo;

  @JsonProperty("data")
  private List<FdrInfo> data;
}
