package it.gov.pagopa.fdrtechsupport.clients;

import io.quarkus.rest.client.reactive.ClientExceptionMapper;
import it.gov.pagopa.fdrtechsupport.clients.model.FdrOldXmlResponse;
import it.gov.pagopa.fdrtechsupport.exceptions.AppErrorCodeMessageEnum;
import it.gov.pagopa.fdrtechsupport.exceptions.AppException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/")
@ClientHeaderParam(name = "Ocp-Apim-Subscription-Key", value = "${fdr-old.api-key-value}")
@RegisterRestClient(configKey = "fdr-old")
public interface FdrOldRestClient {

    @GET
    @Path("/internal/organizations/{ec}/flows/{flowName}")
    FdrOldXmlResponse nodoChiediFlussoRendicontazione(@PathParam("ec") String ec, @PathParam("flowName") String flowName);

    @ClientExceptionMapper
    static RuntimeException toException(Response response) {
        if (response.getStatus() == 500) {
            return new AppException(
                    AppErrorCodeMessageEnum.ERROR
            );
        }
        if (response.getStatus() == 404) {
            return new AppException(
                    AppErrorCodeMessageEnum.FLOW_NOT_FOUND
            );
        }
        return new AppException(
                AppErrorCodeMessageEnum.ERROR
        );
    }
}

