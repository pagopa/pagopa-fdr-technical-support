package it.gov.pagopa.fdrtechsupport.controller.interfaces;

import it.gov.pagopa.fdrtechsupport.controller.model.response.MultipleFlowsResponse;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
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
    MultipleFlowsResponse searchFlowByPsp(
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
    MultipleFlowsResponse searchFlowByPspAndIuv(
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
    MultipleFlowsResponse searchFlowByPspAndIur(
            @PathParam("pspId") @NotNull String pspId,
            @PathParam("iur") @NotNull String iur,
            @QueryParam("dateFrom") LocalDate dateFrom,
            @QueryParam("dateTo") LocalDate dateTo);
}
