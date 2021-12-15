package io.opentelemetry.javaagent.instrumentation.redis;

import static io.opentelemetry.javaagent.instrumentation.api.Java8BytecodeBridge.currentContext;
import static io.opentelemetry.javaagent.instrumentation.redis.CoreNGRedisSingletons.instrumenter;
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

public class CoreNGCacheInstrumentation implements TypeInstrumentation {
  @Override
  public ElementMatcher<TypeDescription> typeMatcher() {
    return named("core.framework.internal.cache.RedisCacheStore");
  }

  @Override
  public void transform(TypeTransformer transformer) {
    transformer.applyAdviceToMethod(
        isMethod()
            .and(named("get"))
            .and(takesArguments(2))
            .and(takesArgument(0, is(String.class)))
            .and(takesArgument(1, named("core.framework.internal.cache.CacheContext"))),
        this.getClass().getName() + "$RedisCacheSpan"
    );
  }

  public static class RedisCacheSpan {
    @Advice.OnMethodEnter(suppress = Throwable.class)
    public static void onEnter(
        @Advice.Argument(0) String key,
        @Advice.Local("otelContext") Context context,
        @Advice.Local("otelScope") Scope scope) {
      RedisRequest request = new RedisRequestBuilder(key, RedisRequest.Type.REDIS_CACHE).build();
      Context parentContext = currentContext();
      if (!instrumenter().shouldStart(parentContext, request)) {
        return;
      }

      context = instrumenter().start(parentContext, request);
      scope = context.makeCurrent();
    }

    @Advice.OnMethodExit(onThrowable = Throwable.class, suppress = Throwable.class)
    public static void stopSpan(
        @Advice.Argument(0) String key,
        @Advice.Thrown Throwable throwable,
        @Advice.Local("otelContext") Context context,
        @Advice.Local("otelScope") Scope scope) {
      RedisRequest request = new RedisRequestBuilder(key, RedisRequest.Type.REDIS_CACHE).build();
      if (scope == null) {
        return;
      }
      scope.close();
      instrumenter().end(context, request, null, throwable);
    }
  }
}
