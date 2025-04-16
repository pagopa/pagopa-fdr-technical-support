package it.gov.pagopa.fdrtechsupport.openapi;


import it.gov.pagopa.fdrtechsupport.util.error.enums.AppErrorCodeMessageEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface APIAppErrorMetadata {

  AppErrorCodeMessageEnum[] errors() default {};
}
