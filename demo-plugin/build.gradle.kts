plugins {
    kotlin("jvm")
    //id("com.google.devtools.ksp") version "2.1.20-1.0.31"
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

    implementation(project(":generator"))
    //ksp(project(":generator"))

    implementation(project(":"))
    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
    implementation("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}