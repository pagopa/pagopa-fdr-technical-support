package it.gov.pagopa.fdrtechsupport.repository.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.PanacheQuery;
import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.panache.common.Parameters;
import lombok.Data;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@MongoEntity(collection = "events")
public class FdrEventEntity extends PanacheMongoEntity {

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
//  private String bodyRef;
//  private String header;
  private String flowPhisicalDelete;
  private String flowStatus;
//  private String revision;

  private static String dateFilter = "created >= :from and created <= :to";
  private static Parameters dateParams(LocalDate dateFrom, LocalDate dateTo){
    return Parameters.with("from", DateTimeFormatter.ISO_DATE.format(dateFrom)+"T00")
            .and("to", DateTimeFormatter.ISO_DATE.format(dateTo)+"T23");
  }

  public static PanacheQuery<FdrEventEntity> findByPspId(
          LocalDate dateFrom,
          LocalDate dateTo,
          String pspId) {
    return find(
            dateFilter +
                    " and flowName != :flowName " +
                    " and pspId = :pspId",
            dateParams(dateFrom,dateTo)
                    .and("pspId", pspId)
                    .and("flowName", null)
    ).project(FdrEventEntity.class);
  }
}
