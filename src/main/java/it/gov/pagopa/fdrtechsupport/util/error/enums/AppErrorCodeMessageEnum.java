package it.gov.pagopa.fdrtechsupport.util.error.enums;

import it.gov.pagopa.fdrtechsupport.util.constant.AppConstant;
import it.gov.pagopa.fdrtechsupport.util.logging.AppMessageUtil;
import org.jboss.resteasy.reactive.RestResponse;

public enum AppErrorCodeMessageEnum {
  DATE_BAD_REQUEST(
      "0400",
      "bad.request",
      RestResponse.Status.BAD_REQUEST,
      "An error occurred while validating the provided dates. Date from and date to must be either"
          + " null o filled in, if present, date from must be less than date to"),
  FLOW_NOT_FOUND(
      "0404",
      "flow.not.found",
      RestResponse.Status.NOT_FOUND,
      "An error occurred during the search of flow. The needed flow does not exists in current"
          + " environment"),
  FLOW_NOT_FOUND_CLIENT(
      "1404",
      "flow.not.found",
      RestResponse.Status.NOT_FOUND,
      "An error occurred during the search of flow. The needed flow was not found"),
  UNAUTHORIZED_CLIENT(
      "1401",
      "system.error",
      RestResponse.Status.INTERNAL_SERVER_ERROR,
      "An error occurred while invoking external services"),
  ERROR(
      "0500",
      "system.error",
      RestResponse.Status.INTERNAL_SERVER_ERROR,
      "An error occurred during computation. This could be caused by an applicative error and it is"
          + " probably required to open an issue."),
  BAD_REQUEST(
      "0400",
      "bad.request",
      RestResponse.Status.BAD_REQUEST,
      "A generic 'Bad Request' error is occurred during request validation"),
  BAD_REQUEST_INPUT_JSON(
      "0401",
      "bad.request.inputJson",
      RestResponse.Status.BAD_REQUEST,
      "A generic error occurred during execution of request syntactic validation"),
  BAD_REQUEST_INPUT_JSON_INSTANT(
      "0402",
      "bad.request.inputJson.instant",
      RestResponse.Status.BAD_REQUEST,
      "An error occurred during execution of request syntactic validation, in particular regarding"
          + " the analysis of date values"),
  BAD_REQUEST_INPUT_JSON_ENUM(
      "0403",
      "bad.request.inputJson.enum",
      RestResponse.Status.BAD_REQUEST,
      "An error occurred during execution of request syntactic validation, in particular regarding"
          + " the analysis of enumerative values"),
  BAD_REQUEST_INPUT_JSON_DESERIALIZE_ERROR(
      "0404",
      "bad.request.inputJson.deserialize",
      RestResponse.Status.BAD_REQUEST,
      "An error occurred during execution of deserialization of request from a JSON string"),
  BAD_REQUEST_INPUT_JSON_NON_VALID_FORMAT(
      "0405",
      "bad.request.inputJson.notValidJsonFormat",
      RestResponse.Status.BAD_REQUEST,
      "An error occurred during execution of analysis of JSON request, in particular regarding its"
          + " format"),
  INVALID_FILE_TYPE(
      "0406",
      "bad.request.invalidFileType",
      RestResponse.Status.BAD_REQUEST,
      "An error occurred while validating the provided file type. The file must be of type xml or"
          + " json");

  private final String errorCode;
  private final String errorMessageKey;
  private final RestResponse.Status httpStatus;
  private final String openAPIDescription;

  AppErrorCodeMessageEnum(
      String errorCode,
      String errorMessageKey,
      RestResponse.Status httpStatus,
      String openAPIDescription) {
    this.errorCode = errorCode;
    this.errorMessageKey = errorMessageKey;
    this.httpStatus = httpStatus;
    this.openAPIDescription = openAPIDescription;
  }

  public String errorCode() {
    return AppConstant.SERVICE_CODE_APP + "-" + errorCode;
  }

  public String message(Object... args) {
    return AppMessageUtil.getMessage(errorMessageKey, args);
  }

  public RestResponse.Status httpStatus() {
    return httpStatus;
  }

  public String getOpenAPIDescription() {
    return openAPIDescription;
  }
}
