package it.gov.pagopa.fdrtechsupport.client;

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

@Path("/")
@ClientHeaderParam(
    name = "Ocp-Apim-Subscription-Key",
    value = "${quarkus.rest-client.fdr.subscription-key}")
@RegisterRestClient(configKey = "fdr")
public interface FdrRestClient {

  @ClientExceptionMapper
  static RuntimeException toException(Response response) {
    return switch (response.getStatus()) {
      case 404 -> new AppException(AppErrorCodeMessageEnum.FLOW_NOT_FOUND_CLIENT);
      case 401 -> new AppException(AppErrorCodeMessageEnum.UNAUTHORIZED_CLIENT);
      default -> new AppException(AppErrorCodeMessageEnum.ERROR);
    };
  }

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
