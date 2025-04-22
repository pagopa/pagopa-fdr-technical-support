package it.gov.pagopa.fdrtechsupport.controller;

import it.gov.pagopa.fdrtechsupport.controller.interfaces.IOrganizationController;
import it.gov.pagopa.fdrtechsupport.controller.model.flow.response.FlowContentResponse;
import it.gov.pagopa.fdrtechsupport.controller.model.report.response.MultipleFlowsOnSingleDateResponse;
import it.gov.pagopa.fdrtechsupport.controller.model.report.response.MultipleFlowsResponse;
import it.gov.pagopa.fdrtechsupport.service.WorkerService;
import it.gov.pagopa.fdrtechsupport.util.constant.AppConstant;
import java.time.LocalDate;

public class OrganizationController implements IOrganizationController {

  private final WorkerService workerService;

  public OrganizationController(WorkerService workerService) {
    this.workerService = workerService;
  }

  public MultipleFlowsResponse getRevisions(
      String organizationId, String flowId, LocalDate dateFrom, LocalDate dateTo) {

    return workerService.getRevisions(organizationId, flowId, dateFrom, dateTo);
  }

  @Override
  public FlowContentResponse getFlow(
      String organizationId,
      String flowId,
      String pspId,
      String revision,
      LocalDate dateFrom,
      LocalDate dateTo,
      String fileType) {

    return workerService.getFlow(
        organizationId, pspId, flowId, revision, dateFrom, dateTo, fileType);
  }

  @Override
  public MultipleFlowsOnSingleDateResponse getDownloads(
      String organizationId, String pspId, LocalDate date, String flowName) {

    return workerService.searchFlowOperations(
        pspId, organizationId, AppConstant.RE_EVENTS_DOWNLOAD_ACTIONS, flowName, date);
  }

  @Override
  public MultipleFlowsOnSingleDateResponse getUploads(
      String organizationId, String pspId, LocalDate date, String flowName) {

    return workerService.searchFlowOperations(
        pspId, organizationId, AppConstant.RE_EVENTS_UPLOAD_ACTIONS, flowName, date);
  }
}
