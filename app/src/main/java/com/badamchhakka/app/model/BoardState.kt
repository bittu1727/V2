package com.badamchhakka.app.model

data class SuitBoard(
    val low: Int? = null,  // lowest card played so far (6 -> 5 -> ... -> 1)
    val high: Int? = null  // highest card played so far (6 -> 7 -> ... -> 13)
) {
    val isOpen: Boolean get() = low != null && high != null
    val isComplete: Boolean get() = low == 1 && high == 13
}

data class BoardState(
    val suits: Map<Suit, SuitBoard> = Suit.ALL.associateWith { SuitBoard() }
) {
    operator fun get(suit: Suit): SuitBoard = suits[suit] ?: SuitBoard()

    fun isFirstMoveOfRound(): Boolean {
        return suits.values.all { it.low == null }
    }

    fun isHeartFivePlayed(): Boolean {
        val hearts = suits[Suit.HEARTS] ?: return false
        return hearts.low != null && hearts.low <= 5
    }

    companion object {
        fun empty(): BoardState = BoardState()
    }
}
