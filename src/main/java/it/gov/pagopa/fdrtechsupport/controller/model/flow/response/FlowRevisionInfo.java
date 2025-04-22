package it.gov.pagopa.fdrtechsupport.controller.model.flow.response;

import java.util.List;
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
public class FlowRevisionInfo extends FlowBaseInfo {

  private String pspId;

  private List<RevisionInfo> revisions;
}
