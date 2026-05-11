package com.mizcausevic.reliability

data class ReliabilityIncident(
    val id: String,
    val title: String,
    val service: String,
    val severity: String,
    val sourceLane: String,
    val targetLane: String,
    val dependencyDrag: Int,
    val errorBudgetBurn: Double,
    val freezeWindowHours: Double,
    val rollbackReady: Boolean,
    val confidence: Double,
    val blockers: List<String>,
    val timeline: List<String>,
    val nextSteps: List<String>
)

data class SummaryResponse(
    val service: String,
    val openIncidents: Int,
    val policyBreaches: Int,
    val freezeWindowConflicts: Int,
    val rollbackReadyCount: Int,
    val averageConfidence: Double
)

data class IncidentCollection(val incidents: List<ReliabilityIncident>)

data class PolicyAnalysisRequest(
    val id: String? = null,
    val title: String,
    val service: String,
    val severity: String,
    val sourceLane: String,
    val targetLane: String,
    val dependencyDrag: Int,
    val errorBudgetBurn: Double,
    val freezeWindowHours: Double,
    val rollbackReady: Boolean,
    val confidence: Double,
    val blockers: List<String> = emptyList(),
    val nextSteps: List<String> = emptyList()
)

data class PolicyAnalysisResponse(
    val incidentId: String?,
    val status: String,
    val riskScore: Int,
    val ownerLane: String,
    val policyAction: String,
    val briefing: String,
    val freezeRecommendation: String
)
