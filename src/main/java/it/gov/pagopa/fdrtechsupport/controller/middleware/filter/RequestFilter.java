package it.gov.pagopa.fdrtechsupport.controller.middleware.filter;

import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.UUID;
import org.jboss.logging.Logger;

@Provider
public class RequestFilter implements ContainerRequestFilter {

  @Inject Logger log;

  @Override
  public void filter(ContainerRequestContext containerRequestContext) throws IOException {
    long requestStartTime = System.nanoTime();
    containerRequestContext.setProperty("requestStartTime", requestStartTime);

    String sessionId = UUID.randomUUID().toString();

    String requestMethod = containerRequestContext.getMethod();
    String requestPath = containerRequestContext.getUriInfo().getPath();

    MultivaluedMap<String, String> queryParameters =
        containerRequestContext.getUriInfo().getQueryParameters();

    log.infof("REQ --> %s [uri:%s] [params:%s]", requestMethod, requestPath, queryParameters);
  }
}
