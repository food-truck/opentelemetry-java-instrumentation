/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.redis;


import io.opentelemetry.instrumentation.api.instrumenter.net.InetSocketAddressNetClientAttributesExtractor;
import java.net.InetSocketAddress;

final class RedisNetAttributesExtractor
    extends InetSocketAddressNetClientAttributesExtractor<RedisRequest, Void> {

  @Override
  public InetSocketAddress getAddress(RedisRequest request, Void unused) {
    return null;
  }

  @Override
  public String transport(RedisRequest request, Void unused) {
    return null;
  }
}
