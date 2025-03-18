plugins {
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.serialization") version "1.9.23"
    `maven-publish`
}

group = "de.cypdashuhn"
description = "Rooster Framework"
val roosterVersion: String by project
version = roosterVersion
val javaVersion: String by project

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://jitpack.io")
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:1.9.23")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.10.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")

    implementation("org.reflections:reflections:0.9.12")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.7.2")
    implementation("com.google.code.gson:gson:2.11.0")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")

    implementation("io.github.classgraph:classgraph:4.8.170")

    implementation("org.jetbrains.exposed:exposed-core:0.49.0")
    implementation("org.jetbrains.exposed:exposed-crypt:0.49.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.49.0")
    implementation("org.jetbrains.exposed:exposed-java-time:0.49.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.49.0")
    implementation("org.jetbrains.exposed:exposed-jodatime:0.49.0")
    implementation("org.jetbrains.exposed:exposed-json:0.49.0")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:0.49.0")
    implementation("org.jetbrains.exposed:exposed-money:0.49.0")

    implementation("org.xerial:sqlite-jdbc:3.45.2.0")

    implementation("net.kyori:adventure-api:4.17.0")
    implementation("com.github.seeseemelk:MockBukkit-v1.21:3.127.1")

    // Uncomment when ready to use AnvilGUI
    // implementation("net.wesjd:anvilgui:1.9.4-SNAPSHOT")
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

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            groupId = group as String
            artifactId = "Rooster"
            version = version
        }
    }
    repositories {
        mavenLocal()
    }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}
tasks.withType<Javadoc>() {
    options.encoding = "UTF-8"
}