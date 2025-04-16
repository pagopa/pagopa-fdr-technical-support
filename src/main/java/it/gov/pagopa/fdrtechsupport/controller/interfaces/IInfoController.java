package it.gov.pagopa.fdrtechsupport.controller.interfaces;

import it.gov.pagopa.fdrtechsupport.controller.model.common.response.InfoResponse;
import it.gov.pagopa.fdrtechsupport.controller.model.error.response.ProblemJson;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/info")
@Tag(name = "Info", description = "Info operations")
public interface IInfoController {

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(
      operationId = "InfoController_healthCheck",
      summary = "Health-check",
      description = "Get health check and deployment-related information")
  @APIResponses(
      value = {
        @APIResponse(
            responseCode = "200",
            description = "Success",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = InfoResponse.class))),
        @APIResponse(
            responseCode = "500",
            description = "Internal Server Error",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = ProblemJson.class),
                    example = "..."))
      })
  InfoResponse healthCheck();
}
