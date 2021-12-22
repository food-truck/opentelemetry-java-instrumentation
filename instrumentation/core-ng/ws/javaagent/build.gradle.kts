plugins {
  id("otel.javaagent-instrumentation")
}

muzzle {
  pass {
    coreJdk()
  }
}

dependencies {
  library("io.undertow:undertow-core:2.2.9.Final")
  implementation("core.framework:core-ng:7.6.15")
}