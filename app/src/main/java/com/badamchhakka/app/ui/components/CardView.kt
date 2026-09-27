package com.badamchhakka.app.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.badamchhakka.app.model.Card
import com.badamchhakka.app.ui.theme.CardBlack
import com.badamchhakka.app.ui.theme.CardCream
import com.badamchhakka.app.ui.theme.CardRed
import com.badamchhakka.app.ui.theme.CardWhite
import com.badamchhakka.app.ui.theme.GoldBorder
import com.badamchhakka.app.ui.theme.GoldPrimary

@Composable
fun CardView(
    card: Card,
    modifier: Modifier = Modifier,
    isPlayable: Boolean = false,
    isSelected: Boolean = false,
    width: Dp = 48.dp,
    height: Dp = 68.dp,
    onClick: (() -> Unit)? = null
) {
    val suitColor = if (card.suit.isRed) CardRed else CardBlack
    val elevation by animateDpAsState(
        targetValue = if (isPlayable || isSelected) 8.dp else 2.dp,
        animationSpec = tween(durationMillis = 200),
        label = "cardElevation"
    )
    val offsetY by animateDpAsState(
        targetValue = if (isSelected) (-10).dp else if (isPlayable) (-4).dp else 0.dp,
        animationSpec = tween(durationMillis = 200),
        label = "cardOffset"
    )

    val shape = RoundedCornerShape(6.dp)

    Box(
        modifier = modifier
            .offset(y = offsetY)
            .width(width)
            .height(height)
            .shadow(elevation, shape)
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(CardWhite, CardCream)
                )
            )
            .border(
                width = if (isPlayable) 2.dp else 1.dp,
                color = if (isPlayable) GoldPrimary else Color(0xFFCBD5E1),
                shape = shape
            )
            .testTag("card_${card.id}")
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else Modifier
            )
            .padding(horizontal = 2.dp, vertical = 2.dp)
    ) {
        // Top-left rank and suit
        Column(
            modifier = Modifier.align(Alignment.TopStart),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = card.shortRank,
                fontSize = if (width < 44.dp) 10.sp else 12.sp,
                fontWeight = FontWeight.Black,
                color = suitColor,
                lineHeight = 11.sp
            )
            Text(
                text = card.suit.symbol,
                fontSize = if (width < 44.dp) 8.sp else 10.sp,
                color = suitColor,
                lineHeight = 9.sp
            )
        }

        // Center Suit Emblem
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = card.suit.symbol,
                fontSize = if (width < 44.dp) 18.sp else 22.sp,
                color = suitColor.copy(alpha = 0.85f),
                fontFamily = FontFamily.Serif
            )
        }

        // Playable badge glow dot if playable
        if (isPlayable) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(2.dp)
                    .width(6.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(GoldPrimary)
            )
        }
    }
}
