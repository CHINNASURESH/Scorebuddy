package com.scorebuddy.app.data

data class Tournament(
    val id: String,
    val name: String,
    val participants: List<String>, // User IDs
    val rounds: List<Round> = emptyList(),
    val isCompleted: Boolean = false
)

data class Round(
    val roundNumber: Int,
    val matches: List<MatchFixture>
)

data class MatchFixture(
    val id: String,
    val player1Id: String?,
    val player2Id: String?,
    val winnerId: String? = null
)
