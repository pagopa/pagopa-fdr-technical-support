package it.gov.pagopa.fdrtechsupport.repository.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.PanacheQuery;
import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.panache.common.Parameters;
import lombok.Data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

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
  private Long revision;

  private static String dateFilter = "created >= :from and created <= :to";
  private static Parameters dateParams(LocalDate dateFrom, LocalDate dateTo){
    return Parameters.with("from", DateTimeFormatter.ISO_DATE.format(dateFrom)+"T00")
            .and("to", DateTimeFormatter.ISO_DATE.format(dateTo)+"T23");
  }

  public static PanacheQuery<FdrEventEntity> findByPspId(
          LocalDate dateFrom,
          LocalDate dateTo,
          String pspId,
          Optional<String> flowName) {
    Parameters params = dateParams(dateFrom, dateTo)
            .and("pspId", pspId);
    String filter = dateFilter + " and pspId = :pspId";
    if(flowName.isPresent()){
      filter += " and flowName like :flowName";
      params = params.and("flowName", flowName.get());
    }else{
      filter += " and flowName != :flowName";
      params = params.and("flowName", null);
    }
    return find(filter,params).project(FdrEventEntity.class);
  }

  public static PanacheQuery<FdrEventEntity> findByOrganizationIdAndFlowName(
          LocalDate dateFrom,
          LocalDate dateTo,
          String organizationId,
          String flowName) {
    Parameters params = dateParams(dateFrom, dateTo).and("organizationId", organizationId);
    String filter = dateFilter + " and organizationId = :organizationId";
    filter += " and flowName like :flowName";
    params = params.and("flowName", flowName);
    return find(filter,params).project(FdrEventEntity.class);
  }

}
