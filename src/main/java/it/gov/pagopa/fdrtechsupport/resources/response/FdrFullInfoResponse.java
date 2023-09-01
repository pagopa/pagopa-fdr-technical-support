package it.gov.pagopa.fdrtechsupport.resources.response;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FdrFullInfoResponse {
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private Object data;
}
