
plugins {
    kotlin("jvm") version "2.3.10"
    application
}

group = "me.totxy"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}
kotlin {
    jvmToolchain(25)
}


dependencies {
    implementation("net.minestom:minestom:2026.03.03-1.21.11")
    implementation("com.mysql:mysql-connector-j:9.2.0")
}

application {
    mainClass.set("me.totxy.MainKt")
}

tasks.test {
    useJUnitPlatform()
}