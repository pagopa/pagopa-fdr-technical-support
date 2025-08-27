package it.gov.pagopa.fdrtechsupport.controller.model.common.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;
import lombok.extern.jackson.Jacksonized;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Getter
@Builder
@Jacksonized
@JsonPropertyOrder({"name", "version", "environment", "description"})
public class InfoResponse {

  @Schema(
      description =
          "The identificative name of the deployed application, example ="
              + " \"pagopa-fdr-technical-support\"")
  private String name;

  @Schema(description = "The current version of the deployed application, example = \"1.2.3\"")
  private String version;

  @Schema(
      description = "The current environment where the application is deployed, example = \"dev\"")
  private String environment;

  @Schema(
      description =
          "The descriptive information related to the info response, example = \"FDR Technical"
              + " Support\"")
  private String description;
}
