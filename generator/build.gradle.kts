plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp")
}

val roosterGroup: String by project
group = roosterGroup
val roosterVersion: String by project
version = roosterVersion

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("com.google.devtools.ksp:symbol-processing-api:2.0.0-1.0.21")

    implementation("org.jetbrains.exposed:exposed-core:0.49.0")
    implementation(project(":"))

    //ksp("org.jetbrains.exposed:exposed-core:0.49.0")
    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}