package com.badamchhakka.app.model

enum class Language {
    HI, EN
}

enum class BotDifficulty {
    EASY, NORMAL, MASTER
}

enum class BotSpeed(val delayMillis: Long) {
    FAST(350L),
    NORMAL(750L),
    SLOW(1200L)
}

enum class TableTheme(val feltColorHex: Long, val displayName: String, val hindiName: String) {
    EMERALD(0xFF0B3823, "Classic Emerald", "क्लासिक एमराल्ड हरा"),
    CRIMSON(0xFF4A0E17, "Royal Crimson", "रॉयल क्रिमसन लाल"),
    SAPPHIRE(0xFF0A2540, "Sapphire Blue", "नीलम नीला"),
    OBSIDIAN(0xFF1C1C1C, "Dark Obsidian", "ऑब्सिडियन डार्क")
}

enum class HandSortMode {
    SUIT, RANK, PLAYABLE
}

data class GameSettings(
    val language: Language = Language.HI,
    val soundEnabled: Boolean = true,
    val speed: BotSpeed = BotSpeed.NORMAL,
    val botDifficulty: BotDifficulty = BotDifficulty.NORMAL,
    val theme: TableTheme = TableTheme.EMERALD,
    val targetScore: Int = 100
)
