package it.gov.pagopa.fdrtechsupport.repository.model;

import static io.quarkus.mongodb.panache.PanacheMongoEntityBase.find;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.quarkus.mongodb.panache.PanacheMongoEntityBase;
import io.quarkus.mongodb.panache.PanacheQuery;
import io.quarkus.panache.common.Parameters;
import lombok.Data;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReEventEntity {

  public static String ACTION = "fdrAction";
  public static String NAME = "fdr";
  public static String PSP = "pspId";
  public static String IUV = "iuv";
  public static String ORGANIZATION = "organizationId";
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


  public static PanacheQuery<PanacheMongoEntityBase> findByQuery(String query, Parameters parameters) {
    return find(query, parameters.map());
  }
}
