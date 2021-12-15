package io.opentelemetry.javaagent.instrumentation.redis;

public final class RedisRequestBuilder {
  private final String key;
  private final RedisRequest.Type type;

  public RedisRequestBuilder(String key, RedisRequest.Type type) {
    this.key = key;
    this.type = type;
  }

  public RedisRequest build() {
    RedisRequest request = new RedisRequest();
    request.key = key;
    request.type = type;
    return request;
  }
}
