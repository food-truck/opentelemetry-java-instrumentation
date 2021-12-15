package io.opentelemetry.javaagent.instrumentation.redis;

public final class RedisRequest {
  public String key;
  public Type type;

  public enum Type {
    REDIS_CACHE,
    REDIS
  }
}
