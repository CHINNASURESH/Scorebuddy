package com.scorebuddy.app.logic

import org.junit.Test
import org.junit.Assert.*

class ScoringEngineTest {
    @Test
    fun testSinglesScoring() {
        val engine = ScoringEngine(
            isDoubles = false,
            team1Player1 = "P1",
            team2Player1 = "P2"
        )

        // Initial state
        var state = engine.getCurrentState()
        assertEquals(0, state.team1Score)
        assertEquals(0, state.team2Score)
        assertEquals("Right", state.serverPosition) // Even (0) -> Right

        // P1 scores
        engine.team1Scores()
        state = engine.getCurrentState()
        assertEquals(1, state.team1Score)
        assertEquals("Left", state.serverPosition) // Odd (1) -> Left

        // P2 scores (Service change)
        engine.team2Scores()
        state = engine.getCurrentState()
        assertEquals(1, state.team2Score)
        assertEquals(2, state.servingTeam)
        // Serving team 2 score is 1 (Odd) -> Left
        assertEquals("Left", state.serverPosition)
    }

    @Test
    fun testDoublesScoring() {
        val engine = ScoringEngine(
            isDoubles = true,
            team1Player1 = "T1P1",
            team1Player2 = "T1P2",
            team2Player1 = "T2P1",
            team2Player2 = "T2P2"
        )

        // Initial: T1 serving. Score 0-0. Even -> Right.
        // T1P1 is on Right (Default). T1P2 on Left.
        var state = engine.getCurrentState()
        assertEquals("Right", state.serverPosition)
        assertEquals("T1P1", state.activeServerPlayerId)

        // T1 scores. 1-0. Odd -> Left.
        // T1P1 was serving and won. Swap positions.
        // T1P1 moves to Left. T1P2 moves to Right.
        // Server should be person on Left. Which is T1P1.
        engine.team1Scores()
        state = engine.getCurrentState()
        assertEquals(1, state.team1Score)
        assertEquals("Left", state.serverPosition)
        assertEquals("T1P1", state.activeServerPlayerId)

        // T2 scores. Service change. 1-1.
        // T1 positions stay same (P1 Left, P2 Right).
        // T2 becomes serving team. Score 1. Odd -> Left.
        // T2P1 is on Right, T2P2 is on Left (Default).
        // Server should be T2P2.
        engine.team2Scores()
        state = engine.getCurrentState()
        assertEquals(1, state.team2Score)
        assertEquals(2, state.servingTeam)
        assertEquals("Left", state.serverPosition)
        assertEquals("T2P2", state.activeServerPlayerId)
    }
}
