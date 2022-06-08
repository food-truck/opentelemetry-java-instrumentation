## Preface
* In wonder, we achieve the purpose of adapting to the core-ng framework by modifying open telemetry

## WebSocket & Redis
* For web socket and redis that we added the core-ng module to the /instrumentation package to add span to websocket and redis

### Redis
* We have assembled the get method of the class 
```
core.framework.internal.redis.RedisImpl
```
###WebSocket
* We have assembled the onMessage and onCloseMessage method of the class 
```
core.framework.internal.web.websocket.WebSocketMessageListener
```

## Undertow & OK HTTP
* In order to improve http transaction to human readable format. We modified the code in the
``` 
/opentelemetry-java-instrumentation/instrumentation-api/src/main/java/io/opentelemetry/instrumentation/api/instrumenter/http
and 
/open-telemetry/opentelemetry-java-instrumentation/instrumentation/okhttp/okhttp-3.0/library/src/main/java/io/opentelemetry/instrumentation/okhttp/v3_0/internal 
```
package and extended the ext package to resolve the conflict 

