/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.ws;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;
import io.opentelemetry.instrumentation.api.instrumenter.SpanKindExtractor;

public final class CoreNGWSSpanSingletons {
  private static final String INSTRUMENTATION_NAME = "io.opentelemetry.core-ng-ws";
  private static final Instrumenter<MethodRequest, Object> INSTRUMENTER = createInstrumenter();

  public static Instrumenter<MethodRequest, Object> instrumenter() {
    return INSTRUMENTER;
  }

  private static Instrumenter<MethodRequest, Object> createInstrumenter() {
    return Instrumenter.builder(
        GlobalOpenTelemetry.get(), INSTRUMENTATION_NAME, CoreNGWSSpanSingletons::spanNameFromMethod)
        .newInstrumenter(SpanKindExtractor.alwaysInternal());
  }

  private static String spanNameFromMethod(MethodRequest request) {
    String spanName = "";
    switch (request.type()) {
      case ON_MESSAGE:
        spanName = request.action();
        break;
      case ON_CLOSE:
        spanName = request.action() + ":close";
        break;
    }
    return spanName;
  }
}
