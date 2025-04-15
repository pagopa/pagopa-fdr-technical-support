package it.gov.pagopa.fdrtechsupport.controller.interfaces;

import it.gov.pagopa.fdrtechsupport.controller.model.response.FrResponse;
import it.gov.pagopa.fdrtechsupport.models.ProblemJson;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.time.LocalDate;

@Path("/organizations")
@Consumes("application/json")
@Produces("application/json")
@Tag(name = "Organization", description = "APIs for inspecting creditor institution use of flows")
public interface IOrganizationController {

    @GET
    @Operation(
            operationId = "OrganizationsController_getRevisions",
            summary = "...",
            description = "...")
    @APIResponses(value = {})
    @Path("/{organizationId}/flows/{flowId}")
    Response getRevisions(
            @PathParam("organizationId") @NotNull String organizationId,
            @PathParam("flowId") @NotNull String flowId,
            @QueryParam("dateFrom") LocalDate dateFrom,
            @QueryParam("dateTo") LocalDate dateTo);

    @GET
    @Operation(
            operationId = "OrganizationsController_getFlow",
            summary = "...",
            description = "...")
    @APIResponses(value = {})
    @Path("/{organizationId}/psps/{pspId}/flows/{flowId}/revisions/{revision}")
    Response getFlow(
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
    Response getDownloads(
            @PathParam("organizationId") @NotNull String organizationId,
            @PathParam("pspId") @NotNull String pspId,
            @QueryParam("date") LocalDate date);

    @GET
    @Operation(
            operationId = "OrganizationsController_getUploads",
            summary = "...",
            description = "...")
    @APIResponses(value = {})
    @Path("/{organizationId}/psps/{pspId}/upload")
    Response getUploads(
            @PathParam("organizationId") @NotNull String organizationId,
            @PathParam("pspId") @NotNull String pspId,
            @QueryParam("date") LocalDate date);
}
