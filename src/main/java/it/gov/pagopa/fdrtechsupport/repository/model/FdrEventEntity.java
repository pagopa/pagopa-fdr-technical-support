package it.gov.pagopa.fdrtechsupport.repository.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.PanacheQuery;
import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.panache.common.Parameters;
import lombok.Data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@MongoEntity(collection = "events")
public class FdrEventEntity extends PanacheMongoEntity {

  private String serviceIdentifier;
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

  private static String dateFilter = " 'created': { '$gte': :from , '$lt': :to } ";
  private static Parameters dateParams(LocalDate dateFrom, LocalDate dateTo){
    return Parameters.with("from", DateTimeFormatter.ISO_DATE.format(dateFrom))
            .and("to", DateTimeFormatter.ISO_DATE.format(dateTo.plusDays(1)));
  }

  public static PanacheQuery<FdrEventEntity> findWithParams(
          LocalDate dateFrom,
          LocalDate dateTo,
          Optional<String> pspId,
          Optional<String> flowName,
          Optional<String> organizationId,
          Optional<List<String>> actions) {
    final Parameters params = dateParams(dateFrom, dateTo);
    StringBuilder filterBuilder = new StringBuilder(dateFilter);
    pspId.ifPresent(psp->{
      filterBuilder.append(",'pspId': :pspId");
      params.and("pspId", psp);
    });
    areParametersPresent(flowName, organizationId, params, filterBuilder);
    if(actions.isPresent()){
      filterBuilder.append(",'flowAction' : { '$in': [:actions] }");
      params.and("actions", actions.get());
    }
    String filter = "{"+filterBuilder.toString()+"}";
    return find(filter,params).project(FdrEventEntity.class);
  }

  public static PanacheQuery<FdrEventEntity> findSpecificWithParams(LocalDate dateFrom, LocalDate dateTo, Optional<String> pspId, Optional<String> iuv, Optional<String> flowName, Optional<String> organizationId) {

    final Parameters params = dateParams(dateFrom, dateTo);
    StringBuilder filterBuilder = new StringBuilder(dateFilter);

    pspId.ifPresent(psp -> {
      filterBuilder.append(",'pspId': :pspId");
      params.and("pspId", psp);
    });

    iuv.ifPresent(i -> {
      filterBuilder.append(",'iuv': :iuv");
      params.and("iuv", i);
    });

    areParametersPresent(flowName, organizationId, params, filterBuilder);

    String filter = "{" + filterBuilder + "}";
    return find(filter, params).project(FdrEventEntity.class);
  }

  private static void areParametersPresent(Optional<String> flowName, Optional<String> organizationId, Parameters params, StringBuilder filterBuilder) {

    if (flowName.isPresent()) {
      filterBuilder.append(",'flowName': :flowName");
      params.and("flowName", flowName.get());
    } else {
      filterBuilder.append(",'flowName': { '$ne' : null }");
    }

    if (organizationId.isPresent()) {
      filterBuilder.append(",'organizationId': :organizationId");
      params.and("organizationId", organizationId.get());
    }
  }

}
