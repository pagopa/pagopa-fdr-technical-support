package it.gov.pagopa.fdrtechsupport.clients;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/")
@ClientHeaderParam(name = "${fdr-old.api-key-name}", value = "${fdr-old.api-key-value}")
@RegisterRestClient(configKey = "fdr-old")
public interface FdrOldRestClient {

    @POST
    String nodoChiediFlussoRendicontazione(String body);

}

