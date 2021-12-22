package io.opentelemetry.instrumentation.api.instrumenter.http.ext;

public class URLFormatUtils {
  public static final String UUID_PATTERN = "/[0-9a-f]{8}(-[0-9a-f]{4}){3}-[0-9a-f]{12}";
  public static final String UUID_WITHOUT_SLASH_PATTERN = "/[0-9a-f]{24}";
  public static final String NUMBER_ID_PATTERN = "/\\d+";
  public static final String ALPH_ID_PATTERN = "/[A-Z_]+";
  public static final String ID_PLACEHOLDER = "/:id";
  public static final String IMAGE_SUFFIX = ".jpg";

  public static String format(String url) {
    return url.replaceAll(UUID_PATTERN, ID_PLACEHOLDER)
        .replaceAll(UUID_WITHOUT_SLASH_PATTERN, ID_PLACEHOLDER)
        .replaceAll(NUMBER_ID_PATTERN, ID_PLACEHOLDER)
        .replaceAll(ALPH_ID_PATTERN, ID_PLACEHOLDER)
        .replaceAll(IMAGE_SUFFIX, "");
  }
}
