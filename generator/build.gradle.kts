plugins {
    kotlin("jvm") version "2.1.20"
    id("com.google.devtools.ksp") version "2.1.20-1.0.32"
}

val roosterGroup: String by project
group = roosterGroup
val roosterVersion: String by project
version = roosterVersion
val javaVersion: String by project

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("com.google.devtools.ksp:symbol-processing-api:2.1.20-1.0.32")

    implementation("org.jetbrains.exposed:exposed-core:0.49.0")
    implementation(project(":"))

    //ksp("org.jetbrains.exposed:exposed-core:0.49.0")
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    implementation("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
}

tasks.test {
    useJUnitPlatform()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(javaVersion))
    }
}

kotlin {
    jvmToolchain(javaVersion.toInt())
}