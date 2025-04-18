package it.gov.pagopa.fdrtechsupport.controller.model.flow.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class FlowBaseInfo {

  private String fdr;

  private String created;

  private String organizationId;
}
