package it.gov.pagopa.fdrtechsupport.util.common;

public class StringUtil {

  private StringUtil() {}

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
