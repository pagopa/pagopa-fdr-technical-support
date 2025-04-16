package it.gov.pagopa.fdrtechsupport.client;

import io.quarkiverse.openapi.generator.annotations.GeneratedClass;
import io.quarkus.rest.client.reactive.ClientExceptionMapper;
import it.gov.pagopa.fdrtechsupport.client.model.PaginatedFlowsBySenderAndReceiverResponse;
import it.gov.pagopa.fdrtechsupport.util.error.enums.AppErrorCodeMessageEnum;
import it.gov.pagopa.fdrtechsupport.util.error.exception.AppException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import java.time.LocalDateTime;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.openapi.quarkus.api_fdr_json.model.GetPaymentResponse;

@Path("/")
@ClientHeaderParam(name = "Ocp-Apim-Subscription-Key", value = "${fdr.api-key-value}")
@RegisterRestClient(configKey = "fdr")
@GeneratedClass(value = "api_fdr.json")
public interface FdrRestClient {

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
  @Path("/internal/organizations/{ec}/fdrs/{flowName}/revisions/{revision}/psps/{psp}/payments")
  GetPaymentResponse getFlow(
      @QueryParam("page") long page,
      @PathParam("ec") String ec,
      @PathParam("flowName") String flowName,
      @PathParam("revision") String revision,
      @PathParam("psp") String psp);

  @GET
  @Path("/internal/psps/{pspId}/iuv/{iuv}")
  PaginatedFlowsBySenderAndReceiverResponse getFlowByIuv(
      @PathParam("pspId") String pspId,
      @PathParam("iuv") String iuv,
      @QueryParam("createdFrom") LocalDateTime createdFrom,
      @QueryParam("createdTo") LocalDateTime createdTo,
      @QueryParam("page") Integer pageNumber,
      @QueryParam("size") Integer pageSize);

  @GET
  @Path("/internal/psps/{pspId}/iur/{iur}")
  PaginatedFlowsBySenderAndReceiverResponse getFlowByIur(
      @PathParam("pspId") String pspId,
      @PathParam("iur") String iur,
      @QueryParam("createdFrom") LocalDateTime createdFrom,
      @QueryParam("createdTo") LocalDateTime createdTo,
      @QueryParam("page") Integer pageNumber,
      @QueryParam("size") Integer pageSize);
}
