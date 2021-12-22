package io.opentelemetry.javaagent.instrumentation.ws;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;
import io.opentelemetry.instrumentation.api.instrumenter.SpanKindExtractor;

public class CoreNGWSSingletons {
  private static final String INSTRUMENTATION_NAME = "io.opentelemetry.core-ng-ws";
  private static final Instrumenter<WSRequest, Void> INSTRUMENTER;

  static {
    CoreNGWSSpanExtractor spanName = new CoreNGWSSpanExtractor();

    INSTRUMENTER = Instrumenter.<WSRequest, Void>builder(
        GlobalOpenTelemetry.get(), INSTRUMENTATION_NAME, spanName)
        .newInstrumenter(SpanKindExtractor.alwaysServer());
//        .newServerInstrumenter(new CoreNGWSResponseHeaderGetter());
  }

  public static Instrumenter<WSRequest, Void> instrumenter() {
    return INSTRUMENTER;
  }

  private CoreNGWSSingletons() {}
}
