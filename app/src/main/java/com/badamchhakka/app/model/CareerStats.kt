package com.badamchhakka.app.model

data class RoundResult(
    val roundNumber: Int,
    val winnerIndex: Int,
    val roundPenaltyPoints: List<Int>,
    val playerScores: List<Int>,
    val playerCardCounts: List<Int>,
    val leftoverCards: List<List<Card>>
)

data class CareerStats(
    val totalRoundsPlayed: Int = 0,
    val totalRoundsWon: Int = 0,
    val totalPenaltyPoints: Int = 0,
    val bestRoundPenalty: Int? = null,
    val cleanWins: Int = 0,
    val sixHeartOpenings: Int = 0
)
