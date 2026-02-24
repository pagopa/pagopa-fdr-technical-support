package it.gov.pagopa.fdrtechsupport.util;

import java.io.IOException;
import java.io.InputStream;

public class ObjectMapperUtil {

  public static InputStream readFromFile(String relativePath) throws IOException {

    try (var inputStream =
        ObjectMapperUtil.class.getClassLoader().getResourceAsStream(relativePath)) {
      if (inputStream == null) {
        throw new IllegalArgumentException("File not found: " + relativePath);
      }

      return inputStream;
    }
  }
}
