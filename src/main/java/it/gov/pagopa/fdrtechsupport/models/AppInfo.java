package it.gov.pagopa.fdrtechsupport.models;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppInfo {
  private String name;
  private String version;
  private String environment;
}
