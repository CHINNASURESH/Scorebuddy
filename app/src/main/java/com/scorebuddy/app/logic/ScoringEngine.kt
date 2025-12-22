package com.scorebuddy.app.logic

data class GameState(
    val team1Score: Int = 0,
    val team2Score: Int = 0,
    val team1Sets: Int = 0,
    val team2Sets: Int = 0,
    val isGameOver: Boolean = false,
    val winnerTeam: Int? = null,
    val servingTeam: Int = 1, // 1 or 2
    val serverPosition: String = "Right", // "Right" or "Left"
    val activeServerPlayerId: String? = null // ID of the player serving
)

class ScoringEngine(
    val isDoubles: Boolean,
    val team1Player1: String,
    val team1Player2: String? = null,
    val team2Player1: String,
    val team2Player2: String? = null
) {
    // Track who is on the Right court for each team. The other is on Left.
    private var team1RightPlayer = team1Player1
    private var team2RightPlayer = team2Player1

    private var currentState = GameState()

    fun getCurrentState() = currentState

    fun team1Scores() {
        if (currentState.isGameOver) return

        val newScore = currentState.team1Score + 1
        var team1Sets = currentState.team1Sets
        var team2Sets = currentState.team2Sets
        var isGameOver = false
        var winner: Int? = null
        var servingTeam = 1

        // Check for set win
        // Simplified: Win set if score >= 21 and lead >= 2, or score == 30
        val wonSet = (newScore >= 21 && (newScore - currentState.team2Score) >= 2) || newScore == 30

        if (wonSet) {
            team1Sets++
            if (team1Sets >= 2) { // Best of 3
                isGameOver = true
                winner = 1
            }
            // If set won but game not over, reset scores
            if (!isGameOver) {
                currentState = currentState.copy(
                    team1Score = 0, team2Score = 0,
                    team1Sets = team1Sets, team2Sets = team2Sets,
                    servingTeam = 1 // Winner of set serves
                )
                // Reset positions? Rules say yes usually, or they stay?
                // Let's assume standard reset to initial positions isn't strictly enforced by app,
                // but for highlighting we keep tracking.
                updateServerHighlight()
                return
            }
        }

        // Doubles rotation logic
        if (isDoubles && !wonSet) {
             if (currentState.servingTeam == 1) {
                 // Team 1 was serving and won. Swap positions.
                 // Who swaps? The team that scored.
                 team1RightPlayer = if (team1RightPlayer == team1Player1) team1Player2!! else team1Player1
             }
             // If Team 2 was serving and lost, Team 1 becomes server. No positional swap.
        }

        currentState = currentState.copy(
            team1Score = if (isGameOver) currentState.team1Score else newScore,
            team1Sets = team1Sets,
            isGameOver = isGameOver,
            winnerTeam = winner,
            servingTeam = 1
        )
        updateServerHighlight()
    }

    fun team2Scores() {
        if (currentState.isGameOver) return

        val newScore = currentState.team2Score + 1
        var team1Sets = currentState.team1Sets
        var team2Sets = currentState.team2Sets
        var isGameOver = false
        var winner: Int? = null

        val wonSet = (newScore >= 21 && (newScore - currentState.team1Score) >= 2) || newScore == 30

        if (wonSet) {
            team2Sets++
            if (team2Sets >= 2) {
                isGameOver = true
                winner = 2
            }
             if (!isGameOver) {
                 currentState = currentState.copy(
                     team1Score = 0, team2Score = 0,
                     team1Sets = team1Sets, team2Sets = team2Sets,
                     servingTeam = 2
                 )
                 updateServerHighlight()
                 return
             }
        }

        if (isDoubles && !wonSet) {
             if (currentState.servingTeam == 2) {
                 // Team 2 was serving and won. Swap positions.
                 team2RightPlayer = if (team2RightPlayer == team2Player1) team2Player2!! else team2Player1
             }
        }

        currentState = currentState.copy(
            team2Score = if (isGameOver) currentState.team2Score else newScore,
            team2Sets = team2Sets,
            isGameOver = isGameOver,
            winnerTeam = winner,
            servingTeam = 2
        )
        updateServerHighlight()
    }

    private fun updateServerHighlight() {
        if (currentState.isGameOver) return

        val servingTeamScore = if (currentState.servingTeam == 1) currentState.team1Score else currentState.team2Score
        val isEven = servingTeamScore % 2 == 0
        val position = if (isEven) "Right" else "Left"

        val serverId = if (currentState.servingTeam == 1) {
            if (isDoubles) {
                // If even score, player in Right court serves.
                if (isEven) team1RightPlayer else (if (team1RightPlayer == team1Player1) team1Player2 else team1Player1)
            } else {
                team1Player1
            }
        } else {
             if (isDoubles) {
                if (isEven) team2RightPlayer else (if (team2RightPlayer == team2Player1) team2Player2 else team2Player1)
            } else {
                team2Player1
            }
        }

        currentState = currentState.copy(
            serverPosition = position,
            activeServerPlayerId = serverId
        )
    }

    init {
        updateServerHighlight()
    }
}
