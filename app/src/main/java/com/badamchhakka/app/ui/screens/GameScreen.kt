package com.badamchhakka.app.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.badamchhakka.app.logic.GameLogic
import com.badamchhakka.app.model.Language
import com.badamchhakka.app.ui.components.BoardView
import com.badamchhakka.app.ui.components.OpponentView
import com.badamchhakka.app.ui.components.PlayerHandView
import com.badamchhakka.app.ui.theme.GoldBorder
import com.badamchhakka.app.ui.theme.GoldPrimary
import com.badamchhakka.app.viewmodel.BadamUiState
import com.badamchhakka.app.viewmodel.BadamViewModel
import com.badamchhakka.app.viewmodel.GameSubMode
import com.badamchhakka.app.viewmodel.ScreenState

@Composable
fun GameScreen(
    viewModel: BadamViewModel,
    state: BadamUiState,
    modifier: Modifier = Modifier
) {
    // Intercept back button to return to lobby cleanly
    BackHandler {
        viewModel.navigateTo(ScreenState.LOBBY)
    }

    val lang = state.settings.language
    val feltBaseColor = Color(state.settings.theme.feltColorHex)
    val humanIndex = if (state.gameSubMode == GameSubMode.PASS_N_PLAY) state.currentTurn else 0
    val humanPlayer = state.players.getOrElse(humanIndex) { state.players.first() }
    val isHumanTurn = state.currentTurn == humanIndex && !humanPlayer.isBot
    val validMoves = if (isHumanTurn) GameLogic.getValidMoves(humanPlayer.hand, state.board) else emptyList()
    val sortedHand = GameLogic.sortHand(humanPlayer.hand, state.sortMode, state.board)

    // Opponents relative to active player
    val opponents = state.players.filter { it.id != humanPlayer.id }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        feltBaseColor,
                        feltBaseColor.copy(alpha = 0.8f),
                        Color(0xFF020B07)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // TOP TABLE BAR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x99000000))
                    .border(1.dp, GoldPrimary.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back to Lobby
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = { viewModel.navigateTo(ScreenState.LOBBY) },
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("back_to_lobby")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = GoldBorder)
                    }

                    Column {
                        Text(
                            text = if (lang == Language.HI) "राउंड ${state.roundNumber}" else "Round ${state.roundNumber}",
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            color = GoldBorder
                        )
                        Text(
                            text = if (state.gameSubMode == GameSubMode.PASS_N_PLAY) "📱 Pass & Play" else "🤖 Vs Smart Bots",
                            fontSize = 9.sp,
                            color = Color.LightGray
                        )
                    }
                }

                // Action Controls: New Round, Rules, Settings
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Language Switch
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF334155))
                            .clickable { viewModel.toggleLanguage() }
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(if (lang == Language.HI) "HI" else "EN", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    // Sound Toggle
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (state.settings.soundEnabled) Color(0xFF065F46) else Color(0xFF7F1D1D))
                            .clickable { viewModel.toggleSound() }
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(if (state.settings.soundEnabled) "🔊" else "🔇", fontSize = 10.sp)
                    }

                    // New Round
                    IconButton(
                        onClick = { viewModel.nextRound() },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "New Round", tint = GoldBorder, modifier = Modifier.size(16.dp))
                    }

                    // Rules
                    IconButton(
                        onClick = { viewModel.setRulesOpen(true) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.HelpOutline, contentDescription = "Rules", tint = Color.White, modifier = Modifier.size(16.dp))
                    }

                    // Settings
                    IconButton(
                        onClick = { viewModel.setSettingsOpen(true) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }

            // TOP OPPONENTS ROW (Seated at the table)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                opponents.forEach { opp ->
                    OpponentView(
                        player = opp,
                        isCurrentTurn = state.currentTurn == opp.id,
                        score = state.scores.getOrElse(opp.id) { 0 },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // CENTER BOARD AREA
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center
            ) {
                BoardView(
                    board = state.board,
                    validMoves = validMoves,
                    language = lang,
                    onPlayCard = { viewModel.playHumanCard(it) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // LIVE COMMENTARY / TURN PROMPT PILL
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                val commentary = when {
                    isHumanTurn && validMoves.isNotEmpty() ->
                        if (lang == Language.HI) "आपके पास चलने योग्य पत्ता है, चाल चलें!" else "Your turn! Tap a highlighted card to play."
                    isHumanTurn && validMoves.isEmpty() ->
                        if (lang == Language.HI) "कोई चाल उपलब्ध नहीं है — 'पास' दबाएँ।" else "No valid moves available. Tap 'Pass'."
                    else -> state.statusMessage.ifBlank {
                        val active = state.players.getOrNull(state.currentTurn)?.name ?: "Bot"
                        "$active is thinking..."
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xD9000000))
                        .border(1.dp, GoldPrimary.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 14.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "ℹ️  $commentary",
                        color = GoldBorder,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
            }

            // BOTTOM HUMAN PLAYER HAND
            PlayerHandView(
                player = humanPlayer.copy(hand = sortedHand),
                isCurrentTurn = isHumanTurn,
                validMoves = validMoves,
                sortMode = state.sortMode,
                language = lang,
                onSortChange = { viewModel.setSortMode(it) },
                onPlayCard = { viewModel.playHumanCard(it) },
                onPass = { viewModel.passHumanTurn() },
                onHint = {
                    val msg = if (validMoves.isNotEmpty()) {
                        "Playable: " + validMoves.joinToString(", ") { "${it.shortRank}${it.suit.symbol}" }
                    } else {
                        if (lang == Language.HI) "कोई चाल नहीं है, पास करें।" else "No valid moves. Must pass."
                    }
                    // Hint is shown in commentary
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
