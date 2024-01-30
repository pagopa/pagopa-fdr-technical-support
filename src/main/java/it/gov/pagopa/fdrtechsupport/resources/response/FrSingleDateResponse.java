package it.gov.pagopa.fdrtechsupport.resources.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.gov.pagopa.fdrtechsupport.models.FdrBaseInfo;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class FrSingleDateResponse {
  private LocalDate date;

  @JsonProperty("data")
  private List<FdrBaseInfo> data;
}
