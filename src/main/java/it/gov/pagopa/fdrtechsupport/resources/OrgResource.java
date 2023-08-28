package it.gov.pagopa.fdrtechsupport.resources;

import it.gov.pagopa.fdrtechsupport.models.ProblemJson;
import it.gov.pagopa.fdrtechsupport.resources.response.FrResponse;
import it.gov.pagopa.fdrtechsupport.resources.response.FrSingleDateResponse;
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
import java.util.Arrays;
import java.util.Optional;

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
            responseCode = "404",
            description = "Not Found",
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
  @Path("/{organizationId}/flows/{flowName}")
  //https://pagopa.atlassian.net/wiki/spaces/PN5/pages/761201348/Design+Review+FdR+API+Assistenza#API-4---Get-all-revision-of-a-FdR-by-Organization-and-Flow-Name-%5BFR_02%5D
  public Response getRevisions(
          @PathParam("organizationId") @NotNull String organizationId,
          @PathParam("flowName") @NotNull String flowName,
          @NotNull @QueryParam("dateFrom") LocalDate dateFrom,
          @NotNull @QueryParam("dateTo") LocalDate dateTo
  ) {
    return Response.ok(workerService.getRevisions(organizationId,flowName,dateFrom,dateTo)).build();
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
                            description = "Bad Request",
                            content =
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON,
                                    schema = @Schema(implementation = ProblemJson.class))),
                    @APIResponse(
                            responseCode = "404",
                            description = "Not Found",
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
    @Path("/{organizationId}/psps/{psp}/flows/{flowName}/revisions/{revision}")
    //https://pagopa.atlassian.net/wiki/spaces/PN5/pages/761201348/Design+Review+FdR+API+Assistenza#API-5---Get-a-specific-FdR-by-Organization%2C-Flow-Name%2C-PSP-and-revision
    public Response getFlow(
            @PathParam("organizationId") @NotNull String organizationId,
            @PathParam("flowName") @NotNull String flowName,
            @PathParam("psp") @NotNull String psp,
            @PathParam("revision") @NotNull String revision,
            @NotNull @QueryParam("dateFrom") LocalDate dateFrom,
            @NotNull @QueryParam("dateTo") LocalDate dateTo
    ) {
        return Response.ok(workerService.getFlow(organizationId,psp,flowName,revision,dateFrom,dateTo)).build();
    }

    @GET
    @Path("/{organizationId}/psps/{psp}/download")
    //https://pagopa.atlassian.net/wiki/spaces/PN5/pages/761201348/Design+Review+FdR+API+Assistenza#API-5---Get-a-specific-FdR-by-Organization%2C-Flow-Name%2C-PSP-and-revision
    public Response getDownloads(
            @PathParam("organizationId") @NotNull String organizationId,
            @PathParam("psp") @NotNull String psp,
            @NotNull @QueryParam("date") LocalDate date
    ) {
        FrResponse fdrActions = workerService.getFdrActions(psp,
                Optional.empty(),
                Optional.of(organizationId),
                Optional.of(Arrays.asList("nodoChiediFlussoRendicontazione", "GET_FDR", "GET_FDR_PAYMENT")),
                date, date);
        return Response.ok(FrSingleDateResponse.builder().data(fdrActions.getData()).date(fdrActions.getDateFrom()).build()).build();
    }

    @GET
    @Path("/{organizationId}/psps/{psp}/upload")
    //https://pagopa.atlassian.net/wiki/spaces/PN5/pages/761201348/Design+Review+FdR+API+Assistenza#API-7---Get-list-of-upload-tentative-of-FdRs-%5BFR_01_03%5D
    public Response getUploads(
            @PathParam("organizationId") @NotNull String organizationId,
            @PathParam("psp") @NotNull String psp,
            @NotNull @QueryParam("date") LocalDate date
    ) {
        return Response.ok(workerService.getFdrActions(psp,
                Optional.empty(),
                Optional.of(organizationId),
                Optional.of(Arrays.asList("nodoInviaFlussoRendicontazione","PUBLISH","ADD_PAYMENT","CREATE_FLOW","DELETE_FLOW","DELETE_PAYMENT")),
                date,date)).build();
    }

}
