package com.example.raithavarta.domain.model

/**
 * Tri-lingual disease insight for UI (mock AI output).
 */
data class DiseaseInsight(
    val diseaseEn: String,
    val diseaseKn: String,
    val diseaseHi: String,
    val descriptionEn: String,
    val descriptionKn: String,
    val descriptionHi: String,
    val solutionEn: String,
    val solutionKn: String,
    val solutionHi: String
)
