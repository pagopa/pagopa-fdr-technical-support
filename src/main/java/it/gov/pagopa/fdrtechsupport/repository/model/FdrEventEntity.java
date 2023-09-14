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
  //private String httpMethod;
  //private String httpUrl;
  //private String fdrPhisicalDelete;

  private static String dateFilter = " '%s': { '$gte': :from , '$lt': :to } ";
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
    StringBuilder filterBuilder = new StringBuilder(String.format(dateFilter,CREATED));
    pspId.ifPresent(psp->{
      filterBuilder.append(String.format(",'%s': :pspId",PSP));
      params.and("pspId", psp);
    });
    areParametersPresent(flowName, organizationId, params, filterBuilder);
    if(actions.isPresent()){
      filterBuilder.append(String.format(",'%s' : { '$in': [:actions] }",ACTION));
      params.and("actions", actions.get());
    }
    String filter = "{"+filterBuilder.toString()+"}";
    return find(filter,params).project(FdrEventEntity.class);
  }

  public static PanacheQuery<FdrEventEntity> findSpecificWithParams(LocalDate dateFrom, LocalDate dateTo, Optional<String> pspId, Optional<String> iuv, Optional<String> flowName, Optional<String> organizationId) {

    final Parameters params = dateParams(dateFrom, dateTo);
    StringBuilder filterBuilder = new StringBuilder(dateFilter);

    pspId.ifPresent(psp -> {
      filterBuilder.append(String.format(",'%s': :pspId"));
      params.and("pspId", psp);
    });

    iuv.ifPresent(i -> {
      filterBuilder.append(String.format(",'%s': :iuv",IUV));
      params.and("iuv", i);
    });

    areParametersPresent(flowName, organizationId, params, filterBuilder);

    String filter = "{" + filterBuilder + "}";
    return find(filter, params).project(FdrEventEntity.class);
  }

  private static void areParametersPresent(Optional<String> flowName, Optional<String> organizationId, Parameters params, StringBuilder filterBuilder) {

    if (flowName.isPresent()) {
      filterBuilder.append(String.format(",'%s': :flowName",NAME));
      params.and("flowName", flowName.get());
    } else {
      filterBuilder.append(String.format(",'%s': { '$ne' : null }",NAME));
    }

    if (organizationId.isPresent()) {
      filterBuilder.append(String.format(",'%s': :organizationId",ORGANIZATION));
      params.and("organizationId", organizationId.get());
    }
  }

}
