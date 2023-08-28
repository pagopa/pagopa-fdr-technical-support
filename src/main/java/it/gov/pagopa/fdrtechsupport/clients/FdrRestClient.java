package it.gov.pagopa.fdrtechsupport.clients;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/")
@ClientHeaderParam(name = "Ocp-Apim-Subscription-Key", value = "${fdr.api-key-value}")
@RegisterRestClient(configKey = "fdr")
public interface FdrRestClient {

    @GET
    @Path("/organizations/{ec}/flows/{flowName}")
    String getFlow(@PathParam("ec") String ec, @PathParam("flowName") String flowName);

}

