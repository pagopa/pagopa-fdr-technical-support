package it.gov.pagopa.fdrtechsupport.controller.middleware.filter;

import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.Set;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

@Provider
public class RequestFilter implements ContainerRequestFilter {

  @Inject Logger log;

  @ConfigProperty(name = "log.skip-logging.endpoints")
  private Set<String> skippedEndpoints;

  @Override
  public void filter(ContainerRequestContext containerRequestContext) throws IOException {

    String requestPath = containerRequestContext.getUriInfo().getPath();
    if (!skippedEndpoints.contains(requestPath)) {

      long requestStartTime = System.nanoTime();
      containerRequestContext.setProperty("requestStartTime", requestStartTime);
      String requestMethod = containerRequestContext.getMethod();
      MultivaluedMap<String, String> queryParameters =
          containerRequestContext.getUriInfo().getQueryParameters();
      log.infof("REQ --> %s [uri:%s] [params:%s]", requestMethod, requestPath, queryParameters);
    }
  }
}
