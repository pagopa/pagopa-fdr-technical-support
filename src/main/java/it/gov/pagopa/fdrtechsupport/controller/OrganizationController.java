package it.gov.pagopa.fdrtechsupport.controller;

import it.gov.pagopa.fdrtechsupport.controller.interfaces.IOrganizationController;
import it.gov.pagopa.fdrtechsupport.controller.model.response.FrResponse;
import it.gov.pagopa.fdrtechsupport.controller.model.response.FrSingleDateResponse;
import it.gov.pagopa.fdrtechsupport.service.WorkerService;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.core.Response;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

public class OrganizationController implements IOrganizationController {

  private final WorkerService workerService;

  public OrganizationController(WorkerService workerService) {
    this.workerService = workerService;
  }

  public Response getRevisions(String organizationId, String flowId, LocalDate dateFrom, LocalDate dateTo) {

    return Response.ok(workerService.getRevisions(organizationId, flowId, dateFrom, dateTo)).build();
  }

  @Override
  public Response getFlow(String organizationId, String flowId, String pspId, String revision, LocalDate dateFrom, LocalDate dateTo, String fileType) {

    return Response.ok(
            workerService.getFlow(organizationId, pspId, flowId, revision, dateFrom, dateTo, fileType))
        .build();
  }

  @Override
  public Response getDownloads(String organizationId, String pspId, LocalDate date) {
    FrResponse fdrActions =
        workerService.getFdrActions(
            pspId,
            Optional.empty(),
            Optional.of(organizationId),
            Optional.of(
                Arrays.asList("nodoChiediFlussoRendicontazione", "GET_FDR", "GET_FDR_PAYMENT")),
            date,
            date);
    return Response.ok(
            FrSingleDateResponse.builder()
                .data(fdrActions.getData())
                .date(fdrActions.getDateFrom())
                .build())
        .build();
  }

  @Override
  public Response getUploads(String organizationId, @NotNull String pspId, LocalDate date) {
    return Response.ok(
            workerService.getFdrActions(
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
                date))
        .build();
  }
}
