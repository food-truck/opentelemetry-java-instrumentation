package io.opentelemetry.javaagent.instrumentation.ws;

import io.opentelemetry.context.propagation.TextMapGetter;
import javax.annotation.Nullable;

public class CoreNGWSResponseHeaderGetter implements TextMapGetter<WSRequest> {
  @Override
  public Iterable<String> keys(WSRequest unused) {
    return null;
  }

  @Nullable
  @Override
  public String get(@Nullable WSRequest unused, String s) {
    return null;
  }
}
