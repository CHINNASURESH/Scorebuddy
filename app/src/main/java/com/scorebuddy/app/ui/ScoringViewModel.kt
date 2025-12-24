package com.scorebuddy.app.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.scorebuddy.app.logic.ScoringEngine
import com.scorebuddy.app.logic.GameState

class ScoringViewModel : ViewModel() {
    private var engine: ScoringEngine? = null

    var gameState by mutableStateOf<GameState?>(null)
        private set

    var isSetup by mutableStateOf(true)
    var isDoubles by mutableStateOf(false)
    var t1p1 by mutableStateOf("")
    var t1p2 by mutableStateOf("")
    var t2p1 by mutableStateOf("")
    var t2p2 by mutableStateOf("")

    fun startMatch() {
        engine = ScoringEngine(isDoubles, t1p1, if(isDoubles) t1p2 else null, t2p1, if(isDoubles) t2p2 else null)
        gameState = engine?.getCurrentState()
        isSetup = false
    }

    fun team1Scores() {
        engine?.team1Scores()
        gameState = engine?.getCurrentState()
    }

    fun team2Scores() {
        engine?.team2Scores()
        gameState = engine?.getCurrentState()
    }
}
