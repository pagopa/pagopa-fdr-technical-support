package it.gov.pagopa.fdrtechsupport.controller;

import it.gov.pagopa.fdrtechsupport.controller.interfaces.IOrganizationController;
import it.gov.pagopa.fdrtechsupport.controller.model.response.FdrFullInfoResponse;
import it.gov.pagopa.fdrtechsupport.controller.model.response.MultipleFlowsResponse;
import it.gov.pagopa.fdrtechsupport.controller.model.response.FrSingleDateResponse;
import it.gov.pagopa.fdrtechsupport.service.WorkerService;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

public class OrganizationController implements IOrganizationController {

  private final WorkerService workerService;

  public OrganizationController(WorkerService workerService) {
    this.workerService = workerService;
  }

  public MultipleFlowsResponse getRevisions(String organizationId, String flowId, LocalDate dateFrom, LocalDate dateTo) {

    return workerService.getRevisions(organizationId, flowId, dateFrom, dateTo);
  }

  @Override
  public FdrFullInfoResponse getFlow(String organizationId, String flowId, String pspId, String revision, LocalDate dateFrom, LocalDate dateTo, String fileType) {

    return workerService.getFlow(organizationId, pspId, flowId, revision, dateFrom, dateTo, fileType);
  }

  @Override
  public FrSingleDateResponse getDownloads(String organizationId, String pspId, LocalDate date) {
    MultipleFlowsResponse fdrActions =
        workerService.getFdrActions(
            pspId,
            Optional.empty(),
            Optional.of(organizationId),
            Optional.of(
                Arrays.asList("nodoChiediFlussoRendicontazione", "GET_FDR", "GET_FDR_PAYMENT")),
            date,
            date);
    return FrSingleDateResponse.builder()
                .data(fdrActions.getData())
                .date(fdrActions.getDateFrom())
                .build();
  }

  @Override
  public MultipleFlowsResponse getUploads(String organizationId, @NotNull String pspId, LocalDate date) {
    return workerService.getFdrActions(
                pspId,
                Optional.empty(),
                Optional.of(organizationId),
                Optional.of(
                    Arrays.asList(
                        "nodoInviaFlussoRendicontazione",
                        "PUBLISH",
                        "ADD_PAYMENT",
                        "CREATE_FLOW",
                        "DELETE_FLOW",
                        "DELETE_PAYMENT")),
                date,
                date);
  }
}
