package it.gov.pagopa.fdrtechsupport.clients;

import io.quarkiverse.openapi.generator.annotations.GeneratedClass;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.openapi.quarkus.api_fdr_json.model.FdrByPspAndIuvResponse;

import java.time.LocalDateTime;

@Path("/")
@ClientHeaderParam(name = "Ocp-Apim-Subscription-Key", value = "${fdr.api-key-value}")
@RegisterRestClient(configKey = "fdr")
@GeneratedClass(value = "api_fdr.json", tag = "fdr_replies")
public interface FdrRestClient {

    @GET
    @Path("/organizations/{ec}/flows/{flowName}")
    String getFlow(@PathParam("ec") String ec, @PathParam("flowName") String flowName);

    @GET
    @Path("/psps/{pspId}/iuv/{iuv}")
    FdrByPspAndIuvResponse getFlowByPspAndIuv(@PathParam("pspId") String pspId, @PathParam("iuv") String iuv,
                                              @QueryParam("pageNumber") Integer pageNumber,
                                              @QueryParam("dateFrom") LocalDateTime dateFrom,
                                              @QueryParam("dateTo") LocalDateTime dateTo);
}

