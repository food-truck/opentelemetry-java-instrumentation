package io.opentelemetry.javaagent.instrumentation.ws;

public class WSRequest {
  public Type type;
  public String action;

  public enum Type {
    ON_CLOSE,
    ON_MESSAGE
  }
}
