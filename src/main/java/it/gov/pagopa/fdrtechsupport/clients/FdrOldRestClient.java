package it.gov.pagopa.fdrtechsupport.clients;

import it.gov.pagopa.fdrtechsupport.clients.model.FdrOldXmlResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/")
@ClientHeaderParam(name = "Ocp-Apim-Subscription-Key", value = "${fdr-old.api-key-value}")
@RegisterRestClient(configKey = "fdr-old")
public interface FdrOldRestClient {

    @GET
    @Path("/internal/organizations/{ec}/flows/{flowName}")
    FdrOldXmlResponse nodoChiediFlussoRendicontazione(@PathParam("ec") String ec, @PathParam("flowName") String flowName);

}

