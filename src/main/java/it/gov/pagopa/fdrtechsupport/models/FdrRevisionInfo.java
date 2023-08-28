package it.gov.pagopa.fdrtechsupport.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class FdrRevisionInfo extends FdrBaseInfo{
    private String pspId;
    private List<RevisionInfo> revisions;
}
