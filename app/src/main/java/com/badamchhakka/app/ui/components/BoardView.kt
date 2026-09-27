package com.badamchhakka.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.badamchhakka.app.model.BoardState
import com.badamchhakka.app.model.Card
import com.badamchhakka.app.model.Language
import com.badamchhakka.app.model.Suit
import com.badamchhakka.app.ui.theme.CardRed
import com.badamchhakka.app.ui.theme.GoldBorder
import com.badamchhakka.app.ui.theme.GoldPrimary

@Composable
fun BoardView(
    board: BoardState,
    validMoves: List<Card>,
    language: Language,
    onPlayCard: (Card) -> Unit,
    modifier: Modifier = Modifier
) {
    val heartFivePlayed = board.isHeartFivePlayed()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xE6051E13),
                        Color(0xF2020B07)
                    )
                )
            )
            .border(1.5.dp, GoldPrimary.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Board Header with 5♥ Rule Status Pill
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "♠",
                    color = GoldPrimary,
                    fontSize = 14.sp
                )
                Text(
                    text = if (language == Language.HI) "टेबल बोर्ड" else "Table Board",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = GoldBorder
                )
            }

            // 5♥ status pill
            val statusBg = if (!heartFivePlayed) Color(0xFF451A03) else Color(0xFF064E3B)
            val statusText = if (!heartFivePlayed) {
                if (language == Language.HI) "५♥ लॉक: पान ५ से पहले अन्य ५ बंद" else "5♥ Locked: 5♥ unlocks other 5s"
            } else {
                if (language == Language.HI) "५♥ खुला: सभी ५ सक्रिय" else "5♥ Played: All 5s unlocked"
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(statusBg)
                    .border(1.dp, if (!heartFivePlayed) GoldPrimary.copy(alpha = 0.5f) else Color(0xFF10B981), RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = statusText,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (!heartFivePlayed) GoldBorder else Color(0xFFA7F3D0)
                )
            }
        }

        // 4 Suits Lanes
        Suit.ALL.forEach { suit ->
            val suitBoard = board[suit]
            val isOpen = suitBoard.isOpen

            // Find if user can play cards in this suit
            val playableInSuit = validMoves.filter { it.suit == suit }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isOpen) Color(0x33000000) else Color(0x1A000000))
                    .border(0.5.dp, if (isOpen) GoldPrimary.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 4.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Suit Medallion
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (suit.isRed) Color(0x33DC2626) else Color(0x33334155))
                        .border(1.dp, if (suit.isRed) CardRed.copy(alpha = 0.4f) else Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = suit.symbol,
                        color = if (suit.isRed) CardRed else Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Row of Cards: Low Wing (5 down to 1) -> Center (6) -> High Wing (7 up to 13)
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    // Low Wing (1 to 5)
                    if (isOpen && suitBoard.low != null && suitBoard.low <= 5) {
                        for (r in 1..5) {
                            val isPlayed = r >= suitBoard.low
                            val isNextTarget = suitBoard.low > 1 && r == suitBoard.low - 1
                            val playableCard = playableInSuit.find { it.rank == r }

                            CardSlot(
                                suit = suit,
                                rank = r,
                                isPlayed = isPlayed,
                                isNextTarget = isNextTarget,
                                playableCard = playableCard,
                                onPlayCard = onPlayCard
                            )
                        }
                    } else if (isOpen) {
                        // Empty placeholder for 5
                        val playable5 = playableInSuit.find { it.rank == 5 }
                        CardSlot(
                            suit = suit,
                            rank = 5,
                            isPlayed = false,
                            isNextTarget = true,
                            playableCard = playable5,
                            onPlayCard = onPlayCard
                        )
                    }

                    // Center Six (6)
                    val centerPlayed = isOpen
                    val playable6 = playableInSuit.find { it.rank == 6 }
                    CardSlot(
                        suit = suit,
                        rank = 6,
                        isPlayed = centerPlayed,
                        isNextTarget = !isOpen,
                        isCenter = true,
                        playableCard = playable6,
                        onPlayCard = onPlayCard
                    )

                    // High Wing (7 to 13)
                    if (isOpen && suitBoard.high != null && suitBoard.high >= 7) {
                        for (r in 7..13) {
                            val isPlayed = r <= suitBoard.high
                            val isNextTarget = suitBoard.high < 13 && r == suitBoard.high + 1
                            val playableCard = playableInSuit.find { it.rank == r }

                            CardSlot(
                                suit = suit,
                                rank = r,
                                isPlayed = isPlayed,
                                isNextTarget = isNextTarget,
                                playableCard = playableCard,
                                onPlayCard = onPlayCard
                            )
                        }
                    } else if (isOpen) {
                        // Empty placeholder for 7
                        val playable7 = playableInSuit.find { it.rank == 7 }
                        CardSlot(
                            suit = suit,
                            rank = 7,
                            isPlayed = false,
                            isNextTarget = true,
                            playableCard = playable7,
                            onPlayCard = onPlayCard
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CardSlot(
    suit: Suit,
    rank: Int,
    isPlayed: Boolean,
    isNextTarget: Boolean,
    isCenter: Boolean = false,
    playableCard: Card? = null,
    onPlayCard: (Card) -> Unit
) {
    val card = Card(suit, rank)
    val rankText = card.shortRank
    val isRed = suit.isRed

    if (isPlayed) {
        CardView(
            card = card,
            width = 30.dp,
            height = 42.dp,
            isPlayable = false
        )
    } else {
        // Unplayed Slot
        val isClickable = playableCard != null
        val borderColor = when {
            playableCard != null -> GoldPrimary
            isCenter -> GoldBorder.copy(alpha = 0.5f)
            isNextTarget -> Color.White.copy(alpha = 0.3f)
            else -> Color.White.copy(alpha = 0.1f)
        }

        Box(
            modifier = Modifier
                .width(28.dp)
                .height(40.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(
                    if (playableCard != null) Color(0x33F59E0B)
                    else if (isCenter) Color(0x1AF59E0B)
                    else Color(0x0DFFFFFF)
                )
                .border(if (playableCard != null) 1.5.dp else 0.5.dp, borderColor, RoundedCornerShape(4.dp))
                .then(
                    if (isClickable && playableCard != null) {
                        Modifier.clickable { onPlayCard(playableCard) }
                    } else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = rankText,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (playableCard != null) GoldPrimary else if (isRed) CardRed.copy(alpha = 0.4f) else Color.Gray.copy(alpha = 0.4f)
                )
                if (playableCard != null) {
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(GoldPrimary)
                    )
                }
            }
        }
    }
}
