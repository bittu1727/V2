package com.badamchhakka.app.logic

import com.badamchhakka.app.model.BoardState
import com.badamchhakka.app.model.BotDifficulty
import com.badamchhakka.app.model.Card
import com.badamchhakka.app.model.HandSortMode
import com.badamchhakka.app.model.Player
import com.badamchhakka.app.model.Suit
import com.badamchhakka.app.model.SuitBoard
import kotlin.random.Random

object GameLogic {

    fun createDeck(): List<Card> {
        val deck = mutableListOf<Card>()
        for (suit in Suit.ALL) {
            for (rank in 1..13) {
                deck.add(Card(suit, rank))
            }
        }
        deck.shuffle()
        return deck
    }

    fun isValidMove(card: Card, board: BoardState): Boolean {
        if (board.isFirstMoveOfRound()) {
            return card.suit == Suit.HEARTS && card.rank == 6
        }

        val suitBoard = board[card.suit]

        // If suit has not opened yet, only its 6 can open it
        if (suitBoard.low == null) {
            return card.rank == 6
        }

        // Downward move (5 down to 1 Ace)
        if (card.rank == suitBoard.low - 1 && card.rank >= 1) {
            // Badam Chhakka Signature Rule: 5♥ must be played before 5♦, 5♣, or 5♠ can be placed!
            if (card.rank == 5 && card.suit != Suit.HEARTS) {
                if (!board.isHeartFivePlayed()) {
                    return false
                }
            }
            return true
        }

        // Upward move (7 up to 13 King)
        if (suitBoard.high != null && card.rank == suitBoard.high + 1 && card.rank <= 13) {
            return true
        }

        return false
    }

    fun getValidMoves(hand: List<Card>, board: BoardState): List<Card> {
        return hand.filter { isValidMove(it, board) }
    }

    fun applyMove(board: BoardState, card: Card): BoardState {
        val current = board[card.suit]
        val newLow: Int
        val newHigh: Int

        if (current.low == null || current.high == null) {
            newLow = 6
            newHigh = 6
        } else {
            newLow = if (card.rank < current.low) card.rank else current.low
            newHigh = if (card.rank > current.high) card.rank else current.high
        }

        val updatedSuits = board.suits.toMutableMap()
        updatedSuits[card.suit] = SuitBoard(low = newLow, high = newHigh)
        return BoardState(suits = updatedSuits)
    }

    fun sortHand(hand: List<Card>, mode: HandSortMode, board: BoardState? = null): List<Card> {
        return when (mode) {
            HandSortMode.PLAYABLE -> {
                if (board == null) hand else {
                    hand.sortedWith(
                        compareByDescending<Card> { isValidMove(it, board) }
                            .thenBy { it.suit.ordinal }
                            .thenBy { it.rank }
                    )
                }
            }
            HandSortMode.RANK -> {
                hand.sortedWith(compareBy<Card> { it.rank }.thenBy { it.suit.ordinal })
            }
            HandSortMode.SUIT -> {
                hand.sortedWith(compareBy<Card> { it.suit.ordinal }.thenBy { it.rank })
            }
        }
    }

    fun chooseBotMove(
        botHand: List<Card>,
        board: BoardState,
        allPlayers: List<Player>,
        difficulty: BotDifficulty = BotDifficulty.NORMAL
    ): Card? {
        val validMoves = getValidMoves(botHand, board)
        if (validMoves.isEmpty()) return null
        if (validMoves.size == 1) return validMoves.first()

        if (difficulty == BotDifficulty.EASY) {
            return validMoves.random()
        }

        val otherPlayers = allPlayers.filter { it.hand != botHand }
        val minOpponentCardCount = if (otherPlayers.isNotEmpty()) {
            otherPlayers.minOf { it.hand.size }
        } else 13

        val scoredMoves = validMoves.map { card ->
            var score = 0.0

            // 1. Opening a suit?
            if (board[card.suit].low == null && card.rank == 6) {
                val cardsInSuit = botHand.count { it.suit == card.suit && it.rank != 6 }
                score += 30 + cardsInSuit * 15
            }

            // 2. Is this 5♥?
            if (card.suit == Suit.HEARTS && card.rank == 5) {
                val lowCardsInOtherSuits = botHand.count { it.suit != Suit.HEARTS && it.rank <= 5 }
                if (lowCardsInOtherSuits > 0) {
                    score += 50 + lowCardsInOtherSuits * 12
                } else {
                    score -= if (difficulty == BotDifficulty.MASTER) 40.0 else 10.0
                }
            }

            // 3. Threat response: shed heavy cards if opponent is near winning
            if (minOpponentCardCount <= 4) {
                score += card.rank * 6.0
            } else {
                score += card.rank * 2.0
            }

            // 4. Reward continuing our own chain
            if (card.rank > 6) {
                val hasNextHigher = botHand.any { it.suit == card.suit && it.rank == card.rank + 1 }
                if (hasNextHigher) score += 20.0
            } else if (card.rank < 6) {
                val hasNextLower = botHand.any { it.suit == card.suit && it.rank == card.rank - 1 }
                if (hasNextLower) score += 20.0
            }

            score += Random.nextDouble(0.0, 5.0)
            Pair(card, score)
        }

        return scoredMoves.maxByOrNull { it.second }?.first
    }
}
