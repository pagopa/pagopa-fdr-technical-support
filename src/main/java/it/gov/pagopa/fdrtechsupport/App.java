package it.gov.pagopa.fdrtechsupport;

import it.gov.pagopa.fdrtechsupport.models.ProblemJson;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Components;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeIn;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.servers.Server;
import org.eclipse.microprofile.openapi.annotations.servers.ServerVariable;


@OpenAPIDefinition(
        components =
        @Components(
                securitySchemes = {
                        @SecurityScheme(
                                securitySchemeName = "ApiKey",
                                apiKeyName = "Ocp-Apim-Subscription-Key",
                                in = SecuritySchemeIn.HEADER,
                                type = SecuritySchemeType.APIKEY)
                },
                responses = {
                        @APIResponse(
                                name = "ErrorResponse500",
                                responseCode = "500",
                                description = "Internal Server Error",
                                content =
                                @Content(
                                        mediaType = MediaType.APPLICATION_JSON,
                                        schema = @Schema(implementation = ProblemJson.class),
                                        example = """
                                                  {
                                                  "errorId": "50905466-1881-457b-b42f-fb7b2bfb1610",
                                                  "httpStatusCode": 500,
                                                  "httpStatusDescription": "Internal Server Error",
                                                  "appErrorCode": "FDR-0500",
                                                  "errors": [
                                                    {
                                                      "message": "An unexpected error has occurred. Please contact support."
                                                    }
                                                  ]
                                                }\
                                                """)),
                        @APIResponse(
                                name = "ErrorResponse400",
                                responseCode = "400",
                                description = "Default app exception for status 400",
                                content =
                                @Content(
                                        mediaType = MediaType.APPLICATION_JSON,
                                        schema = @Schema(implementation = ProblemJson.class),
                                        example = """
                                                {
                                                  "httpStatusCode": 400,
                                                  "httpStatusDescription": "Bad Request",
                                                  "appErrorCode": "FDR-XXXX",
                                                  "errors": [
                                                    {
                                                      "path": "<detail.path.if-exist>",
                                                      "message": "<detail.message>"
                                                    }
                                                  ]
                                                }\
                                                """)),
                        @APIResponse(
                                name = "ErrorResponse404",
                                responseCode = "404",
                                description = "Default app exception for status 404",
                                content =
                                @Content(
                                        mediaType = MediaType.APPLICATION_JSON,
                                        schema = @Schema(implementation = ProblemJson.class),
                                        example = """
                                                {
                                                  "httpStatusCode": 404,
                                                  "httpStatusDescription": "Not Found",
                                                  "appErrorCode": "FDR-XXXX",
                                                  "errors": [
                                                    {
                                                      "message": "<detail.message>"
                                                    }
                                                  ]
                                                }\
                                                """
                                ))
                }),
        info = @Info(
                title = "Fdr technical support - Api",
                version = "",
                description = "placeholder-for-replace"),
        servers = {
                @Server(url = "http://localhost:8080", description = "Localhost base URL"),
                @Server(url = "https://{host}/technical-support/fdr/api/v1", description = "Base URL",
                        variables = {
                                @ServerVariable(
                                        name = "host",
                                        description = "Environment host",
                                        enumeration = {"api.dev.platform.pagopa.it", "api.uat.platform.pagopa.it", "api.platform.pagopa.it"},
                                        defaultValue = "api.dev.platform.pagopa.it")
                        })
        }
)
public class App extends Application {
}
