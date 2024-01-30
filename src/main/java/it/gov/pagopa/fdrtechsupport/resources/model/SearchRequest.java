package it.gov.pagopa.fdrtechsupport.resources.model;

import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchRequest {
  private Optional<String> creditorInstitution;
  private Optional<String> flowId;
  private Optional<String> iuv;
}
