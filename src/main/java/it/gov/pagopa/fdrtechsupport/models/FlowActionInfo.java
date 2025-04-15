package it.gov.pagopa.fdrtechsupport.models;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class FlowActionInfo extends FlowBaseInfo {
  private String flowAction;
  private String serviceIdentifier;
}
