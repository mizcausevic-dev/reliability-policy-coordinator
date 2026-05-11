package com.mizcausevic.reliability

import io.javalin.Javalin
import io.javalin.json.JavalinJackson

object ReliabilityPolicyCoordinatorApp {
    fun create(): Javalin =
        Javalin.create { config ->
            config.http.defaultContentType = "application/json"
            config.jsonMapper(JavalinJackson())
            config.router.apiBuilder {
            }
        }.apply {
            get("/") { ctx ->
                ctx.json(
                    mapOf(
                        "service" to "reliability-policy-coordinator",
                        "language" to "Kotlin",
                        "framework" to "Javalin",
                        "description" to "Reliability policy analysis for dependency drag, freeze windows, and rollback pressure.",
                        "endpoints" to listOf(
                            "/docs",
                            "/api/dashboard/summary",
                            "/api/incidents",
                            "/api/incidents/{id}",
                            "/api/sample",
                            "/api/analyze/policy"
                        )
                    )
                )
            }

            get("/docs") { ctx ->
                ctx.contentType("text/html")
                ctx.result(
                    """
                    <!doctype html>
                    <html lang="en">
                      <head>
                        <meta charset="utf-8" />
                        <title>Reliability Policy Coordinator Docs</title>
                        <style>
                          body { font-family: Segoe UI, sans-serif; background:#09121f; color:#f3efe1; margin:0; padding:32px; }
                          .shell { max-width:960px; margin:0 auto; background:#131d30; border:1px solid #294164; border-radius:20px; padding:28px; }
                          h1 { margin:0 0 8px; font-size:40px; }
                          p, li, code { color:#c6d0e2; }
                          code { background:#0d1728; padding:2px 6px; border-radius:6px; }
                        </style>
                      </head>
                      <body>
                        <div class="shell">
                          <p style="letter-spacing:0.25em;text-transform:uppercase;color:#86c4ff;">Reliability Policy Coordinator</p>
                          <h1>Kotlin control surface for dependency drag, freeze pressure, and rollback guidance.</h1>
                          <p>This service turns reliability signals into owner-aware policy decisions instead of leaving them trapped in dashboards.</p>
                          <ul>
                            <li><code>GET /api/dashboard/summary</code> returns queue posture.</li>
                            <li><code>GET /api/incidents</code> returns the modeled reliability incidents.</li>
                            <li><code>GET /api/sample</code> returns a sample analysis.</li>
                            <li><code>POST /api/analyze/policy</code> scores a payload and returns the next action.</li>
                          </ul>
                        </div>
                      </body>
                    </html>
                    """.trimIndent()
                )
            }

            get("/api/dashboard/summary") { ctx ->
                ctx.json(ReliabilityPolicyEngine.summary())
            }

            get("/api/incidents") { ctx ->
                ctx.json(IncidentCollection(ReliabilityPolicyEngine.incidents()))
            }

            get("/api/incidents/{id}") { ctx ->
                val incidentId = ctx.pathParam("id")
                val incident = ReliabilityPolicyEngine.incident(incidentId)
                if (incident == null) {
                    ctx.status(404).json(mapOf("error" to "incident_not_found", "id" to incidentId))
                } else {
                    ctx.json(incident)
                }
            }

            get("/api/sample") { ctx ->
                ctx.json(ReliabilityPolicyEngine.sampleAnalysis())
            }

            post("/api/analyze/policy") { ctx ->
                val payload = ctx.bodyAsClass(PolicyAnalysisRequest::class.java)
                ctx.json(ReliabilityPolicyEngine.analyze(payload))
            }
        }
}
