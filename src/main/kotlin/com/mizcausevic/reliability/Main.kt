package com.mizcausevic.reliability

import io.javalin.util.JavalinBindException
import kotlin.system.exitProcess

fun main() {
    val port = System.getenv("PORT")?.toIntOrNull() ?: 4286
    val app = ReliabilityPolicyCoordinatorApp.create()
    try {
        app.start("127.0.0.1", port)
    } catch (exception: JavalinBindException) {
        println("Reliability Policy Coordinator could not start because port $port is already in use.")
        println("Set a different port before running again, for example:")
        println("${'$'}env:PORT = \"4290\"")
        println(".\\gradlew.bat run")
        app.stop()
        exitProcess(1)
    }
}
