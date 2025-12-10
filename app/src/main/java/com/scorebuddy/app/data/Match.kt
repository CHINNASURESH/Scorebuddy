package com.scorebuddy.app.data

data class Match(
    val id: String,
    val player1Id: String,
    val player2Id: String,
    val player3Id: String? = null, // For doubles
    val player4Id: String? = null, // For doubles
    val score: String, // E.g., "21-19, 15-21, 21-10"
    val date: Long = System.currentTimeMillis(),
    val winnerId: String? = null,
    val isDoubles: Boolean = false
)
