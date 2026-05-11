package com.mizcausevic.reliability

object ReliabilityPolicyEngine {
    private val severityWeight = mapOf(
        "low" to 10,
        "medium" to 24,
        "high" to 46,
        "critical" to 68
    )

    fun incidents(): List<ReliabilityIncident> = SampleData.incidents()

    fun incident(id: String): ReliabilityIncident? = incidents().firstOrNull { it.id == id }

    fun summary(): SummaryResponse {
        val incidents = incidents()
        return SummaryResponse(
            service = "reliability-policy-coordinator",
            openIncidents = incidents.size,
            policyBreaches = incidents.count { analyze(it.toRequest()).status == "escalate" },
            freezeWindowConflicts = incidents.count { it.freezeWindowHours <= 4.0 },
            rollbackReadyCount = incidents.count { it.rollbackReady },
            averageConfidence = "%.2f".format(incidents.map { it.confidence }.average()).toDouble()
        )
    }

    fun sampleAnalysis(): PolicyAnalysisResponse = analyze(incidents().first().toRequest())

    fun analyze(request: PolicyAnalysisRequest): PolicyAnalysisResponse {
        val riskScore = (
            severityWeight.getValue(request.severity.lowercase()) +
                dependencyPenalty(request.dependencyDrag) +
                burnPenalty(request.errorBudgetBurn) +
                freezePenalty(request.freezeWindowHours) +
                rollbackPenalty(request.rollbackReady) +
                blockerPenalty(request.blockers.size) +
                confidencePenalty(request.confidence)
            ).coerceAtMost(100)

        val status = when {
            riskScore >= 80 -> "escalate"
            riskScore >= 50 -> "watch"
            else -> "stable"
        }

        return PolicyAnalysisResponse(
            incidentId = request.id,
            status = status,
            riskScore = riskScore,
            ownerLane = "${request.targetLane}-lead",
            policyAction = policyAction(request, status),
            briefing = briefing(request, status),
            freezeRecommendation = freezeRecommendation(request, status)
        )
    }

    private fun dependencyPenalty(value: Int): Int = (value * 6).coerceAtMost(24)

    private fun burnPenalty(burn: Double): Int = when {
        burn >= 0.9 -> 22
        burn >= 0.7 -> 15
        burn >= 0.5 -> 9
        else -> 0
    }

    private fun freezePenalty(hours: Double): Int = when {
        hours <= 2.0 -> 18
        hours <= 4.0 -> 12
        hours <= 8.0 -> 6
        else -> 0
    }

    private fun rollbackPenalty(rollbackReady: Boolean): Int = if (rollbackReady) 0 else 12

    private fun blockerPenalty(count: Int): Int = (count * 5).coerceAtMost(15)

    private fun confidencePenalty(confidence: Double): Int = when {
        confidence < 0.65 -> 10
        confidence < 0.8 -> 5
        else -> 0
    }

    private fun policyAction(request: PolicyAnalysisRequest, status: String): String = when (status) {
        "escalate" -> request.nextSteps.firstOrNull()
            ?: "Open the reliability bridge and pin a rollback owner immediately."
        "watch" -> "Hold the service in watch mode and confirm the dependency owner before the next freeze window."
        else -> "Keep the lane warm, recheck dependency health, and continue operator review."
    }

    private fun briefing(request: PolicyAnalysisRequest, status: String): String = when (status) {
        "escalate" -> "${request.service} is stacking error-budget pressure with dependency drag and cannot drift without a command path."
        "watch" -> "${request.service} is recoverable, but timing pressure means ownership cannot stay informal."
        else -> "${request.service} is stable enough for operator review without triggering a freeze."
    }

    private fun freezeRecommendation(request: PolicyAnalysisRequest, status: String): String = when {
        status == "escalate" && request.freezeWindowHours <= 4.0 -> "Freeze releases until rollback posture improves."
        !request.rollbackReady -> "Keep deployment pressure low until rollback readiness is restored."
        else -> "No freeze required; keep the rollback lane warm."
    }
}

private fun ReliabilityIncident.toRequest() = PolicyAnalysisRequest(
    id = id,
    title = title,
    service = service,
    severity = severity,
    sourceLane = sourceLane,
    targetLane = targetLane,
    dependencyDrag = dependencyDrag,
    errorBudgetBurn = errorBudgetBurn,
    freezeWindowHours = freezeWindowHours,
    rollbackReady = rollbackReady,
    confidence = confidence,
    blockers = blockers,
    nextSteps = nextSteps
)
