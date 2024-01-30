package it.gov.pagopa.fdrtechsupport.resources;

import io.swagger.v3.oas.annotations.tags.Tag;
import it.gov.pagopa.fdrtechsupport.models.ProblemJson;
import it.gov.pagopa.fdrtechsupport.resources.response.FrResponse;
import it.gov.pagopa.fdrtechsupport.service.WorkerService;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Optional;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

@Path("/psps")
@Produces(value = MediaType.APPLICATION_JSON)
public class PspResource implements Serializable {

  @Inject WorkerService workerService;

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
            responseCode = "401",
            description = "Unauthorized.",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = ProblemJson.class))),
        @APIResponse(
            responseCode = "404",
            description = "Not found.",
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
  @Path("/{pspId}")
  // https://pagopa.atlassian.net/wiki/spaces/PN5/pages/761201348/Design+Review+FdR+API+Assistenza#API-1---Get-all-FdR-by-PSP-%5BFR_01_04%5D
  public Response frO1(
      @PathParam("pspId") @NotNull String pspId,
      @NotNull @QueryParam("dateFrom") LocalDate dateFrom,
      @NotNull @QueryParam("dateTo") LocalDate dateTo,
      @QueryParam("fdr") Optional<String> fdr,
      @QueryParam("organizationId") Optional<String> organizationId) {
    return Response.ok(
            workerService.getFdrByPsp(Optional.of(pspId), fdr, organizationId, dateFrom, dateTo))
        .build();
  }

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
            description = "Bad request.",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = ProblemJson.class))),
        @APIResponse(
            responseCode = "401",
            description = "Unauthorized.",
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
  @Tag(name = "API 2 - Get all FdR by PSP and IUV")
  @Path("/{pspId}/iuv/{iuv}")
  public Response frO2(
      @PathParam("pspId") @NotNull String pspId,
      @PathParam("iuv") @NotNull String iuv,
      @NotNull @QueryParam("dateFrom") LocalDate dateFrom,
      @NotNull @QueryParam("dateTo") LocalDate dateTo) {
    return Response.ok(workerService.getFdrByPspAndIuv(pspId, iuv, dateFrom, dateTo)).build();
  }

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
            description = "Bad request.",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = ProblemJson.class))),
        @APIResponse(
            responseCode = "401",
            description = "Unauthorized.",
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
  @Tag(name = "API 3 - Get all FdR by PSP and IUR")
  @Path("/{pspId}/iur/{iur}")
  public Response frO3(
      @PathParam("pspId") @NotNull String pspId,
      @PathParam("iur") @NotNull String iur,
      @NotNull @QueryParam("dateFrom") LocalDate dateFrom,
      @NotNull @QueryParam("dateTo") LocalDate dateTo) {
    return Response.ok(workerService.getFdrByPspAndIur(pspId, iur, dateFrom, dateTo)).build();
  }
}
