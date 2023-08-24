//package it.gov.pagopa.fdrtechsupport.resources;
//
//import com.azure.cosmos.CosmosClient;
//import com.azure.cosmos.CosmosClientBuilder;
//import com.azure.cosmos.models.CosmosItemRequestOptions;
//import com.azure.data.tables.TableClient;
//import com.azure.data.tables.TableServiceClient;
//import com.azure.data.tables.TableServiceClientBuilder;
//import io.quarkiverse.mockserver.test.MockServerTestResource;
//import io.quarkus.test.common.QuarkusTestResource;
//import io.quarkus.test.junit.QuarkusTest;
//import io.restassured.RestAssured;
//import io.restassured.common.mapper.TypeRef;
//import io.restassured.filter.log.RequestLoggingFilter;
//import io.restassured.filter.log.ResponseLoggingFilter;
//import it.gov.pagopa.fdrtechsupport.repository.model.FdrEventEntity;
//import it.gov.pagopa.fdrtechsupport.resources.response.FrResponse;
//import it.gov.pagopa.fdrtechsupport.resources.response.TransactionResponse;
//import it.gov.pagopa.fdrtechsupport.util.AppConstantTestHelper;
//import it.gov.pagopa.fdrtechsupport.util.AzuriteResource;
//import it.gov.pagopa.fdrtechsupport.util.CosmosResource;
//import it.gov.pagopa.fdrtechsupport.util.MongoResource;
//import it.gov.pagopa.fdrtechsupport.util.Util;
//import lombok.SneakyThrows;
//import org.eclipse.microprofile.config.inject.ConfigProperty;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//
//import java.time.Instant;
//import java.time.LocalDate;
//
//import static io.restassured.RestAssured.given;
//import static it.gov.pagopa.fdrtechsupport.util.AppConstantTestHelper.*;
//import static org.hamcrest.Matchers.equalTo;
//import static org.hamcrest.Matchers.greaterThan;
//
//import static io.restassured.RestAssured.given;
//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.hamcrest.Matchers.equalTo;
//import static org.hamcrest.Matchers.greaterThan;
//
//@QuarkusTest
//@QuarkusTestResource(MockServerTestResource.class)
//@QuarkusTestResource(AzuriteResource.class)
//@QuarkusTestResource(MongoResource.class)
//class PspTest {
//
//  @ConfigProperty(name = "re-table-storage.connection-string")
//  String connString;
//
//  @ConfigProperty(name = "biz.endpoint")
//  String bizendpoint;
//
//  @ConfigProperty(name = "biz.key")
//  String bizkey;
//
//  private TableClient tableClient;
//
//  private TableClient getTableClient() {
//    if (tableClient == null) {
//      TableServiceClient tableServiceClient =
//          new TableServiceClientBuilder().connectionString(connString).buildClient();
//      tableServiceClient.createTableIfNotExists("events");
//      tableClient = tableServiceClient.getTableClient("events");
//    }
//    return tableClient;
//  }
//
//  @SneakyThrows
//  @Test
//  @DisplayName("sp03 by ci and nn with positive")
//  void test1() {
//    String noticeNumber = String.valueOf(Instant.now().toEpochMilli());
//    String url = SP03_NN.formatted(PA_CODE, noticeNumber);
//
//    getTableClient().createEntity(AppConstantTestHelper.newRe(PA_CODE, noticeNumber, null));
//
//    FdrEventEntity fdrEventEntity = new FdrEventEntity();
//    fdrEventEntity
//    .persist();
//
//
//    FrResponse res =
//        given()
//            .param("dateFrom", Util.format(LocalDate.now()))
//            .param("dateTo", Util.format(LocalDate.now()))
//            .when()
//            .get(url)
//            .then()
//            .statusCode(200)
//            .extract()
//            .as(new TypeRef<FrResponse>() {});
//    assertThat(res.getPayments().size(), greaterThan(0));
//    PaymentInfo o = (PaymentInfo) res.getPayments().get(0);
//    assertThat(o.getNoticeNumber(), equalTo(noticeNumber));
//    assertThat(o.getOrganizationFiscalCode(), equalTo(PA_CODE));
//    assertThat(o.getOutcome(), equalTo(AppConstantTestHelper.outcomeOK));
//    assertThat(o.getPspId(), equalTo("pspTest"));
//    assertThat(o.getChannelId(), equalTo("canaleTest"));
//    assertThat(o.getBrokerPspId(), equalTo("intTest"));
//  }
//
//}
