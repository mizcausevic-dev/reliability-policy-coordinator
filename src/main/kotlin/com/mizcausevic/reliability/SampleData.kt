package com.mizcausevic.reliability

object SampleData {
    fun incidents(): List<ReliabilityIncident> =
        listOf(
            ReliabilityIncident(
                id = "rel-7102",
                title = "Northstar checkout path is burning budget faster than the rollback lane can absorb",
                service = "checkout-runtime",
                severity = "critical",
                sourceLane = "platform",
                targetLane = "revenue-systems",
                dependencyDrag = 4,
                errorBudgetBurn = 0.92,
                freezeWindowHours = 1.5,
                rollbackReady = false,
                confidence = 0.73,
                blockers = listOf(
                    "Cache invalidation handoff is split across two owners",
                    "EU traffic lane still routes through the degraded dependency"
                ),
                timeline = listOf(
                    "18:05 ET - Tail latency crossed p99 threshold",
                    "18:18 ET - Retry pressure started eating error budget",
                    "18:31 ET - GTM leadership asked for an answer before campaign send"
                ),
                nextSteps = listOf(
                    "Freeze the release lane and shift traffic to the resilient fallback path",
                    "Assign a single rollback owner before the next freeze window closes"
                )
            ),
            ReliabilityIncident(
                id = "rel-7110",
                title = "Identity sync worker is colliding with a regional freeze window",
                service = "identity-sync",
                severity = "high",
                sourceLane = "security-ops",
                targetLane = "platform",
                dependencyDrag = 3,
                errorBudgetBurn = 0.61,
                freezeWindowHours = 4.0,
                rollbackReady = true,
                confidence = 0.81,
                blockers = listOf(
                    "One regional tenant still depends on the old queue contract"
                ),
                timeline = listOf(
                    "11:14 ET - Sync lag breached security expectation",
                    "12:03 ET - Freeze window was shortened for regional maintenance"
                ),
                nextSteps = listOf(
                    "Keep rollback ready and route the remaining tenants through the legacy adapter"
                )
            ),
            ReliabilityIncident(
                id = "rel-7119",
                title = "Observability exporter is noisy but still recoverable without a freeze",
                service = "observability-exporter",
                severity = "medium",
                sourceLane = "sre",
                targetLane = "platform",
                dependencyDrag = 1,
                errorBudgetBurn = 0.29,
                freezeWindowHours = 12.0,
                rollbackReady = true,
                confidence = 0.9,
                blockers = listOf("Dashboard panel cache is still stale"),
                timeline = listOf(
                    "09:20 ET - Export lag alert triggered",
                    "09:47 ET - Queue pressure normalized after worker restart"
                ),
                nextSteps = listOf(
                    "Hold the lane in watch mode and recheck the panel cache after one ingest cycle"
                )
            )
        )
}
