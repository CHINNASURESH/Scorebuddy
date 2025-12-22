package com.scorebuddy.app.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

object DataRepository {
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    private val _matches = MutableStateFlow<List<Match>>(emptyList())
    val matches: StateFlow<List<Match>> = _matches.asStateFlow()

    private val _tournaments = MutableStateFlow<List<Tournament>>(emptyList())
    val tournaments: StateFlow<List<Tournament>> = _tournaments.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    fun addUser(user: User) {
        _users.update { it + user }
    }

    fun login(username: String) {
        val user = _users.value.find { it.username == username }
        if (user != null) {
            _currentUser.value = user
        } else {
            val newUser = User(id = UUID.randomUUID().toString(), username = username)
            addUser(newUser)
            _currentUser.value = newUser
        }
    }

    fun logout() {
        _currentUser.value = null
    }

    fun addMatch(match: Match) {
        _matches.update { it + match }
    }

    fun addTournament(tournament: Tournament) {
        _tournaments.update { it + tournament }
    }

    fun updateTournament(tournament: Tournament) {
        _tournaments.update { list ->
            list.map { if (it.id == tournament.id) tournament else it }
        }
    }
}
