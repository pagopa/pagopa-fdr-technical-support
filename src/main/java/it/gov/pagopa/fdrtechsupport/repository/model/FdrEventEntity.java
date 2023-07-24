package it.gov.pagopa.fdrtechsupport.repository.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FdrEventEntity {

  private String appVersion;
  private String created;
  private String sessionId;
  private String eventType;
  private String flowName;
  private String pspId;
  private String organizationId;
  private String flowAction;
  private String httpType;
  private String httpMethod;
  private String httpUrl;
  private String bodyRef;
  private String header;
  private String flowPhisicalDelete;
  private String flowStatus;
  private String revision;
}
