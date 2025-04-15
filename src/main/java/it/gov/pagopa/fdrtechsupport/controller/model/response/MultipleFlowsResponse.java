package it.gov.pagopa.fdrtechsupport.controller.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.gov.pagopa.fdrtechsupport.models.FlowBaseInfo;
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
public class MultipleFlowsResponse {

  private LocalDate dateFrom;

  private LocalDate dateTo;

  @JsonProperty("data")
  private List<FlowBaseInfo> data;
}
