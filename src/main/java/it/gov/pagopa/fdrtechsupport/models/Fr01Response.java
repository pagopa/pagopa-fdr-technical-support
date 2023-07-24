package it.gov.pagopa.fdrtechsupport.models;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Fr01Response {

    private String flowAction;
    private String timestamp;
    private String outcome;
    private String pspId;
    private String req;
    private String res;
}
