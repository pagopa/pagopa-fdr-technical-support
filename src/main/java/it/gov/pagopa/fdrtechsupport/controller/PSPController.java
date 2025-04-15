package it.gov.pagopa.fdrtechsupport.controller;

import it.gov.pagopa.fdrtechsupport.controller.interfaces.IPSPController;
import it.gov.pagopa.fdrtechsupport.controller.model.response.MultipleFlowsResponse;
import it.gov.pagopa.fdrtechsupport.service.WorkerService;

import java.time.LocalDate;
import java.util.Optional;

public class PSPController implements IPSPController {

  private final WorkerService workerService;

  public PSPController(WorkerService workerService) {
    this.workerService = workerService;
  }

  @Override
  public MultipleFlowsResponse searchFlowByPsp(String pspId, LocalDate dateFrom, LocalDate dateTo, String flowId, String organizationId) {

    return workerService.getFdrByPsp(Optional.of(pspId), Optional.of(flowId), Optional.of(organizationId), dateFrom, dateTo);
  }

  @Override
  public MultipleFlowsResponse searchFlowByPspAndIuv(String pspId, String iuv, LocalDate dateFrom, LocalDate dateTo) {

    return workerService.getFdrByPspAndIuv(pspId, iuv, dateFrom, dateTo);
  }

  @Override
  public MultipleFlowsResponse searchFlowByPspAndIur(String pspId, String iur, LocalDate dateFrom, LocalDate dateTo) {

    return workerService.getFdrByPspAndIur(pspId, iur, dateFrom, dateTo);
  }
}
