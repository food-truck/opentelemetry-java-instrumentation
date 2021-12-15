/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.undertow;

import io.opentelemetry.instrumentation.api.instrumenter.http.HttpServerAttributesExtractor;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderMap;
import io.undertow.util.HeaderValues;
import io.undertow.util.Headers;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

public class UndertowHttpAttributesExtractor
    extends HttpServerAttributesExtractor<HttpServerExchange, HttpServerExchange> {
  private static final String UUID_PATTERN = "/[0-9a-f]{8}(-[0-9a-f]{4}){3}-[0-9a-f]{12}";
  private static final String UUID_WITHOUT_SLASH_PATTERN = "/[0-9a-f]{24}";
  private static final String NUMBER_ID_PATTERN = "/\\d+";
  private static final String ALPH_ID_PATTERN = "/[A-Z_]+";
  private static final String ID_PLACEHOLDER = "/:id";

  @Override
  protected String method(HttpServerExchange exchange) {
    return exchange.getRequestMethod().toString();
  }

  @Override
  public String path(HttpServerExchange exchange) {
    // don't change the order for below code.
    return exchange.getRequestPath().replaceAll(UUID_PATTERN, ID_PLACEHOLDER)
        .replaceAll(UUID_WITHOUT_SLASH_PATTERN, ID_PLACEHOLDER)
        .replaceAll(NUMBER_ID_PATTERN, ID_PLACEHOLDER)
        .replaceAll(ALPH_ID_PATTERN, ID_PLACEHOLDER);
  }

  @Override
  public boolean isWebSocket(HttpServerExchange exchange) {
    String method = exchange.getRequestMethod().toString();
    HeaderMap headers = exchange.getRequestHeaders();

    if ("GET".equals(method) && headers.getFirst(Headers.SEC_WEB_SOCKET_KEY) != null) {
      String version = headers.getFirst(Headers.SEC_WEB_SOCKET_VERSION);
      return "13".equals(version);  // only support latest ws version
    }
    return false;
  }

  @Override
  protected List<String> requestHeader(HttpServerExchange exchange, String name) {
    HeaderValues values = exchange.getRequestHeaders().get(name);
    return values == null ? Collections.emptyList() : values;
  }

  @Override
  @Nullable
  protected Long requestContentLength(
      HttpServerExchange exchange, @Nullable HttpServerExchange unused) {
    long requestContentLength = exchange.getRequestContentLength();
    return requestContentLength != -1 ? requestContentLength : null;
  }

  @Override
  @Nullable
  protected Long requestContentLengthUncompressed(
      HttpServerExchange exchange, @Nullable HttpServerExchange unused) {
    return null;
  }

  @Override
  protected String flavor(HttpServerExchange exchange) {
    String flavor = exchange.getProtocol().toString();
    // remove HTTP/ prefix to comply with semantic conventions
    if (flavor.startsWith("HTTP/")) {
      flavor = flavor.substring("HTTP/".length());
    }
    return flavor;
  }

  @Override
  protected Integer statusCode(HttpServerExchange exchange, HttpServerExchange unused) {
    return exchange.getStatusCode();
  }

  @Override
  @Nullable
  protected Long responseContentLength(HttpServerExchange exchange, HttpServerExchange unused) {
    long responseContentLength = exchange.getResponseContentLength();
    return responseContentLength != -1 ? responseContentLength : null;
  }

  @Override
  @Nullable
  protected Long responseContentLengthUncompressed(
      HttpServerExchange exchange, HttpServerExchange unused) {
    return null;
  }

  @Override
  protected List<String> responseHeader(
      HttpServerExchange exchange, HttpServerExchange unused, String name) {
    HeaderValues values = exchange.getResponseHeaders().get(name);
    return values == null ? Collections.emptyList() : values;
  }

  @Override
  @Nullable
  protected String target(HttpServerExchange exchange) {
    String requestPath = exchange.getRequestPath();
    String queryString = exchange.getQueryString();
    if (requestPath != null && queryString != null && !queryString.isEmpty()) {
      return requestPath + "?" + queryString;
    }
    return requestPath;
  }

  @Override
  @Nullable
  protected String scheme(HttpServerExchange exchange) {
    return exchange.getRequestScheme();
  }

  @Override
  @Nullable
  protected String route(HttpServerExchange exchange) {
    return null;
  }

  @Override
  @Nullable
  protected String serverName(HttpServerExchange exchange, @Nullable HttpServerExchange unused) {
    return null;
  }
}
