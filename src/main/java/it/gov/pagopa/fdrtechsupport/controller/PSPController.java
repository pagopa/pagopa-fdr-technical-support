package it.gov.pagopa.fdrtechsupport.controller;

import it.gov.pagopa.fdrtechsupport.controller.interfaces.IPSPController;
import it.gov.pagopa.fdrtechsupport.service.WorkerService;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

import java.time.LocalDate;
import java.util.Optional;

public class PSPController implements IPSPController {

  private final WorkerService workerService;

  public PSPController(WorkerService workerService) {
    this.workerService = workerService;
  }

  @Override
  public Response searchFlowByPsp(String pspId, LocalDate dateFrom, LocalDate dateTo, String flowId, String organizationId) {

    return Response.ok(
            workerService.getFdrByPsp(Optional.of(pspId), flowId, organizationId, dateFrom, dateTo))
        .build();
  }

  @Override
  public Response searchFlowByPspAndIuv(String pspId, String iuv, LocalDate dateFrom, LocalDate dateTo) {
    return Response.ok(workerService.getFdrByPspAndIuv(pspId, iuv, dateFrom, dateTo)).build();
  }

  @Override
  public Response searchFlowByPspAndIur(String pspId, String iur, LocalDate dateFrom, LocalDate dateTo) {
    return Response.ok(workerService.getFdrByPspAndIur(pspId, iur, dateFrom, dateTo)).build();
  }
}
