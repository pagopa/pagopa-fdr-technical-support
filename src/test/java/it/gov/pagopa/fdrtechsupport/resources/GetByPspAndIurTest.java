package it.gov.pagopa.fdrtechsupport.resources;

import io.quarkiverse.mockserver.test.MockServerTestResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import it.gov.pagopa.fdrtechsupport.models.FdrBaseInfo;
import it.gov.pagopa.fdrtechsupport.resources.response.FrResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

@QuarkusTest
@QuarkusTestResource(MockServerTestResource.class)
public class GetByPspAndIurTest {

    public static final String url = "/psps/%s/iur/%s";
    @Test
    void testGetFdrByPspAndIur() {

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
        assertThat(data.get(0).getFlowName(), equalTo("string4"));
        assertThat(data.get(0).getOrganizationId(), equalTo("string4"));
        assertThat(data.get(0).getCreated(), equalTo("2022-07-27T10:13:16Z"));
    }

    @Test
    void testGetFdrByPspAndIurMalformedError() {

        given()
                .param("dateFrom", "27-07-2022")
                .param("dateTo", "27-07-2022")
                .when()
                .get(url.formatted(25, 1))
                .then()
                .statusCode(equalTo(404));
    }

    @Test
    void testGetFdrByPspAndIurEmptyParamError() {

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
