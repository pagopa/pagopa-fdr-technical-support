package it.gov.pagopa.fdrtechsupport.repository.model;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.PanacheMongoEntityBase;
import io.quarkus.mongodb.panache.PanacheQuery;
import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.panache.common.Parameters;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@MongoEntity(collection = "fdr1-metadata")
public class FdR1MetadataEntity extends PanacheMongoEntity {

  private String flowId;

  private String psp;

  private String brokerPsp;

  private String channel;

  private String creditorInstitution;

  private String flowDate;

  private String pspCreditorInstitution;

  private BlobRefEntity blobBodyRef;

  public static PanacheQuery<PanacheMongoEntityBase> findByQuery(
      String query, Parameters parameters) {
    return find(query, parameters.map());
  }
}
