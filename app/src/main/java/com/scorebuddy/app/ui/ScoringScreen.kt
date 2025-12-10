package com.scorebuddy.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scorebuddy.app.data.DataRepository
import com.scorebuddy.app.data.Match
import com.scorebuddy.app.logic.ScoringEngine
import java.util.UUID

@Composable
fun ScoringScreen(onMatchFinished: () -> Unit) {
    var isSetup by remember { mutableStateOf(true) }
    var isDoubles by remember { mutableStateOf(false) }
    var t1p1 by remember { mutableStateOf("") }
    var t1p2 by remember { mutableStateOf("") }
    var t2p1 by remember { mutableStateOf("") }
    var t2p2 by remember { mutableStateOf("") }

    var engine by remember { mutableStateOf<ScoringEngine?>(null) }
    var gameState by remember { mutableStateOf<com.scorebuddy.app.logic.GameState?>(null) }

    if (isSetup) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Match Setup", style = MaterialTheme.typography.headlineMedium)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = isDoubles, onCheckedChange = { isDoubles = it })
                Text("Doubles Match")
            }

            OutlinedTextField(value = t1p1, onValueChange = { t1p1 = it }, label = { Text("Team 1 Player 1") }, modifier = Modifier.fillMaxWidth())
            if (isDoubles) {
                OutlinedTextField(value = t1p2, onValueChange = { t1p2 = it }, label = { Text("Team 1 Player 2") }, modifier = Modifier.fillMaxWidth())
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = t2p1, onValueChange = { t2p1 = it }, label = { Text("Team 2 Player 1") }, modifier = Modifier.fillMaxWidth())
            if (isDoubles) {
                OutlinedTextField(value = t2p2, onValueChange = { t2p2 = it }, label = { Text("Team 2 Player 2") }, modifier = Modifier.fillMaxWidth())
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    engine = ScoringEngine(isDoubles, t1p1, if(isDoubles) t1p2 else null, t2p1, if(isDoubles) t2p2 else null)
                    gameState = engine!!.getCurrentState()
                    isSetup = false
                },
                enabled = t1p1.isNotBlank() && t2p1.isNotBlank() && (!isDoubles || (t1p2.isNotBlank() && t2p2.isNotBlank())),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Start Match")
            }
        }
    } else {
        val state = gameState!!
        if (state.isGameOver) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Game Over!", style = MaterialTheme.typography.headlineLarge)
                Text("Winner: Team ${state.winnerTeam}", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = {
                     // Save match
                     val match = Match(
                         id = UUID.randomUUID().toString(),
                         player1Id = t1p1,
                         player2Id = t2p1,
                         player3Id = if(isDoubles) t1p2 else null,
                         player4Id = if(isDoubles) t2p2 else null,
                         score = "Sets: ${state.team1Sets}-${state.team2Sets} (Last: ${state.team1Score}-${state.team2Score})",
                         winnerId = if (state.winnerTeam == 1) "Team 1" else "Team 2",
                         isDoubles = isDoubles
                     )
                     DataRepository.addMatch(match)
                     onMatchFinished()
                }) {
                    Text("Save & Exit")
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                // Scoreboard
                Row(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Team 1
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(if (state.servingTeam == 1) Color(0xFFE3F2FD) else Color.Transparent)
                            .clickable {
                                engine!!.team1Scores()
                                gameState = engine!!.getCurrentState()
                            },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Team 1", style = MaterialTheme.typography.titleLarge)
                        Text("${state.team1Score}", style = MaterialTheme.typography.displayLarge)
                        Text("Sets: ${state.team1Sets}")

                        // Server highlight
                        if (state.servingTeam == 1) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Serving: ${state.serverPosition}")
                            Text("Server: ${state.activeServerPlayerId ?: ""}", color = Color.Red, style = MaterialTheme.typography.bodyLarge)
                        }
                    }

                    Divider(modifier = Modifier.width(1.dp).fillMaxHeight())

                    // Team 2
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(if (state.servingTeam == 2) Color(0xFFE3F2FD) else Color.Transparent)
                            .clickable {
                                engine!!.team2Scores()
                                gameState = engine!!.getCurrentState()
                            },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Team 2", style = MaterialTheme.typography.titleLarge)
                        Text("${state.team2Score}", style = MaterialTheme.typography.displayLarge)
                        Text("Sets: ${state.team2Sets}")

                        if (state.servingTeam == 2) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Serving: ${state.serverPosition}")
                            Text("Server: ${state.activeServerPlayerId ?: ""}", color = Color.Red, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        }
    }
}
