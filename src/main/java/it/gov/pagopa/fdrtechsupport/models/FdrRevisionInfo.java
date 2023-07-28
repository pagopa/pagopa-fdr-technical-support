package it.gov.pagopa.fdrtechsupport.models;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FdrRevisionInfo {
    private String flowName;
    private String created;
    private Long revision;
}
