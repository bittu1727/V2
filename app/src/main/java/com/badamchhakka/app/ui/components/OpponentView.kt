package com.badamchhakka.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.badamchhakka.app.model.Player
import com.badamchhakka.app.model.PlayerAction
import com.badamchhakka.app.ui.theme.GoldBorder
import com.badamchhakka.app.ui.theme.GoldPrimary

@Composable
fun OpponentView(
    player: Player,
    isCurrentTurn: Boolean,
    score: Int,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isCurrentTurn) GoldPrimary else Color.White.copy(alpha = 0.2f)
    val bgColor = if (isCurrentTurn) Color(0x66B45309) else Color(0x660F172A)

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(if (isCurrentTurn) 2.dp else 1.dp, borderColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        // Avatar + Active Turn Indicator
        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1E293B))
                    .border(if (isCurrentTurn) 2.dp else 1.dp, borderColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = player.avatar,
                    fontSize = 18.sp
                )
            }
            if (isCurrentTurn) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(GoldPrimary)
                        .align(Alignment.TopEnd)
                )
            }
        }

        // Name
        Text(
            text = player.name,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            color = if (isCurrentTurn) GoldBorder else Color.White,
            maxLines = 1
        )

        // Cards left & Score
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF334155))
                    .padding(horizontal = 4.dp, vertical = 1.dp)
            ) {
                Text(
                    text = "🎴 ${player.hand.size}",
                    fontSize = 9.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "${score} pts",
                fontSize = 9.sp,
                color = GoldBorder
            )
        }

        // Last Action Pill
        player.lastAction?.let { action ->
            val actionText = when (action) {
                is PlayerAction.Play -> "▶ ${action.card.shortRank}${action.card.suit.symbol}"
                is PlayerAction.Pass -> "✖ Pass"
            }
            val actionBg = when (action) {
                is PlayerAction.Play -> Color(0xFF047857)
                is PlayerAction.Pass -> Color(0xFF991B1B)
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(actionBg)
                    .padding(horizontal = 6.dp, vertical = 1.dp)
            ) {
                Text(
                    text = actionText,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
