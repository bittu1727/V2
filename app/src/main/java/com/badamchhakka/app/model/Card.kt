package com.badamchhakka.app.model

enum class Suit(
    val symbol: String,
    val isRed: Boolean,
    val englishName: String,
    val hindiName: String
) {
    HEARTS("♥", true, "Hearts (Badam)", "पान / बादाम"),
    DIAMONDS("♦", true, "Diamonds (Eent)", "ईंट"),
    CLUBS("♣", false, "Clubs (Chidi)", "चिड़ी"),
    SPADES("♠", false, "Spades (Hukum)", "हुकुम");

    companion object {
        val ALL = listOf(HEARTS, DIAMONDS, CLUBS, SPADES)
    }
}

data class Card(
    val suit: Suit,
    val rank: Int // 1 (Ace) to 13 (King)
) {
    val id: String = "${suit.symbol}-$rank"

    val shortRank: String
        get() = when (rank) {
            1 -> "A"
            11 -> "J"
            12 -> "Q"
            13 -> "K"
            else -> rank.toString()
        }

    val penaltyPoints: Int
        get() = rank // A=1, 2-10=value, J=11, Q=12, K=13

    fun displayName(language: Language): String {
        val rankName = when (rank) {
            1 -> if (language == Language.HI) "इक्का (A)" else "Ace (A)"
            11 -> if (language == Language.HI) "गुलाम (J)" else "Jack (J)"
            12 -> if (language == Language.HI) "बेगम (Q)" else "Queen (Q)"
            13 -> if (language == Language.HI) "बादशाह (K)" else "King (K)"
            else -> rank.toString()
        }
        val suitName = if (language == Language.HI) suit.hindiName else suit.englishName
        return "$rankName ${suit.symbol} ($suitName)"
    }
}
