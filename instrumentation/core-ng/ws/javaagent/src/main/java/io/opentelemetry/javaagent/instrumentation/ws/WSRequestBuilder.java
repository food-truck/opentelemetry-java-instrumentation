package io.opentelemetry.javaagent.instrumentation.ws;

public class WSRequestBuilder {
  private final String action;
  private final WSRequest.Type type;

  public WSRequestBuilder(String action, WSRequest.Type type) {
    this.action = action;
    this.type = type;
  }

  public WSRequest build() {
    WSRequest request = new WSRequest();
    request.action = action;
    request.type = type;
    return request;
  }
}
