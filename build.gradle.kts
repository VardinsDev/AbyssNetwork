plugins {
    kotlin("jvm") version "2.3.10"
}

group = "me.totxy"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("net.minestom:minestom:2026.03.03-1.21.11")
}

tasks.test {
    useJUnitPlatform()
}