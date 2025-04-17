package it.gov.pagopa.fdrtechsupport.controller.interfaces;

import it.gov.pagopa.fdrtechsupport.controller.model.response.FdrFullInfoResponse;
import it.gov.pagopa.fdrtechsupport.controller.model.response.FrResponse;
import it.gov.pagopa.fdrtechsupport.controller.model.response.FrSingleDateResponse;
import it.gov.pagopa.fdrtechsupport.openapi.APIAppErrorMetadata;
import it.gov.pagopa.fdrtechsupport.openapi.APITableMetadata;
import it.gov.pagopa.fdrtechsupport.util.error.enums.AppErrorCodeMessageEnum;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestQuery;

import java.time.LocalDate;

@Path("/organizations")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Organization", description = "APIs for inspecting creditor institution usage of flows")
public interface IOrganizationController {

    @GET
    @Path("/{organizationId}/flows/{flowId}")
    @Operation(
            operationId = "OrganizationsController_getRevisions",
            summary = "This API allow to retrieve all revision of a FdR for a specific CI",
            description = "Retrieves a list of revision of a FdR for a given flow name and Creditor Institution (CI) " +
                    "within a specified date range. If no dates are specified, data from the last 7 days is returned.")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "200",
                    description = "Success",
                    content =
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = FrResponse.class))),
            @APIResponse(ref = "#/components/responses/ErrorResponse400"),
            @APIResponse(ref = "#/components/responses/ErrorResponse404"),
            @APIResponse(ref = "#/components/responses/ErrorResponse500"),
    })
    @APITableMetadata(
            synchronism = APITableMetadata.APISynchronism.SYNC,
            authorization = APITableMetadata.APISecurityMode.NONE,
            authentication = APITableMetadata.APISecurityMode.APIKEY,
            readWriteIntense = APITableMetadata.ReadWrite.READ,
            cacheable = true)
    @APIAppErrorMetadata(
            errors = {
                    AppErrorCodeMessageEnum.DATE_BAD_REQUEST,
                    AppErrorCodeMessageEnum.FLOW_NOT_FOUND
            })
    FrResponse getRevisions(
            @RestPath
            @Parameter(
                    description = "The organization fiscal code, used as a search filter",
                    example = "99999999999",
                    required = true)
            @NotNull String organizationId,
            @RestPath
            @Parameter(
                    description = "The flow name, used as a search filter",
                    example = "2025-01-0188888888888-0001",
                    required = true)
            @NotNull String flowId,
            @RestQuery
            @Parameter(
                    description = "The starting date for retrieving flows",
                    example = "2025-03-10")
            LocalDate dateFrom,
            @RestQuery
            @Parameter(
                    description = "The ending date for retrieving flows",
                    example = "2025-03-10")
            LocalDate dateTo
    );


    @GET
    @Path("/{organizationId}/psps/{pspId}/flows/{flowId}/revisions/{revision}")
    @Operation(
            operationId = "OrganizationsController_getFlow",
            summary = "This API allow to retrieve a revision of a FdR for a specific CI and PSP",
            description = "Retrieves a specific revision of a FdR for a given Creditor Institution (CI), " +
                    "Payment Service Provider (PSP), flow name, and revision number within a specified date range." +
                    " If no dates are specified, data from the last 7 days is returned.")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "200",
                    description = "Success",
                    content =
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = FdrFullInfoResponse.class))),
            @APIResponse(ref = "#/components/responses/ErrorResponse400"),
            @APIResponse(ref = "#/components/responses/ErrorResponse404"),
            @APIResponse(ref = "#/components/responses/ErrorResponse500"),
    })
    @APITableMetadata(
            synchronism = APITableMetadata.APISynchronism.SYNC,
            authorization = APITableMetadata.APISecurityMode.NONE,
            authentication = APITableMetadata.APISecurityMode.APIKEY,
            readWriteIntense = APITableMetadata.ReadWrite.READ,
            cacheable = true)
    @APIAppErrorMetadata(
            errors = {
                    AppErrorCodeMessageEnum.DATE_BAD_REQUEST,
                    AppErrorCodeMessageEnum.INVALID_FILE_TYPE,
                    AppErrorCodeMessageEnum.UNAUTHORIZED_CLIENT,
                    AppErrorCodeMessageEnum.FLOW_NOT_FOUND_CLIENT,
                    AppErrorCodeMessageEnum.ERROR
            })
    FdrFullInfoResponse getFlow(
            @RestPath
            @Parameter(
                    description = "The organization fiscal code, used as a search filter",
                    example = "99999999999",
                    required = true)
            @NotNull String organizationId,
            @RestPath
            @Parameter(
                    description = "The flow name, used as a search filter",
                    example = "2025-01-0188888888888-0001",
                    required = true)
            @NotNull String flowId,
            @RestPath
            @Parameter(
                    description = "The PSP identifier, used as a search filter",
                    example = "88888888888",
                    required = true)
            @NotNull String pspId,
            @RestPath
            @Parameter(
                    description = "The FdR revision, used as a search filter",
                    example = "1",
                    required = true)
            @NotNull String revision,
            @RestQuery
            @Parameter(
                    description = "The starting date for retrieving flows",
                    example = "2025-03-10")
            LocalDate dateFrom,
            @RestQuery
            @Parameter(
                    description = "The ending date for retrieving flows",
                    example = "2025-03-10")
            LocalDate dateTo,
            @RestQuery
            @Parameter(
                    description = "The expected FdR format, based of this parameter the flow is retrieved from FdR-1 (xml) or FdR-3 (json)",
                    example = "xml")
            @DefaultValue("json")
            String fileType
    );

    @GET
    @Path("/{organizationId}/psps/{pspId}/download")
    @Operation(
            operationId = "OrganizationsController_getDownloads",
            summary = "This API allow to retrieve a list of download tentative of FdR for a specific date",
            description = "Retrieves a list of download tentative of FdR for a given Creditor Institution (CI) and " +
                    "Payment Service Provider (PSP) within the specified date. If no date is specified, data from previous day is returned.")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "200",
                    description = "Success",
                    content =
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = FrSingleDateResponse.class))),
            @APIResponse(ref = "#/components/responses/ErrorResponse400"),
            @APIResponse(ref = "#/components/responses/ErrorResponse404"),
            @APIResponse(ref = "#/components/responses/ErrorResponse500")
    })
    @APITableMetadata(
            synchronism = APITableMetadata.APISynchronism.SYNC,
            authorization = APITableMetadata.APISecurityMode.NONE,
            authentication = APITableMetadata.APISecurityMode.APIKEY,
            readWriteIntense = APITableMetadata.ReadWrite.READ,
            cacheable = true)
    @APIAppErrorMetadata(
            errors = {
                    AppErrorCodeMessageEnum.DATE_BAD_REQUEST,
                    AppErrorCodeMessageEnum.FLOW_NOT_FOUND
            })
    FrSingleDateResponse getDownloads(
            @RestPath
            @Parameter(
                    description = "The organization fiscal code, used as a search filter",
                    example = "99999999999",
                    required = true)
            @NotNull String organizationId,
            @RestPath
            @Parameter(
                    description = "The PSP identifier, used as a search filter",
                    example = "88888888888",
                    required = true)
            @NotNull String pspId,
            @RestQuery
            @Parameter(
                    description = "The date for retrieving FdR download tentative",
                    example = "2025-03-10")
            LocalDate date
    );

    @GET
    @Path("/{organizationId}/psps/{pspId}/upload")
    @Operation(
            operationId = "OrganizationsController_getUploads",
            summary = "This API allow to retrieve a list of upload tentative of FdR for a specific date",
            description = "Retrieves a list of upload tentative of FdR for a given Creditor Institution (CI) and " +
                    "Payment Service Provider (PSP) within the specified date. If no date is specified, data from previous day is returned.")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "200",
                    description = "Success",
                    content =
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = FrResponse.class))),
            @APIResponse(ref = "#/components/responses/ErrorResponse400"),
            @APIResponse(ref = "#/components/responses/ErrorResponse404"),
            @APIResponse(ref = "#/components/responses/ErrorResponse500")
    })
    @APITableMetadata(
            synchronism = APITableMetadata.APISynchronism.SYNC,
            authorization = APITableMetadata.APISecurityMode.NONE,
            authentication = APITableMetadata.APISecurityMode.APIKEY,
            readWriteIntense = APITableMetadata.ReadWrite.READ,
            cacheable = true)
    @APIAppErrorMetadata(
            errors = {
                    AppErrorCodeMessageEnum.DATE_BAD_REQUEST,
                    AppErrorCodeMessageEnum.FLOW_NOT_FOUND
            })
    FrResponse getUploads(
            @RestPath
            @Parameter(
                    description = "The organization fiscal code, used as a search filter",
                    example = "99999999999",
                    required = true)
            @NotNull String organizationId,
            @RestPath
            @Parameter(
                    description = "The PSP identifier, used as a search filter",
                    example = "88888888888",
                    required = true)
            @NotNull String pspId,
            @RestQuery
            @Parameter(
                    description = "The date for retrieving FdR upload tentative",
                    example = "2025-03-10")
            LocalDate date
    );
}
