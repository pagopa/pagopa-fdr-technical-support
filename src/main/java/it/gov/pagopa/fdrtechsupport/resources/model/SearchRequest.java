package it.gov.pagopa.fdrtechsupport.resources.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Optional;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchRequest {
    private Optional<String> creditorInstitution;
    private Optional<String> flowId;
    private Optional<String> iuv;
    private Optional<String> iur;
}
