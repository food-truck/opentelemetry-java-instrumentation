package io.opentelemetry.javaagent.instrumentation.redis;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;
import io.opentelemetry.instrumentation.api.instrumenter.SpanKindExtractor;
import io.opentelemetry.instrumentation.api.instrumenter.SpanNameExtractor;
import io.opentelemetry.instrumentation.api.instrumenter.db.DbAttributesExtractor;
import io.opentelemetry.instrumentation.api.instrumenter.db.DbSpanNameExtractor;

public final class CoreNGRedisSingletons {
  private static final String INSTRUMENTATION_NAME = "io.opentelemetry.core-ng-redis";
  private static final Instrumenter<RedisRequest, Void> INSTRUMENTER;

  static {
    DbAttributesExtractor<RedisRequest, Void> dbAttributesExtractor = new RedisDBAttributesExtractor();
    RedisNetAttributesExtractor netAttributesExtractor = new RedisNetAttributesExtractor();
    SpanNameExtractor<RedisRequest> spanName = DbSpanNameExtractor.create(dbAttributesExtractor);

    INSTRUMENTER = Instrumenter.<RedisRequest, Void>builder(
        GlobalOpenTelemetry.get(), INSTRUMENTATION_NAME, spanName)
        .addAttributesExtractor(dbAttributesExtractor)
        .addAttributesExtractor(netAttributesExtractor)
        .newInstrumenter(SpanKindExtractor.alwaysClient());
  }

  public static Instrumenter<RedisRequest, Void> instrumenter() {
    return INSTRUMENTER;
  }

  private CoreNGRedisSingletons() {}
}
