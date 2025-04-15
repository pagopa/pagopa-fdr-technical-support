package it.gov.pagopa.fdrtechsupport.util.error.exception;

import java.io.Serializable;

import it.gov.pagopa.fdrtechsupport.exceptions.AppErrorCodeMessageInterface;
import lombok.Getter;

@Getter
public class AppException extends RuntimeException {

  private final transient AppErrorCodeMessageInterface codeMessage;

  private final transient Object[] args;

  public AppException(Throwable cause, AppErrorCodeMessageInterface codeMessage) {
    super(cause);
    this.codeMessage = codeMessage;
    this.args = null;
  }

  public AppException(Throwable cause, AppErrorCodeMessageInterface codeMessage, Object... args) {
    super(cause);
    this.codeMessage = codeMessage;
    this.args = args;
  }

  public AppException(AppErrorCodeMessageInterface codeMessage) {
    super();
    this.codeMessage = codeMessage;
    this.args = null;
  }

  public AppException(AppErrorCodeMessageInterface codeMessage, Serializable... args) {
    super();
    this.codeMessage = codeMessage;
    this.args = args;
  }
}
