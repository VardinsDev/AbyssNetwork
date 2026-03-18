
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
    implementation("org.slf4j:slf4j-simple:2.0.13")
    implementation("rocks.minestom:placement:0.1.0")
}

application {
    mainClass.set("me.totxy.MainKt")
    applicationDefaultJvmArgs = listOf(
        "--add-opens=java.base/java.lang=ALL-UNNAMED",
        "-XX:+EnableDynamicAgentLoading",
        "-Djdk.reflect.useDirectMethodHandle=false"
    )
}

tasks.test {
    useJUnitPlatform()
}