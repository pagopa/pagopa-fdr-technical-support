package it.gov.pagopa.fdrtechsupport.util.common;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

public class StringUtil {

  public static String decompressGZip(byte[] gzipContent) {
    String result;
    if (gzipContent == null || gzipContent.length == 0) {
      result = "";

    } else {

      // byte[] compressedData = Base64.getDecoder().decode(gzipContent);
      ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(gzipContent);

      try (GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream)) {

        byte[] buffer = new byte[1024];
        StringBuilder output = new StringBuilder();
        int bytesRead;
        while ((bytesRead = gzipInputStream.read(buffer)) != -1) {
          output.append(new String(buffer, 0, bytesRead));
        }
        result = output.toString();

      } catch (IOException e) {
        result = null;
      }
    }
    return result;
  }

  public static String insertCharacterAfter(
      String input, String newChar, int charsPerLine, char separator) {

    StringBuilder result = new StringBuilder();
    int count = 0;
    for (int i = 0; i < input.length(); i++) {
      char c = input.charAt(i);
      result.append(c);
      if (c == separator && count >= charsPerLine) {
        result.append(newChar);
        count = 0;
      } else {
        count++;
      }
    }
    return result.toString();
  }
}
