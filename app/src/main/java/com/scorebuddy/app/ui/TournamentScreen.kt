package com.scorebuddy.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.scorebuddy.app.data.DataRepository
import com.scorebuddy.app.logic.TournamentManager

@Composable
fun TournamentScreen(onBack: () -> Unit) {
    val tournaments by DataRepository.tournaments.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    if (showCreateDialog) {
        CreateTournamentDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { name, players ->
                val t = TournamentManager.createTournament(name, players)
                DataRepository.addTournament(t)
                showCreateDialog = false
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Tournaments", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Button(onClick = onBack) { Text("Back") }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = { showCreateDialog = true }) { Text("Create New") }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(tournaments) { t ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(t.name, style = MaterialTheme.typography.titleLarge)
                        Text("Participants: ${t.participants.size}")
                        Text("Rounds: ${t.rounds.size}")
                        // Show first round matches
                        t.rounds.firstOrNull()?.let { round ->
                            Text("Round 1 Matches:", style = MaterialTheme.typography.titleSmall)
                            round.matches.forEach { m ->
                                Text("${m.player1Id ?: "Bye"} vs ${m.player2Id ?: "Bye"}")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CreateTournamentDialog(onDismiss: () -> Unit, onCreate: (String, List<String>) -> Unit) {
    var name by remember { mutableStateOf("") }
    var playersInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Tournament") },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Tournament Name") })
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = playersInput,
                    onValueChange = { playersInput = it },
                    label = { Text("Player Names (comma separated)") },
                    minLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val players = playersInput.split(",").map { it.trim() }.filter { it.isNotBlank() }
                    if (name.isNotBlank() && players.size >= 2) {
                        onCreate(name, players)
                    }
                }
            ) { Text("Create") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
