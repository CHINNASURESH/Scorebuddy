package com.scorebuddy.app.logic

import com.scorebuddy.app.data.Tournament
import com.scorebuddy.app.data.Round
import com.scorebuddy.app.data.MatchFixture
import java.util.UUID

object TournamentManager {
    fun createTournament(name: String, playerIds: List<String>): Tournament {
        val shuffled = playerIds.shuffled()
        val count = shuffled.size
        var size = 1
        while (size < count) size *= 2

        // Pad with nulls (Byes)
        val participants = shuffled.toMutableList<String?>()
        while (participants.size < size) {
            participants.add(null)
        }

        // Create Round 1
        val matches = mutableListOf<MatchFixture>()
        for (i in 0 until size step 2) {
            val p1 = participants[i]
            val p2 = participants[i+1]
            // If one is null, the other wins automatically
            val winner = if (p2 == null) p1 else if (p1 == null) p2 else null

            matches.add(MatchFixture(
                id = UUID.randomUUID().toString(),
                player1Id = p1,
                player2Id = p2,
                winnerId = winner
            ))
        }

        val round1 = Round(1, matches)

        return Tournament(
            id = UUID.randomUUID().toString(),
            name = name,
            participants = playerIds,
            rounds = listOf(round1)
        )
    }
}
