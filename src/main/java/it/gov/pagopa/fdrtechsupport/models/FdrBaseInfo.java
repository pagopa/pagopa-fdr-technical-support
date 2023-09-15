package it.gov.pagopa.fdrtechsupport.models;

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
public class FdrBaseInfo {
    private String fdr;
    private String created;
    private String organizationId;
}
