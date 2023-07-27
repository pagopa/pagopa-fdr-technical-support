package it.gov.pagopa.fdrtechsupport.repository.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.PanacheQuery;
import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.panache.common.Parameters;
import lombok.Data;

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
  private String bodyRef;
  private String header;
  private String flowPhisicalDelete;
  private String flowStatus;
  private String revision;

  private static String dateFilter = "PartitionKey >= :from and PartitionKey <= :to";
  private static Parameters dateParams(LocalDate dateFrom, LocalDate dateTo){
    return Parameters.with("from", DateTimeFormatter.ISO_DATE.format(dateFrom)+"T00")
            .and("to", DateTimeFormatter.ISO_DATE.format(dateTo)+"T23");
  }

  public static PanacheQuery<FdrEventEntity> findReByCiAndNN(
          String creditorInstitution,
          String nav,
          LocalDate dateFrom,
          LocalDate dateTo) {
    return find(
            dateFilter +
                    " and idDominio = :idDominio and noticeNumber = :noticeNumber and esito = 'CAMBIO_STATO'"
                    + " and status like 'payment_'",
            dateParams(dateFrom,dateTo)
                    .and("idDominio", creditorInstitution)
                    .and("noticeNumber", nav)
    )
            .project(FdrEventEntity.class);
  }
}
