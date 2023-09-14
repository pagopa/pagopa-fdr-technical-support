package it.gov.pagopa.fdrtechsupport.clients;

import io.quarkiverse.openapi.generator.annotations.GeneratedClass;
import io.quarkus.rest.client.reactive.ClientExceptionMapper;
import it.gov.pagopa.fdrtechsupport.exceptions.AppErrorCodeMessageEnum;
import it.gov.pagopa.fdrtechsupport.exceptions.AppException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.openapi.quarkus.api_fdr_json.model.FdrByPspIdIuvIurResponse;
import org.openapi.quarkus.api_fdr_json.model.GetPaymentResponse;

import java.time.LocalDateTime;

@Path("/")
@ClientHeaderParam(name = "Ocp-Apim-Subscription-Key", value = "${fdr.api-key-value}")
@RegisterRestClient(configKey = "fdr")
@GeneratedClass(value = "api_fdr.json")
public interface FdrRestClient {

    @ClientExceptionMapper
    static RuntimeException toException(Response response) {
        if (response.getStatus() == 500) {
            return new AppException(
                    AppErrorCodeMessageEnum.ERROR
            );
        }
        if (response.getStatus() == 404) {
            return new AppException(
                    AppErrorCodeMessageEnum.FLOW_NOT_FOUND_CLIENT
            );
        }
        return new AppException(
                AppErrorCodeMessageEnum.ERROR
        );
    }

    @GET
    @Path("/internal/organizations/{ec}/fdrs/{flowName}/revisions/{revision}/psps/{psp}/payments")
    GetPaymentResponse getFlow(@QueryParam("page") long page, @PathParam("ec") String ec, @PathParam("flowName") String flowName,
                               @PathParam("revision") String revision, @PathParam("psp") String psp);

    @GET
    @Path("/internal/psps/{pspId}/iuv/{iuv}")
    FdrByPspIdIuvIurResponse getFlowByIuv(@PathParam("pspId") String pspId, @PathParam("iuv") String iuv,
                                          @QueryParam("pageNumber") Integer pageNumber,
                                          @QueryParam("dateFrom") LocalDateTime dateFrom,
                                          @QueryParam("dateTo") LocalDateTime dateTo);

    @GET
    @Path("/internal/psps/{pspId}/iur/{iur}")
    FdrByPspIdIuvIurResponse getFlowByIur(@PathParam("pspId") String pspId, @PathParam("iur") String iur,
                                              @QueryParam("pageNumber") Integer pageNumber,
                                              @QueryParam("dateFrom") LocalDateTime dateFrom,
                                              @QueryParam("dateTo") LocalDateTime dateTo);
}

