package it.gov.pagopa.fdrtechsupport.repository.model;

import static io.quarkus.mongodb.panache.PanacheMongoEntityBase.find;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.PanacheMongoEntityBase;
import io.quarkus.mongodb.panache.PanacheQuery;
import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.panache.common.Parameters;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@MongoEntity(collection = "events")
public class ReEventEntity extends PanacheMongoEntity {

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


  public static PanacheQuery<PanacheMongoEntityBase> findByQuery(String query, Parameters parameters) {
    return find(query, parameters.map());
  }
}
