plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp") version "2.1.20-1.0.32"

    id("xyz.jpenilla.run-paper") version "2.3.1"
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

    implementation(project(":generator"))
    ksp(project(":generator"))

    implementation(project(":"))
    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
    implementation("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(javaVersion.toInt())
}