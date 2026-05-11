package com.mizcausevic.reliability

import io.javalin.Javalin
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ReliabilityPolicyCoordinatorTest {
    private var app: Javalin? = null
    private val client = HttpClient.newHttpClient()
    private val port = 5078

    @AfterTest
    fun tearDown() {
        app?.stop()
        app = null
    }

    @Test
    fun rootRouteReturnsServiceMetadata() {
        startApp()

        val response = get("/")

        assertEquals(200, response.statusCode())
        assertTrue(response.body().contains("reliability-policy-coordinator"))
    }

    @Test
    fun sampleRouteReturnsAnalysis() {
        startApp()

        val response = get("/api/sample")

        assertEquals(200, response.statusCode())
        assertTrue(response.body().contains("\"status\""))
    }

    @Test
    fun policyAnalysisEndpointScoresPayload() {
        startApp()

        val response =
            post(
                "/api/analyze/policy",
                """
                {
                  "id": "rel-x",
                  "title": "Latency wall is colliding with a release freeze",
                  "service": "checkout-runtime",
                  "severity": "critical",
                  "sourceLane": "platform",
                  "targetLane": "revenue",
                  "dependencyDrag": 4,
                  "errorBudgetBurn": 0.94,
                  "freezeWindowHours": 1.5,
                  "rollbackReady": false,
                  "confidence": 0.71,
                  "blockers": ["Cache ownership gap", "Queue retry drift"],
                  "nextSteps": ["Freeze release lane immediately"]
                }
                """.trimIndent()
            )

        assertEquals(200, response.statusCode())
        assertTrue(response.body().contains("Freeze release lane immediately"))
    }

    private fun startApp() {
        if (app == null) {
            app = ReliabilityPolicyCoordinatorApp.create().start("127.0.0.1", port)
        }
    }

    private fun get(path: String): HttpResponse<String> =
        client.send(
            HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:$port$path"))
                .GET()
                .build(),
            HttpResponse.BodyHandlers.ofString()
        )

    private fun post(path: String, json: String): HttpResponse<String> =
        client.send(
            HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:$port$path"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build(),
            HttpResponse.BodyHandlers.ofString()
        )
}
