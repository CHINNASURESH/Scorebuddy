package com.scorebuddy.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.scorebuddy.app.data.DataRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(onBack: () -> Unit) {
    val matches by DataRepository.matches.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Match History", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onBack) {
            Text("Back")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(matches) { match ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        val date = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(Date(match.date))
                        Text("Date: $date", style = MaterialTheme.typography.bodySmall)
                        Text(
                            if (match.isDoubles)
                                "${match.player1Id}/${match.player3Id} vs ${match.player2Id}/${match.player4Id}"
                            else
                                "${match.player1Id} vs ${match.player2Id}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text("Score: ${match.score}")
                        Text("Winner: ${match.winnerId ?: "Draw"}")
                    }
                }
            }
        }
    }
}
