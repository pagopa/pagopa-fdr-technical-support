package it.gov.pagopa.fdrtechsupport.models;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class FdrRevisionInfo extends FdrBaseInfo{
    private String flowName;
    private String created;
}
