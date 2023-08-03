package it.gov.pagopa.fdrtechsupport.resources;

import it.gov.pagopa.fdrtechsupport.models.ProblemJson;
import it.gov.pagopa.fdrtechsupport.resources.response.FrResponse;
import it.gov.pagopa.fdrtechsupport.service.WorkerService;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import java.io.Serializable;
import java.time.LocalDate;

@Path("/organizations")
@Produces(value = MediaType.APPLICATION_JSON)
public class OrgResource implements Serializable {

  @Inject
  WorkerService workerService;

  @APIResponses(
      value = {
        @APIResponse(
            responseCode = "200",
            description = "OK",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = FrResponse.class))),
        @APIResponse(
            responseCode = "400",
            description = "Bad Request",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = ProblemJson.class))),
        @APIResponse(
            responseCode = "500",
            description = "Service unavailable.",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = ProblemJson.class)))
      })
  @GET
  @Path("/{organizationId}/flowName/{flowName}")
  public Response frO1(@PathParam("organizationId") @NotNull String organizationId,
                       @PathParam("flowName") @NotNull String flowName,
                       @NotNull @QueryParam("dateFrom") LocalDate dateFrom,
                       @NotNull @QueryParam("dateTo") LocalDate dateTo
                       ) {
    return Response.ok(workerService.getFdr04(organizationId,flowName,dateFrom,dateTo)).build();
  }

}
