plugins {
    kotlin("jvm")
}

val roosterGroup: String by project
group = roosterGroup
val roosterVersion: String by project
version = roosterVersion
val javaVersion: String by project

repositories {
    mavenCentral()
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
}

dependencies {
    testImplementation(kotlin("test"))

    implementation("com.github.seeseemelk:MockBukkit-v1.21:3.127.1")
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    implementation(project(":"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(javaVersion.toInt())
}