package it.gov.pagopa.fdrtechsupport.controller.interfaces;

import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.time.LocalDate;

@Path("/psps")
@Consumes("application/json")
@Produces("application/json")
@Tag(name = "PSP", description = "APIs for inspecting PSP usage of flows")
public interface IPSPController {

    @GET
    @Operation(
            operationId = "PSPController_searchFlowByPsp",
            summary = "...",
            description = "...")
    @APIResponses(value = {})
    @Path("/{pspId}")
    Response searchFlowByPsp(
            @PathParam("pspId") @NotNull String pspId,
            @QueryParam("dateFrom") LocalDate dateFrom,
            @QueryParam("dateTo") LocalDate dateTo,
            @QueryParam("flowId") String flowId,
            @QueryParam("organizationId") String organizationId);

    @GET
    @Operation(
            operationId = "PSPController_searchFlowByPspAndIuv",
            summary = "...",
            description = "...")
    @APIResponses(value = {})
    @Path("/{pspId}/iuv/{iuv}")
    Response searchFlowByPspAndIuv(
            @PathParam("pspId") @NotNull String pspId,
            @PathParam("iuv") @NotNull String iuv,
            @QueryParam("dateFrom") LocalDate dateFrom,
            @QueryParam("dateTo") LocalDate dateTo);

    @GET
    @Operation(
            operationId = "PSPController_searchFlowByPspAndIuv",
            summary = "...",
            description = "...")
    @APIResponses(value = {})
    @Path("/{pspId}/iur/{iur}")
    Response searchFlowByPspAndIur(
            @PathParam("pspId") @NotNull String pspId,
            @PathParam("iur") @NotNull String iur,
            @QueryParam("dateFrom") LocalDate dateFrom,
            @QueryParam("dateTo") LocalDate dateTo);
}
