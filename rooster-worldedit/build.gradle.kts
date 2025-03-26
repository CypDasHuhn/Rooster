plugins {
    kotlin("jvm")
}

group = "dev.cypdashuhn"
version = "0.0.1"
val javaVersion: String by project

repositories {
    mavenCentral()

    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
    maven { url = uri("https://maven.enginehub.org/repo/") }
}

dependencies {
    testImplementation(kotlin("test"))
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")

    implementation(platform("com.intellectualsites.bom:bom-newest:1.52")) // Ref: https://github.com/IntellectualSites/bom
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Core")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Bukkit") { isTransitive = false }

    implementation(project(":"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(javaVersion.toInt())
}