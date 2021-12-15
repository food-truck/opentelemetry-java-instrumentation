/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.redis;

import static java.util.Arrays.asList;

import com.google.auto.service.AutoService;
import io.opentelemetry.javaagent.extension.instrumentation.InstrumentationModule;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import java.util.List;

@AutoService(InstrumentationModule.class)
public class CoreNGRedisInstrumentationModule extends InstrumentationModule {

  public CoreNGRedisInstrumentationModule() {
    super("core-ng-redis", "core-ng-redis-7.6.15");
  }

  @Override
  public List<TypeInstrumentation> typeInstrumentations() {
    return asList(new CoreNGCacheInstrumentation(), new CoreNGRedisInstrumentation());
  }
}
