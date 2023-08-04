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
  private Integer revision;

  private static String dateFilter = " 'PartitionKey': { '$gte': :from , '$lt': :to } ";
  private static Parameters dateParams(LocalDate dateFrom, LocalDate dateTo){
    return Parameters.with("from", DateTimeFormatter.ISO_DATE.format(dateFrom)+"T00")
            .and("to", DateTimeFormatter.ISO_DATE.format(dateTo)+"T23");
  }

  public static PanacheQuery<FdrEventEntity> findWithParams(
          LocalDate dateFrom,
          LocalDate dateTo,
          Optional<String> pspId,
          Optional<String> flowName,
          Optional<String> organizationId) {
    final Parameters params = dateParams(dateFrom, dateTo);
    StringBuilder filterBuilder = new StringBuilder(dateFilter);
    pspId.ifPresent(psp->{
      filterBuilder.append(",'pspId': :pspId");
      params.and("pspId", psp);
    });
    if(flowName.isPresent()){
      filterBuilder.append(",'flowName': :flowName");
      params.and("flowName", flowName.get());
    }else{
      filterBuilder.append(",'flowName': { '$ne' : null }");
    }
    if(organizationId.isPresent()){
      filterBuilder.append(",'organizationId': :organizationId");
      params.and("organizationId", organizationId.get());
    }
    String filter = "{"+filterBuilder.toString()+"}";
    return find(filter,params).project(FdrEventEntity.class);
  }

}
