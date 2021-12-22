package io.opentelemetry.javaagent.instrumentation.ws;

import static io.opentelemetry.javaagent.instrumentation.api.Java8BytecodeBridge.currentContext;
import static io.opentelemetry.javaagent.instrumentation.ws.CoreNGWSSingletons.instrumenter;
import static net.bytebuddy.matcher.ElementMatchers.is;
import static net.bytebuddy.matcher.ElementMatchers.isMethod;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArgument;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

public class CoreNGWSInstrumentation implements TypeInstrumentation {

  @Override
  public ElementMatcher<TypeDescription> typeMatcher() {
    return named("core.framework.internal.web.websocket.WebSocketMessageListener");
  }

  @Override
  public void transform(TypeTransformer transformer) {
    transformer.applyAdviceToMethod(
        isMethod()
            .and(named("onMessage"))
            .and(takesArguments(5))
            .and(takesArgument(0, is(String.class)))
            .and(takesArgument(1, named("io.undertow.websockets.core.WebSocketChannel")))
            .and(takesArgument(2, named("io.undertow.websockets.core.BufferedTextMessage")))
            .and(takesArgument(3, named("core.framework.internal.web.websocket.ChannelImpl")))
            .and(takesArgument(4, named("core.framework.internal.log.ActionLog"))),
        this.getClass().getName() + "$AdviceOnMessage"
    );

    transformer.applyAdviceToMethod(
        isMethod()
            .and(named("onCloseMessage"))
            .and(takesArguments(5))
            .and(takesArgument(0, is(String.class)))
            .and(takesArgument(1, named("io.undertow.websockets.core.CloseMessage")))
            .and(takesArgument(2, named("io.undertow.websockets.core.WebSocketChannel")))
            .and(takesArgument(3, named("core.framework.internal.web.websocket.ChannelImpl")))
            .and(takesArgument(4, named("core.framework.internal.log.ActionLog"))),
        this.getClass().getName() + "$AdviceOnClose"
    );
  }

  public static class AdviceOnMessage {
    @Advice.OnMethodEnter(suppress = Throwable.class)
    public static void onEnter(
        @Advice.Argument(0) String action,
        @Advice.Local("otelContext") Context context) {
      WSRequest request = new WSRequestBuilder(action, WSRequest.Type.ON_MESSAGE).build();
      Context parentContext = currentContext();
      if (!instrumenter().shouldStart(parentContext, request)) {
        return;
      }
      context = instrumenter().start(parentContext, request);
      Scope scope = context.makeCurrent();
    }

    @Advice.OnMethodExit(onThrowable = Throwable.class, suppress = Throwable.class)
    public static void stopSpan(
        @Advice.Argument(0) String action,
        @Advice.Thrown Throwable throwable,
        @Advice.Local("otelContext") Context context,
        @Advice.Local("otelScope") Scope scope) {
      if (scope != null) {
        scope.close();
      }
      WSRequest request = new WSRequestBuilder(action, WSRequest.Type.ON_MESSAGE).build();
      instrumenter().end(context, request, null, null);
    }
  }

  public static class AdviceOnClose {
    @Advice.OnMethodEnter(suppress = Throwable.class)
    public static void onEnter(
        @Advice.Argument(0) String action,
        @Advice.Local("otelContext") Context context) {
      WSRequest request = new WSRequestBuilder(action, WSRequest.Type.ON_CLOSE).build();
      Context parentContext = currentContext();
      if (!instrumenter().shouldStart(parentContext, request)) {
        return;
      }
      context = instrumenter().start(parentContext, request);
      Scope scope = context.makeCurrent();
    }

    @Advice.OnMethodExit(onThrowable = Throwable.class, suppress = Throwable.class)
    public static void stopSpan(
        @Advice.Argument(0) String action,
        @Advice.Thrown Throwable throwable,
        @Advice.Local("otelContext") Context context,
        @Advice.Local("otelScope") Scope scope) {
      if (scope != null) {
        scope.close();
      }
      WSRequest request = new WSRequestBuilder(action, WSRequest.Type.ON_CLOSE).build();
      instrumenter().end(context, request, null, throwable);
    }
  }
}
