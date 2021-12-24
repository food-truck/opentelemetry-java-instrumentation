package io.opentelemetry.javaagent.instrumentation.ws;

import static io.opentelemetry.javaagent.instrumentation.ws.CoreNGWSSpanSingletons.instrumenter;
import static net.bytebuddy.matcher.ElementMatchers.is;
import static net.bytebuddy.matcher.ElementMatchers.isMethod;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArgument;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.instrumentation.api.annotation.support.async.AsyncOperationEndSupport;
import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import io.opentelemetry.javaagent.instrumentation.api.Java8BytecodeBridge;
import java.lang.reflect.Method;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
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
        this.getClass().getName() + "$OnMessageAdvice"
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
        this.getClass().getName() + "$OnCloseAdvice"
    );
  }

  @SuppressWarnings("unused")
  public static class OnMessageAdvice {

    @Advice.OnMethodEnter(suppress = Throwable.class)
    public static void onEnter(
        @Advice.Argument(0) String action,
        @Advice.Origin Method originMethod,
        @Advice.Local("otelMethod") Method method,
        @Advice.Local("otelOperationEndSupport")
            AsyncOperationEndSupport<MethodRequest, Object> operationEndSupport,
        @Advice.Local("otelContext") Context context,
        @Advice.Local("otelScope") Scope scope) {
      // Every usage of @Advice.Origin Method is replaced with a call to Class.getMethod, copy it
      // to local variable so that there would be only one call to Class.getMethod.
      method = originMethod;
      MethodRequest methodRequest= new MethodRequest(method, MethodRequest.Type.ON_MESSAGE, action);
      Instrumenter<MethodRequest, Object> instrumenter = instrumenter();
      Context current = Java8BytecodeBridge.currentContext();

      if (instrumenter.shouldStart(current, methodRequest)) {
        context = instrumenter.start(current, methodRequest);
        scope = context.makeCurrent();
        operationEndSupport =
            AsyncOperationEndSupport.create(instrumenter, Object.class, method.getReturnType());
      }
    }

    @Advice.OnMethodExit(onThrowable = Throwable.class, suppress = Throwable.class)
    public static void stopSpan(
        @Advice.Local("otelMethod") Method method,
        @Advice.Local("otelRequest") MethodRequest methodRequest,
        @Advice.Local("otelOperationEndSupport")
            AsyncOperationEndSupport<MethodRequest, Object> operationEndSupport,
        @Advice.Local("otelContext") Context context,
        @Advice.Local("otelScope") Scope scope,
        @Advice.Return(typing = Assigner.Typing.DYNAMIC, readOnly = false) Object returnValue,
        @Advice.Thrown Throwable throwable) {
      if (scope == null) {
        return;
      }
      scope.close();
      returnValue = operationEndSupport.asyncEnd(context, methodRequest, returnValue, throwable);
    }
  }

  @SuppressWarnings("unused")
  public static class OnCloseAdvice {

    @Advice.OnMethodEnter(suppress = Throwable.class)
    public static void onEnter(
        @Advice.Argument(0) String action,
        @Advice.Origin Method originMethod,
        @Advice.Local("otelMethod") Method method,
        @Advice.Local("otelOperationEndSupport")
            AsyncOperationEndSupport<MethodRequest, Object> operationEndSupport,
        @Advice.Local("otelContext") Context context,
        @Advice.Local("otelScope") Scope scope) {
      // Every usage of @Advice.Origin Method is replaced with a call to Class.getMethod, copy it
      // to local variable so that there would be only one call to Class.getMethod.
      method = originMethod;
      MethodRequest methodRequest= new MethodRequest(method, MethodRequest.Type.ON_MESSAGE, action);
      Instrumenter<MethodRequest, Object> instrumenter = instrumenter();
      Context current = Java8BytecodeBridge.currentContext();

      if (instrumenter.shouldStart(current, methodRequest)) {
        context = instrumenter.start(current, methodRequest);
        scope = context.makeCurrent();
        operationEndSupport =
            AsyncOperationEndSupport.create(instrumenter, Object.class, method.getReturnType());
      }
    }

    @Advice.OnMethodExit(onThrowable = Throwable.class, suppress = Throwable.class)
    public static void stopSpan(
        @Advice.Local("otelMethod") Method method,
        @Advice.Local("otelRequest") MethodRequest methodRequest,
        @Advice.Local("otelOperationEndSupport")
            AsyncOperationEndSupport<MethodRequest, Object> operationEndSupport,
        @Advice.Local("otelContext") Context context,
        @Advice.Local("otelScope") Scope scope,
        @Advice.Return(typing = Assigner.Typing.DYNAMIC, readOnly = false) Object returnValue,
        @Advice.Thrown Throwable throwable) {
      if (scope == null) {
        return;
      }
      scope.close();
      returnValue = operationEndSupport.asyncEnd(context, methodRequest, returnValue, throwable);
    }
  }
}
