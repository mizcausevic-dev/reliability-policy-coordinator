plugins {
    kotlin("jvm") version "2.0.21"
    application
}

group = "com.mizcausevic"
version = "0.1.0"

repositories {
    mavenCentral()
}

val logbackVersion = "1.5.18"
val javalinVersion = "6.6.0"
val jacksonVersion = "2.19.1"

dependencies {
    implementation("io.javalin:javalin:$javalinVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.12.2")
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("com.mizcausevic.reliability.MainKt")
}

tasks.test {
    useJUnitPlatform()
}
