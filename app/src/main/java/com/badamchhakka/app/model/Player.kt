package com.badamchhakka.app.model

sealed interface PlayerAction {
    data class Play(val card: Card, val timestamp: Long = System.currentTimeMillis()) : PlayerAction
    data class Pass(val timestamp: Long = System.currentTimeMillis()) : PlayerAction
}

data class Player(
    val id: Int,
    val name: String,
    val isBot: Boolean,
    val avatar: String,
    val hand: List<Card>,
    val lastAction: PlayerAction? = null
)
