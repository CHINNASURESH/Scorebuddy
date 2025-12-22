package com.scorebuddy.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.scorebuddy.app.data.DataRepository

@Composable
fun HomeScreen(
    onNavigateToScoring: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToTournament: () -> Unit,
    onLogout: () -> Unit
) {
    val user by DataRepository.currentUser.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Hello, ${user?.username ?: "Guest"}", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onNavigateToScoring,
            modifier = Modifier.fillMaxWidth().height(60.dp)
        ) {
            Text("New Match")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onNavigateToTournament,
            modifier = Modifier.fillMaxWidth().height(60.dp)
        ) {
            Text("Tournaments")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onNavigateToHistory,
            modifier = Modifier.fillMaxWidth().height(60.dp)
        ) {
            Text("Match History")
        }

        Spacer(modifier = Modifier.weight(1f))

        TextButton(onClick = {
            DataRepository.logout()
            onLogout()
        }) {
            Text("Logout")
        }
    }
}
