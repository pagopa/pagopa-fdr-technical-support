package it.gov.pagopa.fdrtechsupport.controller.interfaces;

import it.gov.pagopa.fdrtechsupport.controller.model.flow.response.FlowContentResponse;
import it.gov.pagopa.fdrtechsupport.controller.model.report.response.MultipleFlowsOnSingleDateResponse;
import it.gov.pagopa.fdrtechsupport.controller.model.report.response.MultipleFlowsResponse;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import java.time.LocalDate;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/organizations")
@Consumes("application/json")
@Produces("application/json")
@Tag(name = "Organization", description = "APIs for inspecting creditor institution usage of flows")
public interface IOrganizationController {

  @GET
  @Operation(
      operationId = "OrganizationsController_getRevisions",
      summary = "...",
      description = "...")
  @APIResponses(value = {})
  @Path("/{organizationId}/flows/{flowId}")
  MultipleFlowsResponse getRevisions(
      @PathParam("organizationId") @NotNull String organizationId,
      @PathParam("flowId") @NotNull String flowId,
      @QueryParam("dateFrom") LocalDate dateFrom,
      @QueryParam("dateTo") LocalDate dateTo);

  @GET
  @Operation(operationId = "OrganizationsController_getFlow", summary = "...", description = "...")
  @APIResponses(value = {})
  @Path("/{organizationId}/psps/{pspId}/flows/{flowId}/revisions/{revision}")
  FlowContentResponse getFlow(
      @PathParam("organizationId") @NotNull String organizationId,
      @PathParam("flowId") @NotNull String flowId,
      @PathParam("pspId") @NotNull String pspId,
      @PathParam("revision") @NotNull String revision,
      @QueryParam("dateFrom") LocalDate dateFrom,
      @QueryParam("dateTo") LocalDate dateTo,
      @QueryParam("fileType") String fileType);

  @GET
  @Operation(
      operationId = "OrganizationsController_getDownloads",
      summary = "...",
      description = "...")
  @APIResponses(value = {})
  @Path("/{organizationId}/psps/{pspId}/download")
  MultipleFlowsOnSingleDateResponse getDownloads(
      @PathParam("organizationId") @NotNull String organizationId,
      @PathParam("pspId") @NotNull String pspId,
      @QueryParam("date") @NotNull LocalDate date,
      @QueryParam("flowName") String flowName);

  @GET
  @Operation(
      operationId = "OrganizationsController_getUploads",
      summary = "...",
      description = "...")
  @APIResponses(value = {})
  @Path("/{organizationId}/psps/{pspId}/upload")
  MultipleFlowsOnSingleDateResponse getUploads(
      @PathParam("organizationId") @NotNull String organizationId,
      @PathParam("pspId") @NotNull String pspId,
      @QueryParam("date") @NotNull LocalDate date,
      @QueryParam("flowName") String flowName);
}
