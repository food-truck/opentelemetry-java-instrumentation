/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.ws;

import java.lang.reflect.Method;

public final class MethodRequest {
  private final Method method;
  private final Type type;
  private final String action;

  public MethodRequest(Method method, Type type, String action) {
    this.method = method;
    this.action = action;
    this.type = type;
  }

  public Method method() {
    return this.method;
  }

  public Type type() {
    return this.type;
  }

  public String action() {
    return this.action;
  }

  public enum Type {
    ON_CLOSE,
    ON_MESSAGE
  }
}
