package it.gov.pagopa.fdrtechsupport.util.common;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StringUtilTest {

  @Test
  @DisplayName("decompressGZip: non-gzip input -> returns null")
  void decompress_notGzip_returnsNull() {
    byte[] bytes = "<xml>NOT_GZIP</xml>".getBytes(StandardCharsets.UTF_8);
    String out = StringUtil.decompressGZip(bytes);
    assertNull(out);
  }
}