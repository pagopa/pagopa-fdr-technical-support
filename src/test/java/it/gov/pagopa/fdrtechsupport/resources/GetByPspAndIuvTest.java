package it.gov.pagopa.fdrtechsupport.resources;

import io.quarkiverse.mockserver.test.MockServerTestResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import it.gov.pagopa.fdrtechsupport.models.FdrBaseInfo;
import it.gov.pagopa.fdrtechsupport.resources.response.FrResponse;

import it.gov.pagopa.fdrtechsupport.util.AzuriteResource;
import it.gov.pagopa.fdrtechsupport.util.MongoResource;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

@QuarkusTest
@QuarkusTestResource(MockServerTestResource.class)
@QuarkusTestResource(AzuriteResource.class)
@QuarkusTestResource(MongoResource.class)
public class GetByPspAndIuvTest {

    public static final String url = "/psps/%s/iuv/%s";

    @Test
    void testGetFdrByPspAndIuv() {

        FrResponse res =
                given()
                        .param("dateFrom", "2022-07-27")
                        .param("dateTo", "2022-07-27")
                        .when()
                        .get(url.formatted(25, 1))
                        .then()
                        .statusCode(200)
                        .extract()
                        .as(new TypeRef<>() {
                        });

        List<FdrBaseInfo> data = res.getData();
        assertThat(data.size(), greaterThan(0));
        assertThat(res.getDateFrom().toString(), equalTo("2022-07-27"));
        assertThat(res.getDateTo().toString(), equalTo("2022-07-27"));
        assertThat(data.get(0).getFdr(), equalTo("string"));
        assertThat(data.get(0).getOrganizationId(), equalTo("string"));
        assertThat(data.get(0).getCreated(), equalTo("2022-07-27T16:15:50Z"));
    }

    @Test
    void testGetFdrByPspAndIuvMalformedError() {

                given()
                        .param("dateFrom", "27-07-2022")
                        .param("dateTo", "27-07-2022")
                        .when()
                        .get(url.formatted(25, 1))
                        .then()
                        .statusCode(equalTo(404));
    }

    @Test
    void testGetFdrByPspAndIuvEmptyParamError() {

        given()
                .param("dateFrom", "")
                .param("dateTo", "")
                .when()
                .get(url.formatted(25, 1))
                .then()
                .statusCode(equalTo(404));
    }

    @Test
    void testGetFdrByPspAndIurReverseDate() {

        given()
                .param("dateFrom", "27-07-2022")
                .param("dateTo", "27-05-2022")
                .when()
                .get(url.formatted(25, 1))
                .then()
                .statusCode(equalTo(404));
    }
}