package it.gov.pagopa.fdrtechsupport.clients;

import io.quarkiverse.openapi.generator.annotations.GeneratedClass;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.openapi.quarkus.api_fdr_json.model.FdrByPspIdIuvIurResponse;

import java.time.LocalDateTime;
import org.openapi.quarkus.fdr_internal_json.model.GetPaymentResponse;
import org.openapi.quarkus.fdr_internal_json.model.GetResponse;

@Path("/")
@ClientHeaderParam(name = "Ocp-Apim-Subscription-Key", value = "${fdr.api-key-value}")
@RegisterRestClient(configKey = "fdr")
@GeneratedClass(value = "api_fdr.json", tag = "fdr_replies")
public interface FdrRestClient {

    @GET
    @Path("/internal/history/organizations/{ec}/fdrs/{flowName}/revisions/{revision}/psps/{psp}/payments")
    GetPaymentResponse getFlow(@QueryParam("page") long page, @PathParam("ec") String ec, @PathParam("flowName") String flowName,
                               @PathParam("revision") String revision, @PathParam("psp") String psp);

    @GET
    @Path("/psps/{pspId}/iuv/{iuv}")
    FdrByPspIdIuvIurResponse getFlowByIuv(@PathParam("pspId") String pspId, @PathParam("iuv") String iuv,
                                          @QueryParam("pageNumber") Integer pageNumber,
                                          @QueryParam("dateFrom") LocalDateTime dateFrom,
                                          @QueryParam("dateTo") LocalDateTime dateTo);

    @GET
    @Path("/psps/{pspId}/iur/{iur}")
    FdrByPspIdIuvIurResponse getFlowByIur(@PathParam("pspId") String pspId, @PathParam("iur") String iur,
                                              @QueryParam("pageNumber") Integer pageNumber,
                                              @QueryParam("dateFrom") LocalDateTime dateFrom,
                                              @QueryParam("dateTo") LocalDateTime dateTo);
}

