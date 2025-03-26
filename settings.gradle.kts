plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "Rooster"
include("simulator")
include("worldedit")
include("generator")
include("demo-plugin")
