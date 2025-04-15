package it.gov.pagopa.fdrtechsupport.client;

import io.quarkiverse.openapi.generator.annotations.GeneratedClass;
import io.quarkus.rest.client.reactive.ClientExceptionMapper;
import it.gov.pagopa.fdrtechsupport.util.error.enums.AppErrorCodeMessageEnum;
import it.gov.pagopa.fdrtechsupport.util.error.exception.AppException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.openapi.quarkus.api_fdr_nodo_json.model.GetXmlRendicontazioneResponse;

@Path("/")
@ClientHeaderParam(name = "Ocp-Apim-Subscription-Key", value = "${fdr-old.api-key-value}")
@RegisterRestClient(configKey = "fdr-old")
@GeneratedClass(value = "api_fdr_nodo.json")
public interface FdrOldRestClient {

  @ClientExceptionMapper
  static RuntimeException toException(Response response) {
    switch (response.getStatus()) {
      case 500:
        return new AppException(AppErrorCodeMessageEnum.ERROR);
      case 404:
        return new AppException(AppErrorCodeMessageEnum.FLOW_NOT_FOUND_CLIENT);
      case 401:
        return new AppException(AppErrorCodeMessageEnum.UNAUTHORIZED_CLIENT);
      default:
        return new AppException(AppErrorCodeMessageEnum.ERROR);
    }
  }

  @GET
  @Path("/internal/organizations/{ec}/fdrs/{flowName}")
  GetXmlRendicontazioneResponse nodoChiediFlussoRendicontazione(
      @PathParam("ec") String ec, @PathParam("flowName") String flowName);
}
