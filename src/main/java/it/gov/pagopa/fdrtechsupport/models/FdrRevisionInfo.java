package it.gov.pagopa.fdrtechsupport.models;

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
public class FdrRevisionInfo extends FdrBaseInfo {
  private String pspId;
  private List<RevisionInfo> revisions;
}
