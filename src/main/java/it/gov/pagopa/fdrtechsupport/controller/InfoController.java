package it.gov.pagopa.fdrtechsupport.controllers;


import it.gov.pagopa.fdrtechsupport.controllers.interfaces.IInfoController;
import it.gov.pagopa.fdrtechsupport.controllers.response.InfoResponse;
import it.gov.pagopa.fdrtechsupport.service.HealthCheckService;

public class InfoController implements IInfoController {


  private final HealthCheckService healthCheckService;

  public InfoController(HealthCheckService healthCheckService) {
    this.healthCheckService = healthCheckService;
  }

  @Override
  public InfoResponse healthCheck() {
    return healthCheckService.getHealthInfo();
  }
}
