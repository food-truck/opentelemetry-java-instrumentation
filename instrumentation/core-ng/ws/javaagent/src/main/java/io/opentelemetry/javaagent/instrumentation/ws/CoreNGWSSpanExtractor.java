package io.opentelemetry.javaagent.instrumentation.ws;

import io.opentelemetry.instrumentation.api.instrumenter.SpanNameExtractor;

public class CoreNGWSSpanExtractor implements SpanNameExtractor<WSRequest> {

  @Override
  public String extract(WSRequest action) {
    String spanName = "";
    switch (action.type) {
      case ON_MESSAGE:
        spanName = action.action;
        break;
      case ON_CLOSE:
        spanName = action.action + ":close";
        break;
    }
    return spanName;
  }
}
