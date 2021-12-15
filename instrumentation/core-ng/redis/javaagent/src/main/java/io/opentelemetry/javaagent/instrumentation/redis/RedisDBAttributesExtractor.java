/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.redis;

import io.opentelemetry.instrumentation.api.instrumenter.db.DbAttributesExtractor;
import io.opentelemetry.semconv.trace.attributes.SemanticAttributes;


final class RedisDBAttributesExtractor extends DbAttributesExtractor<RedisRequest, Void> {

  @Override
  protected String system(RedisRequest request) {
    return SemanticAttributes.DbSystemValues.REDIS;
  }

  @Override
  protected String user(RedisRequest request) {
    return null;
  }

  @Override
  protected String name(RedisRequest request) {
    return request.type.name();
  }

  @Override
  protected String connectionString(RedisRequest request) {
    return null;
  }

  @Override
  protected String statement(RedisRequest request) {
    return "key: " + request.key;
  }

  @Override
  protected String operation(RedisRequest request) {
    return null;
  }
}
