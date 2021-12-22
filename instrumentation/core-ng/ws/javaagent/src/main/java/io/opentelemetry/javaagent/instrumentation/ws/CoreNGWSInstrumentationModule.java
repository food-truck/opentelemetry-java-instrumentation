package io.opentelemetry.javaagent.instrumentation.ws;

import com.google.auto.service.AutoService;
import io.opentelemetry.javaagent.extension.instrumentation.InstrumentationModule;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import java.util.Collections;
import java.util.List;

@AutoService(InstrumentationModule.class)
public class CoreNGWSInstrumentationModule extends InstrumentationModule {

  public CoreNGWSInstrumentationModule() {
    super("core-ng-ws", "core-ng-ws-7.6.15");
  }

  @Override
  public List<TypeInstrumentation> typeInstrumentations() {
    return Collections.singletonList(new CoreNGWSInstrumentation());
  }
}
