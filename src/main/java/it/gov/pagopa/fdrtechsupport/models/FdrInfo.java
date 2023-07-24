package it.gov.pagopa.fdrtechsupport.models;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FdrInfo {
    private String flowId;
    private String flowTime;
    private String identificativoUnivocoRegolamento;
    private String istitutoMittente;
    private String istitutoRicevente;
    private String numeroTotalePagamenti;
    private String importoTotalePagamenti;
    private String DataOraInvioFlusso;
    private String EsitoCaricamentoFlusso;
}
