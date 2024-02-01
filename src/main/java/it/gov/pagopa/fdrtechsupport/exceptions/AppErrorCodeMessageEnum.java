package it.gov.pagopa.fdrtechsupport.exceptions;

import it.gov.pagopa.fdrtechsupport.util.AppConstant;
import it.gov.pagopa.fdrtechsupport.util.AppMessageUtil;
import org.jboss.resteasy.reactive.RestResponse;

public enum AppErrorCodeMessageEnum implements AppErrorCodeMessageInterface {
  DATE_BAD_REQUEST("0400", "bad.request", RestResponse.Status.BAD_REQUEST),
  INTERVAL_TOO_LARGE("0400.1", "bad.request.interval.too.large", RestResponse.Status.BAD_REQUEST),
  FLOW_NOT_FOUND("0404", "flow.not.found", RestResponse.Status.NOT_FOUND),
  FLOW_NOT_FOUND_CLIENT("1404", "flow.not.found", RestResponse.Status.NOT_FOUND),
  UNAUTHORIZED_CLIENT("1401", "system.error", RestResponse.Status.INTERNAL_SERVER_ERROR),
  ERROR("0500", "system.error", RestResponse.Status.INTERNAL_SERVER_ERROR),
  BAD_REQUEST("0400", "bad.request", RestResponse.Status.BAD_REQUEST),
  BAD_REQUEST_INPUT_JSON("0401", "bad.request.inputJson", RestResponse.Status.BAD_REQUEST),
  BAD_REQUEST_INPUT_JSON_INSTANT(
      "0402", "bad.request.inputJson.instant", RestResponse.Status.BAD_REQUEST),
  BAD_REQUEST_INPUT_JSON_ENUM(
      "0403", "bad.request.inputJson.enum", RestResponse.Status.BAD_REQUEST),
  BAD_REQUEST_INPUT_JSON_DESERIALIZE_ERROR(
      "0404", "bad.request.inputJson.deserialize", RestResponse.Status.BAD_REQUEST),
  BAD_REQUEST_INPUT_JSON_NON_VALID_FORMAT(
      "0405", "bad.request.inputJson.notValidJsonFormat", RestResponse.Status.BAD_REQUEST),
  INVALID_FILE_TYPE("0406", "bad.request.invalidFileType", RestResponse.Status.BAD_REQUEST);
  private final String errorCode;
  private final String errorMessageKey;
  private final RestResponse.Status httpStatus;

  AppErrorCodeMessageEnum(
      String errorCode, String errorMessageKey, RestResponse.Status httpStatus) {
    this.errorCode = errorCode;
    this.errorMessageKey = errorMessageKey;
    this.httpStatus = httpStatus;
  }

  @Override
  public String errorCode() {
    return AppConstant.SERVICE_CODE_APP + "-" + errorCode;
  }

  @Override
  public String message(Object... args) {
    return AppMessageUtil.getMessage(errorMessageKey, args);
  }

  @Override
  public RestResponse.Status httpStatus() {
    return httpStatus;
  }
}
