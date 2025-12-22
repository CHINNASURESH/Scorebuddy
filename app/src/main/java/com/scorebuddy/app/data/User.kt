package com.scorebuddy.app.data

data class User(
    val id: String,
    val username: String,
    val gamesPlayed: Int = 0,
    val gamesWon: Int = 0
)
