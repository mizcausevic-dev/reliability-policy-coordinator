package com.mizcausevic.reliability

fun main() {
    val port = System.getenv("PORT")?.toIntOrNull() ?: 4068
    val app = ReliabilityPolicyCoordinatorApp.create()
    app.start("127.0.0.1", port)
}
