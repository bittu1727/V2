package com.badamchhakka.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
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
import com.badamchhakka.app.model.Card
import com.badamchhakka.app.model.HandSortMode
import com.badamchhakka.app.model.Language
import com.badamchhakka.app.model.Player
import com.badamchhakka.app.ui.theme.GoldBorder
import com.badamchhakka.app.ui.theme.GoldDark
import com.badamchhakka.app.ui.theme.GoldPrimary

@Composable
fun PlayerHandView(
    player: Player,
    isCurrentTurn: Boolean,
    validMoves: List<Card>,
    sortMode: HandSortMode,
    language: Language,
    onSortChange: (HandSortMode) -> Unit,
    onPlayCard: (Card) -> Unit,
    onPass: () -> Unit,
    onHint: () -> Unit,
    modifier: Modifier = Modifier
) {
    val canPass = isCurrentTurn && validMoves.isEmpty()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xE60F172A),
                        Color(0xF2020617)
                    )
                )
            )
            .border(
                1.5.dp,
                if (isCurrentTurn) GoldPrimary else Color.White.copy(alpha = 0.15f),
                RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            )
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Hand Bar: Player info, Sort mode toggles, Card count
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Player Avatar & Name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF1E293B))
                        .border(1.dp, GoldPrimary, RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = player.avatar, fontSize = 16.sp)
                }
                Column {
                    Text(
                        text = player.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (isCurrentTurn) GoldBorder else Color.White
                    )
                    Text(
                        text = "${player.hand.size} ${if (language == Language.HI) "पत्ते शेष" else "cards left"}",
                        fontSize = 10.sp,
                        color = Color.LightGray
                    )
                }
            }

            // Sort Mode Chips
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (language == Language.HI) "क्रम:" else "Sort:",
                    fontSize = 10.sp,
                    color = Color.Gray
                )

                SortChip(
                    label = if (language == Language.HI) "सूट" else "Suit",
                    selected = sortMode == HandSortMode.SUIT,
                    onClick = { onSortChange(HandSortMode.SUIT) }
                )
                SortChip(
                    label = if (language == Language.HI) "मान" else "Rank",
                    selected = sortMode == HandSortMode.RANK,
                    onClick = { onSortChange(HandSortMode.RANK) }
                )
                SortChip(
                    label = if (language == Language.HI) "चलने योग्य" else "Playable",
                    selected = sortMode == HandSortMode.PLAYABLE,
                    onClick = { onSortChange(HandSortMode.PLAYABLE) }
                )
            }
        }

        // Horizontal Scrollable Cards Fan
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(82.dp)
        ) {
            if (player.hand.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🎉 " + if (language == Language.HI) "सभी पत्ते समाप्त!" else "All cards cleared!",
                        color = GoldBorder,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy((-6).dp)
                ) {
                    player.hand.forEach { card ->
                        val isPlayable = isCurrentTurn && validMoves.any { it.id == card.id }
                        CardView(
                            card = card,
                            isPlayable = isPlayable,
                            isSelected = isPlayable,
                            width = 46.dp,
                            height = 70.dp,
                            onClick = {
                                if (isPlayable) {
                                    onPlayCard(card)
                                }
                            }
                        )
                    }
                }
            }
        }

        // Action Buttons Row: Hint and Pass
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onHint,
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .testTag("hint_button"),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = GoldPrimary
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.6f))
            ) {
                Text(
                    text = "💡 " + if (language == Language.HI) "संकेत (${validMoves.size})" else "Hint (${validMoves.size})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = onPass,
                enabled = canPass,
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .testTag("pass_button"),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (canPass) Color(0xFFDC2626) else Color(0xFF334155),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0x33334155),
                    disabledContentColor = Color.Gray
                )
            ) {
                Text(
                    text = "✖ " + if (language == Language.HI) "पास (Pass)" else "Pass Turn",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun SortChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (selected) GoldPrimary else Color(0xFF1E293B))
            .border(0.5.dp, if (selected) GoldBorder else Color.Gray.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = if (selected) FontWeight.Black else FontWeight.Normal,
            color = if (selected) Color.Black else Color.LightGray
        )
    }
}
