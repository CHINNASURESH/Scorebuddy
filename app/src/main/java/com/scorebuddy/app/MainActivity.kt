package com.scorebuddy.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.scorebuddy.app.ui.HistoryScreen
import com.scorebuddy.app.ui.HomeScreen
import com.scorebuddy.app.ui.LoginScreen
import com.scorebuddy.app.ui.ScoringScreen
import com.scorebuddy.app.ui.TournamentScreen
import com.scorebuddy.app.ui.theme.ScorebuddyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScorebuddyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ScorebuddyApp()
                }
            }
        }
    }
}

@Composable
fun ScorebuddyApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(onLoginSuccess = {
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
            })
        }
        composable("home") {
            HomeScreen(
                onNavigateToScoring = { navController.navigate("scoring") },
                onNavigateToHistory = { navController.navigate("history") },
                onNavigateToTournament = { navController.navigate("tournament") },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
        composable("scoring") {
            ScoringScreen(onMatchFinished = {
                navController.popBackStack()
            })
        }
        composable("history") {
            HistoryScreen(onBack = {
                navController.popBackStack()
            })
        }
        composable("tournament") {
            TournamentScreen(onBack = {
                navController.popBackStack()
            })
        }
    }
}
