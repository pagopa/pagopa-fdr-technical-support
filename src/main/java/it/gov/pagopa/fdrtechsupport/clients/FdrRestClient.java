package it.gov.pagopa.fdrtechsupport.clients;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/")
@ClientHeaderParam(name = "${fdr.api-key-name}", value = "${fdr.api-key-value}")
@RegisterRestClient(configKey = "fdr")
public interface FdrRestClient {

    @GET
    @Path("/organizations/{ec}/flows/{fdr}/psps/{psp}")
    String getFlow(@PathParam("ec") String ec, @PathParam("psp") String psp);

}

