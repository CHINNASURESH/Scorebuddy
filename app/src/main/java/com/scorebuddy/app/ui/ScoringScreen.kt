package com.scorebuddy.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.scorebuddy.app.data.DataRepository
import com.scorebuddy.app.data.Match
import java.util.UUID

@Composable
fun ScoringScreen(
    onMatchFinished: () -> Unit,
    viewModel: ScoringViewModel = viewModel()
) {
    if (viewModel.isSetup) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Match Setup", style = MaterialTheme.typography.headlineMedium)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = viewModel.isDoubles, onCheckedChange = { viewModel.isDoubles = it })
                Text("Doubles Match")
            }

            OutlinedTextField(value = viewModel.t1p1, onValueChange = { viewModel.t1p1 = it }, label = { Text("Team 1 Player 1") }, modifier = Modifier.fillMaxWidth())
            if (viewModel.isDoubles) {
                OutlinedTextField(value = viewModel.t1p2, onValueChange = { viewModel.t1p2 = it }, label = { Text("Team 1 Player 2") }, modifier = Modifier.fillMaxWidth())
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = viewModel.t2p1, onValueChange = { viewModel.t2p1 = it }, label = { Text("Team 2 Player 1") }, modifier = Modifier.fillMaxWidth())
            if (viewModel.isDoubles) {
                OutlinedTextField(value = viewModel.t2p2, onValueChange = { viewModel.t2p2 = it }, label = { Text("Team 2 Player 2") }, modifier = Modifier.fillMaxWidth())
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { viewModel.startMatch() },
                enabled = viewModel.t1p1.isNotBlank() && viewModel.t2p1.isNotBlank() && (!viewModel.isDoubles || (viewModel.t1p2.isNotBlank() && viewModel.t2p2.isNotBlank())),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Start Match")
            }
        }
    } else {
        val state = viewModel.gameState!!
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
                         player1Id = viewModel.t1p1,
                         player2Id = viewModel.t2p1,
                         player3Id = if(viewModel.isDoubles) viewModel.t1p2 else null,
                         player4Id = if(viewModel.isDoubles) viewModel.t2p2 else null,
                         score = "Sets: ${state.team1Sets}-${state.team2Sets} (Last: ${state.team1Score}-${state.team2Score})",
                         winnerId = if (state.winnerTeam == 1) "Team 1" else "Team 2",
                         isDoubles = viewModel.isDoubles
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
                            .clickable { viewModel.team1Scores() },
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

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .fillMaxHeight()
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    )

                    // Team 2
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(if (state.servingTeam == 2) Color(0xFFE3F2FD) else Color.Transparent)
                            .clickable { viewModel.team2Scores() },
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
