package it.gov.pagopa.fdrtechsupport.resources;

import io.quarkiverse.mockserver.test.MockServerTestResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@QuarkusTestResource(MockServerTestResource.class)
public class GetByPspAndIurTest {
}
