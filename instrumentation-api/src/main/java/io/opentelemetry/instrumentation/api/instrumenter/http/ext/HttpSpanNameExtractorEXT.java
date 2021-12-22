/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.api.instrumenter.http.ext;

import io.opentelemetry.instrumentation.api.instrumenter.SpanNameExtractor;
import javax.annotation.Nullable;

/**
 * Extractor of the <a
 * href="https://github.com/open-telemetry/opentelemetry-specification/blob/main/specification/trace/semantic_conventions/http.md#name">HTTP
 * span name</a>. Instrumentation of HTTP server or client frameworks should use this class to
 * comply with OpenTelemetry HTTP semantic conventions.
 */
public final class HttpSpanNameExtractorEXT<REQUEST> implements SpanNameExtractor<REQUEST> {

  /**
   * Returns a {@link SpanNameExtractor} which should be used for HTTP requests. HTTP attributes
   * will be examined to determine the name of the span.
   */
  public static <REQUEST> SpanNameExtractor<REQUEST> create(
      HttpCommonAttributesExtractorEXT<REQUEST, ?> attributesExtractor) {
    return new HttpSpanNameExtractorEXT<>(attributesExtractor);
  }

  private final HttpCommonAttributesExtractorEXT<REQUEST, ?> attributesExtractor;

  private HttpSpanNameExtractorEXT(HttpCommonAttributesExtractorEXT<REQUEST, ?> attributesExtractor) {
    this.attributesExtractor = attributesExtractor;
  }

  @Override
  public String extract(REQUEST request) {
    String route = extractRoute(request);
    if (route != null) {
      return route;
    }
    String path = attributesExtractor.path(request);
    String method = attributesExtractor.method(request);
    boolean isWebSocket = attributesExtractor.isWebSocket(request);

    if (isWebSocket) {
      return "ws: " + path + ":open";
    }
    if (isRegularHTTPRequest(path, method)) {
      return method + ' ' + path;
    }
    if (method != null) {
      return "HTTP " + method;
    }
    return "HTTP request";
  }

  public boolean isRegularHTTPRequest(String path, String method) {
    return path != null && method != null;
  }

  @Nullable
  private String extractRoute(REQUEST request) {
    if (attributesExtractor instanceof HttpServerAttributesExtractorEXT) {
      return ((HttpServerAttributesExtractorEXT<REQUEST, ?>) attributesExtractor).route(request);
    }
    return null;
  }
}
