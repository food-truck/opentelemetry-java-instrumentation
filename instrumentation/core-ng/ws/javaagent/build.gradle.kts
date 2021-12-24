plugins {
  id("otel.javaagent-instrumentation")
}

muzzle {
  pass {
    coreJdk()
  }
}

dependencies {
  compileOnly(project(":instrumentation-api-annotation-support"))
  implementation("core.framework:core-ng:7.6.15")
}