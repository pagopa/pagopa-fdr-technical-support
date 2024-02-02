package it.gov.pagopa.fdrtechsupport.repository.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FdrEventEntity {

  public static String ACTION = "fdrAction";
  public static String NAME = "fdr";
  public static String PSP = "pspId";
  public static String IUV = "iuv";
  public static String ORGANIZATION = "organizationId";
  public static String PARTITIONKEY = "PartitionKey";
  public static String CREATED = "created";

  private String fdr;
  private String fdrAction;
  private String serviceIdentifier;
  private String created;
  private String sessionId;
  private String eventType;
  private String pspId;
  private String organizationId;
  private String fdrStatus;
  private Integer revision;
  private String httpType;
  // private String httpMethod;
  // private String httpUrl;
  // private String fdrPhisicalDelete;

  private static String excludeInternals = ",'eventType' : { '$ne' : 'INTERNAL' }";
  private static String dateFilter = " '%s': { '$gte': :from , '$lt': :to } ";
}
