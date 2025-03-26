plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp")
}

group = "dev.cypdashuhn"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("com.google.devtools.ksp:symbol-processing-api:2.0.0-1.0.21")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}