package it.gov.pagopa.fdrtechsupport.controllers.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import lombok.*;
import lombok.extern.jackson.Jacksonized;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Getter
@Builder
@Jacksonized
@JsonPropertyOrder({"name", "version", "environment", "description"})
public class InfoResponse {

  @Schema(
          example = "pagopa-fdr-technical-support",
          description = "The identificative name of the deployed application")
  private String name;

  @Schema(example = "1.2.3", description = "The current version of the deployed application")
  private String version;

  @Schema(
          example = "dev",
          description = "The current environment where the application is deployed")
  private String environment;

  @Schema(
          example = "FDR Technical Support",
          description = "The descriptive information related to the info response")
  private String description;
}
