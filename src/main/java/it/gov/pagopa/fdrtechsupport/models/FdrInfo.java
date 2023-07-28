package it.gov.pagopa.fdrtechsupport.models;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FdrInfo {
    private String flowName;
    private String created;
}
