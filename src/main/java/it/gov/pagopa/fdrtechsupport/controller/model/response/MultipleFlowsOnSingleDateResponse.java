package it.gov.pagopa.fdrtechsupport.controller.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.gov.pagopa.fdrtechsupport.models.FlowBaseInfo;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class MultipleFlowsOnSingleDateResponse {

  private LocalDate date;

  @JsonProperty("data")
  private List<FlowBaseInfo> data;
}
