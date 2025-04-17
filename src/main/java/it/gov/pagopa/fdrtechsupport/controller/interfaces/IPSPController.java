package it.gov.pagopa.fdrtechsupport.controller.interfaces;

import it.gov.pagopa.fdrtechsupport.controller.model.response.FrResponse;
import it.gov.pagopa.fdrtechsupport.openapi.APIAppErrorMetadata;
import it.gov.pagopa.fdrtechsupport.openapi.APITableMetadata;
import it.gov.pagopa.fdrtechsupport.openapi.APITableMetadata.APISecurityMode;
import it.gov.pagopa.fdrtechsupport.openapi.APITableMetadata.APISynchronism;
import it.gov.pagopa.fdrtechsupport.openapi.APITableMetadata.ReadWrite;
import it.gov.pagopa.fdrtechsupport.util.error.enums.AppErrorCodeMessageEnum;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.time.LocalDate;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestQuery;

@Path("/psps")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "PSP", description = "APIs for inspecting PSP usage of flows")
public interface IPSPController {

  @GET
  @Path("/{pspId}")
  @Operation(
      operationId = "IPSPController_searchFlowByPsp",
      summary = "This API allow to retrieve a list of FdR for a specific PSP",
      description =
          "Retrieves a list of FdR for a given Payment Service Provider (PSP) within a specified date range, "
              + " optionally filtered by flow name. If no dates are specified, data from the last 7 days is returned.")
  @APIResponses(
      value = {
        @APIResponse(
            responseCode = "200",
            description = "Success",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = FrResponse.class))),
        @APIResponse(ref = "#/components/responses/ErrorResponse400"),
        @APIResponse(ref = "#/components/responses/ErrorResponse500"),
      })
  @APITableMetadata(
      synchronism = APISynchronism.SYNC,
      authorization = APISecurityMode.NONE,
      authentication = APISecurityMode.APIKEY,
      readWriteIntense = ReadWrite.READ,
      cacheable = true)
  @APIAppErrorMetadata(errors = {AppErrorCodeMessageEnum.DATE_BAD_REQUEST})
  FrResponse searchFlowByPsp(
      @RestPath
          @Parameter(
              description = "The PSP identifier, used as a search filter",
              example = "88888888888",
              required = true)
          @NotNull
          String pspId,
      @RestQuery
          @Parameter(description = "The starting date for retrieving flows", example = "2025-03-10")
          LocalDate dateFrom,
      @RestQuery
          @Parameter(description = "The ending date for retrieving flows", example = "2025-03-10")
          LocalDate dateTo,
      @RestQuery
          @Parameter(
              description = "The flow name, used as a search filter",
              example = "2025-01-0188888888888-0001")
          String flowId,
      @RestQuery
          @Parameter(
              description = "The organization fiscal code, used as a search filter",
              example = "99999999999")
          String organizationId);

  @GET
  @Path("/{pspId}/iuv/{iuv}")
  @Operation(
      operationId = "IPSPController_searchFlowByPspAndIuv",
      summary = "This API allow to retrieve a list of FdR for a specific PSP and IUV",
      description =
          "Retrieves a list of FdR for a given Payment Service Provider (PSP) and IUV within a specified date range."
              + " If no dates are specified, data from the last 7 days is returned.")
  @APIResponses(
      value = {
        @APIResponse(
            responseCode = "200",
            description = "Success",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = FrResponse.class))),
        @APIResponse(ref = "#/components/responses/ErrorResponse400"),
        @APIResponse(ref = "#/components/responses/ErrorResponse500"),
      })
  @APITableMetadata(
      synchronism = APISynchronism.SYNC,
      authorization = APISecurityMode.NONE,
      authentication = APISecurityMode.APIKEY,
      readWriteIntense = ReadWrite.READ,
      cacheable = true)
  @APIAppErrorMetadata(errors = {AppErrorCodeMessageEnum.DATE_BAD_REQUEST})
  FrResponse searchFlowByPspAndIuv(
      @RestPath
          @Parameter(
              description = "The PSP identifier, used as a search filter",
              example = "88888888888",
              required = true)
          @NotNull
          String pspId,
      @RestPath
          @Parameter(
              description = "The payment's IUV code, used as a search filter",
              example = "17854456582215")
          @Pattern(regexp = "^(.{1,35})$")
          @NotNull
          String iuv,
      @RestQuery
          @Parameter(description = "The starting date for retrieving flows", example = "2025-03-10")
          LocalDate dateFrom,
      @RestQuery
          @Parameter(description = "The ending date for retrieving flows", example = "2025-03-10")
          LocalDate dateTo);

  @GET
  @Path("/{pspId}/iur/{iur}")
  @Operation(
      operationId = "IPSPController_searchFlowByPspAndIur",
      summary = "This API allow to retrieve a list of FdR for a specific PSP and IUV",
      description =
          "Retrieves a list of FdR for a given Payment Service Provider (PSP) and IUV within a specified date range."
              + " If no dates are specified, data from the last 7 days is returned.")
  @APIResponses(
      value = {
        @APIResponse(
            responseCode = "200",
            description = "Success",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = FrResponse.class))),
        @APIResponse(ref = "#/components/responses/ErrorResponse400"),
        @APIResponse(ref = "#/components/responses/ErrorResponse500"),
      })
  @APITableMetadata(
      synchronism = APISynchronism.SYNC,
      authorization = APISecurityMode.NONE,
      authentication = APISecurityMode.APIKEY,
      readWriteIntense = ReadWrite.READ,
      cacheable = true)
  @APIAppErrorMetadata(errors = {AppErrorCodeMessageEnum.DATE_BAD_REQUEST})
  FrResponse searchFlowByPspAndIur(
      @RestPath
          @Parameter(
              description = "The PSP identifier, used as a search filter",
              example = "88888888888",
              required = true)
          @NotNull
          String pspId,
      @RestPath
          @Pattern(regexp = "^(.{1,35})$")
          @Parameter(
              description = "The payment's IUR code, used as a search filter",
              example = "17854456582215")
          @NotNull
          String iur,
      @RestQuery
          @Parameter(description = "The starting date for retrieving flows", example = "2025-03-10")
          LocalDate dateFrom,
      @RestQuery
          @Parameter(description = "The ending date for retrieving flows", example = "2025-03-10")
          LocalDate dateTo);
}
