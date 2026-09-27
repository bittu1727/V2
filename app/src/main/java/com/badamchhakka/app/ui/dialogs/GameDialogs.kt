package com.badamchhakka.app.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.badamchhakka.app.model.BotDifficulty
import com.badamchhakka.app.model.BotSpeed
import com.badamchhakka.app.model.Card
import com.badamchhakka.app.model.CareerStats
import com.badamchhakka.app.model.GameSettings
import com.badamchhakka.app.model.Language
import com.badamchhakka.app.model.Player
import com.badamchhakka.app.model.RoundResult
import com.badamchhakka.app.model.Suit
import com.badamchhakka.app.model.TableTheme
import com.badamchhakka.app.ui.components.CardView
import com.badamchhakka.app.ui.theme.CoinAmber
import com.badamchhakka.app.ui.theme.GemCyan
import com.badamchhakka.app.ui.theme.GoldBorder
import com.badamchhakka.app.ui.theme.GoldPrimary

@Composable
fun RulesDialog(
    language: Language,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF0F172A))
                .border(1.5.dp, GoldPrimary, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (language == Language.HI) "बादाम छक्का के नियम" else "Game Rules & Strategy",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = GoldBorder
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(340.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        RuleSection(
                            title = if (language == Language.HI) "१. शुरुआती चाल (६♥)" else "1. The Opening Move (6♥)",
                            body = if (language == Language.HI)
                                "जिस खिलाड़ी के पास पान/बादाम का छक्का (6♥) होता है, वही अनिवार्य रूप से इसे चलकर राउंड शुरू करता है।"
                            else
                                "The player holding the Six of Hearts (6♥ / Badam Chhakka) must play it first to start the round."
                        )
                    }
                    item {
                        RuleSection(
                            title = if (language == Language.HI) "२. अन्य सूट खोलना" else "2. Opening Other Suits",
                            body = if (language == Language.HI)
                                "ईंट (♦), चिड़ी (♣) या हुकुम (♠) के पत्ते चलने के लिए पहले उस सूट का ६ टेबल पर चलना अनिवार्य है।"
                            else
                                "To play cards in Diamonds (♦), Clubs (♣), or Spades (♠), that suit must first be opened with its respective 6."
                        )
                    }
                    item {
                        RuleSection(
                            title = if (language == Language.HI) "३. दोनों दिशाओं में क्रमबद्ध चाल" else "3. Outward Sequences",
                            body = if (language == Language.HI)
                                "६ उतरने के बाद पत्ते दोनों दिशाओं में खुलते हैं: नीचे ५ ➔ ४ ➔ ३ ➔ २ ➔ १ (इक्का A), और ऊपर ७ ➔ ८ ➔ ९ ➔ १० ➔ J ➔ Q ➔ K। इक्का और बादशाह आने पर छोर लॉक हो जाता है।"
                            else
                                "Cards branch outwards: Downwards: 5 ➔ 4 ➔ 3 ➔ 2 ➔ Ace (1). Upwards: 7 ➔ 8 ➔ 9 ➔ 10 ➔ Jack ➔ Queen ➔ King. Ace and King close their ends."
                        )
                    }
                    item {
                        RuleSection(
                            title = if (language == Language.HI) "४. ५♥ का प्रसिद्ध ब्लॉकिंग नियम!" else "4. The Signature 5♥ Blocking Rule!",
                            body = if (language == Language.HI)
                                "विशेष नियम: जब तक टेबल पर ५♥ नहीं चला जाता, तब तक ईंट, चिड़ी या हुकुम का ५ नहीं चला जा सकता! ५♥ हाथ में रोक कर रखना विरोधियों को फंसाने की मुख्य रणनीति है।"
                            else
                                "Crucial signature Indian rule: No other suit can play its 5 (♦, ♣, ♠) until the 5 of Hearts (5♥) is placed on the board! Withholding 5♥ is a master tactic."
                        )
                    }
                    item {
                        RuleSection(
                            title = if (language == Language.HI) "५. पॉइंट व स्कोरिंग" else "5. Scoring & Points",
                            body = if (language == Language.HI)
                                "सभी पत्ते पहले समाप्त करने वाले को ० पॉइंट मिलते हैं। अन्य खिलाड़ियों को हाथ में बचे पत्तों के मान बराबर पेनल्टी मिलती है (A=१, २-१०=संख्या, J=११, Q=१२, K=१३)। कम स्कोर वाला जीतता है!"
                            else
                                "The first player to clear their hand gets 0 points. Opponents receive penalty points equal to their remaining card values (A=1, 2-10=rank, J=11, Q=12, K=13). Lowest total score wins!"
                        )
                    }
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                ) {
                    Text(if (language == Language.HI) "समझ गया" else "Got It", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun RuleSection(title: String, body: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF1E293B))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(text = title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = GoldBorder)
        Text(text = body, fontSize = 11.sp, color = Color.LightGray, lineHeight = 16.sp)
    }
}

@Composable
fun SettingsDialog(
    settings: GameSettings,
    language: Language,
    onUpdateSettings: (GameSettings) -> Unit,
    onResetTournament: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF0F172A))
                .border(1.5.dp, GoldPrimary, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (language == Language.HI) "सेटिंग्स" else "Settings",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = GoldBorder
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                // Sound FX
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(if (language == Language.HI) "ध्वनि व कंपन (Sound & Vibration)" else "Sound & Haptics", color = Color.White, fontSize = 13.sp)
                    Switch(
                        checked = settings.soundEnabled,
                        onCheckedChange = { onUpdateSettings(settings.copy(soundEnabled = it)) },
                        colors = SwitchDefaults.colors(checkedThumbColor = GoldPrimary)
                    )
                }

                // Bot Speed
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(if (language == Language.HI) "बॉट गति (Bot Speed)" else "Bot Speed", color = Color.LightGray, fontSize = 12.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        BotSpeed.entries.forEach { spd ->
                            val label = when (spd) {
                                BotSpeed.FAST -> if (language == Language.HI) "तेज़" else "Fast"
                                BotSpeed.NORMAL -> if (language == Language.HI) "सामान्य" else "Normal"
                                BotSpeed.SLOW -> if (language == Language.HI) "धीमी" else "Slow"
                            }
                            val isSel = settings.speed == spd
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) GoldPrimary else Color(0xFF1E293B))
                                    .clickable { onUpdateSettings(settings.copy(speed = spd)) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(label, color = if (isSel) Color.Black else Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Bot Difficulty
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(if (language == Language.HI) "बॉट स्तर (AI Difficulty)" else "Bot Difficulty", color = Color.LightGray, fontSize = 12.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        BotDifficulty.entries.forEach { diff ->
                            val label = when (diff) {
                                BotDifficulty.EASY -> if (language == Language.HI) "आसान" else "Easy"
                                BotDifficulty.NORMAL -> if (language == Language.HI) "स्मार्ट" else "Smart"
                                BotDifficulty.MASTER -> if (language == Language.HI) "मास्टर" else "Master"
                            }
                            val isSel = settings.botDifficulty == diff
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) GoldPrimary else Color(0xFF1E293B))
                                    .clickable { onUpdateSettings(settings.copy(botDifficulty = diff)) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(label, color = if (isSel) Color.Black else Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Reset Tournament
                OutlinedButton(
                    onClick = {
                        onResetTournament()
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444))
                ) {
                    Text(if (language == Language.HI) "टूर्नामेंट रीसेट करें" else "Reset Tournament Scores", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun RoundSummaryDialog(
    winnerIndex: Int,
    players: List<Player>,
    scores: List<Int>,
    roundNumber: Int,
    language: Language,
    onNextRound: () -> Unit,
    onResetTournament: () -> Unit,
    onDismiss: () -> Unit
) {
    val winner = players.getOrNull(winnerIndex) ?: players.first()

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF0F172A))
                .border(2.dp, GoldPrimary, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "🏆 " + if (language == Language.HI) "${winner.name} विजयी!" else "${winner.name} Won Round $roundNumber!",
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    color = GoldBorder
                )

                Text(
                    text = if (language == Language.HI) "राउंड का पॉइंट विवरण (पेनल्टी स्कोर):" else "Penalty Points This Round:",
                    fontSize = 11.sp,
                    color = Color.LightGray
                )

                // Players Breakdown List
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    players.forEachIndexed { idx, player ->
                        val isWin = idx == winnerIndex
                        val penaltyPoints = if (isWin) 0 else player.hand.sumOf { it.penaltyPoints }
                        val totalScore = scores.getOrElse(idx) { 0 }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isWin) Color(0x3310B981) else Color(0x331E293B))
                                .border(0.5.dp, if (isWin) Color(0xFF10B981) else Color.Transparent, RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(text = player.avatar, fontSize = 16.sp)
                                Column {
                                    Text(
                                        text = player.name + if (isWin) " 👑" else "",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (isWin) Color(0xFFA7F3D0) else Color.White
                                    )
                                    Text(
                                        text = "${player.hand.size} " + if (language == Language.HI) "पत्ते बचे" else "cards leftover",
                                        fontSize = 10.sp,
                                        color = Color.LightGray
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "+$penaltyPoints pts",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isWin) Color(0xFF34D399) else Color(0xFFF87171)
                                )
                                Text(
                                    text = if (language == Language.HI) "कुल: $totalScore" else "Total: $totalScore",
                                    fontSize = 10.sp,
                                    color = GoldBorder
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onResetTournament,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.LightGray)
                    ) {
                        Text(if (language == Language.HI) "रीसेट" else "Reset", fontSize = 11.sp)
                    }

                    Button(
                        onClick = onNextRound,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                    ) {
                        Text(if (language == Language.HI) "अगला राउंड" else "Next Round", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun SevenUpDownDialog(
    coins: Int,
    language: Language,
    onUpdateCoins: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var betAmount by remember { mutableIntStateOf(100) }
    var betChoice by remember { mutableStateOf<String?>(null) } // "DOWN", "SEVEN", "UP"
    var drawnCard by remember { mutableStateOf<Card?>(null) }
    var resultMessage by remember { mutableStateOf<String?>(null) }
    var won by remember { mutableStateOf<Boolean?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF0F172A))
                .border(2.dp, GoldPrimary, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🎰 " + if (language == Language.HI) "7 अप 7 डाउन" else "7 Up 7 Down Minigame",
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        color = GoldBorder
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                // Balance
                Text(
                    text = "🪙 ${coins} Coins",
                    color = CoinAmber,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                // Drawn Card Showcase
                Box(
                    modifier = Modifier
                        .size(64.dp, 90.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1E293B))
                        .border(1.dp, GoldPrimary, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (drawnCard != null) {
                        CardView(card = drawnCard!!, width = 56.dp, height = 82.dp)
                    } else {
                        Text("🎴", fontSize = 32.sp)
                    }
                }

                resultMessage?.let { msg ->
                    Text(
                        text = msg,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (won == true) Color(0xFF34D399) else Color(0xFFF87171)
                    )
                }

                // 3 Bet Choices
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Down (2 - 6) 2x
                    BetBox(
                        title = if (language == Language.HI) "7 डाउन (२-६)" else "7 Down (2-6)",
                        multiplier = "2x",
                        isSelected = betChoice == "DOWN",
                        onClick = { betChoice = "DOWN" },
                        modifier = Modifier.weight(1f)
                    )
                    // Seven (7) 5x
                    BetBox(
                        title = if (language == Language.HI) "लकी ७ (7)" else "Lucky 7 (7)",
                        multiplier = "5x 🔥",
                        isSelected = betChoice == "SEVEN",
                        onClick = { betChoice = "SEVEN" },
                        modifier = Modifier.weight(1f)
                    )
                    // Up (8 - 12) 2x
                    BetBox(
                        title = if (language == Language.HI) "7 अप (८-१३)" else "7 Up (8-K)",
                        multiplier = "2x",
                        isSelected = betChoice == "UP",
                        onClick = { betChoice = "UP" },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Bet Amounts
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf(50, 100, 250, 500).forEach { amt ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (betAmount == amt) GoldPrimary else Color(0xFF1E293B))
                                .clickable { betAmount = amt }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "$amt",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (betAmount == amt) Color.Black else Color.White
                            )
                        }
                    }
                }

                // Roll Button
                Button(
                    onClick = {
                        if (coins < betAmount) {
                            resultMessage = if (language == Language.HI) "पर्याप्त कॉइन्स नहीं हैं!" else "Not enough coins!"
                            won = false
                            return@Button
                        }
                        if (betChoice == null) return@Button

                        val suits = Suit.ALL
                        val randomSuit = suits.random()
                        val randomRank = (1..13).random()
                        val drawn = Card(randomSuit, randomRank)
                        drawnCard = drawn

                        onUpdateCoins(-betAmount)

                        val isWin = when (betChoice) {
                            "DOWN" -> randomRank < 7
                            "UP" -> randomRank > 7
                            "SEVEN" -> randomRank == 7
                            else -> false
                        }

                        val mult = if (betChoice == "SEVEN") 5 else 2

                        if (isWin) {
                            val winCoins = betAmount * mult
                            onUpdateCoins(winCoins)
                            won = true
                            resultMessage = if (language == Language.HI) "बधाई! आप जीते $winCoins कॉइन्स!" else "WINNER! You won $winCoins Coins!"
                        } else {
                            won = false
                            resultMessage = if (language == Language.HI) "कोई बात नहीं! पत्ता था ${drawn.shortRank}${drawn.suit.symbol}" else "Better luck! Card was ${drawn.shortRank}${drawn.suit.symbol}"
                        }
                    },
                    enabled = betChoice != null && coins >= betAmount,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                ) {
                    Text(
                        text = if (language == Language.HI) "रोल करें (Roll Card)" else "Draw Card",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
private fun BetBox(
    title: String,
    multiplier: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) GoldPrimary else Color(0xFF1E293B))
            .border(1.dp, if (isSelected) GoldBorder else Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(text = title, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isSelected) Color.Black else Color.White)
        Text(text = multiplier, fontSize = 9.sp, fontWeight = FontWeight.Black, color = if (isSelected) Color(0xFF78350F) else GoldBorder)
    }
}

@Composable
fun SpinWheelDialog(
    language: Language,
    onReward: (coins: Int, gems: Int) -> Unit,
    onDismiss: () -> Unit
) {
    var hasSpun by remember { mutableStateOf(false) }
    var rewardText by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF0F172A))
                .border(2.dp, GoldPrimary, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🎁 " + if (language == Language.HI) "दैनिक स्पिन लकी व्हील" else "Daily Lucky Spin",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = GoldBorder
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.sweepGradient(
                                listOf(
                                    Color(0xFFF59E0B),
                                    Color(0xFFEF4444),
                                    Color(0xFF3B82F6),
                                    Color(0xFF10B981),
                                    Color(0xFFF59E0B)
                                )
                            )
                        )
                        .border(3.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🎡", fontSize = 48.sp)
                }

                rewardText?.let {
                    Text(text = it, fontWeight = FontWeight.Bold, color = GoldBorder, fontSize = 13.sp)
                }

                Button(
                    onClick = {
                        if (!hasSpun) {
                            val rewards = listOf(
                                Pair(500, 5),
                                Pair(1000, 10),
                                Pair(250, 2),
                                Pair(1500, 15)
                            )
                            val rew = rewards.random()
                            onReward(rew.first, rew.second)
                            hasSpun = true
                            rewardText = if (language == Language.HI)
                                "शानदार! प्राप्त हुए +${rew.first} कॉइन्स और +${rew.second} रत्न!"
                            else
                                "Awesome! Won +${rew.first} Coins and +${rew.second} Gems!"
                        }
                    },
                    enabled = !hasSpun,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                ) {
                    Text(
                        text = if (hasSpun) (if (language == Language.HI) "आज का दावा किया" else "Claimed for Today")
                        else (if (language == Language.HI) "स्पिन करें (Spin Free)" else "Free Spin"),
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun StoreDialog(
    coins: Int,
    gems: Int,
    language: Language,
    onAddCurrency: (addCoins: Int, addGems: Int) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF0F172A))
                .border(2.dp, GoldPrimary, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🛒 " + if (language == Language.HI) "कॉइन्स व जेम्स स्टोर" else "Coins & Gems Store",
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        color = GoldBorder
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                // Balance Summary
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🪙 ${coins} Coins   |   💎 ${gems} Gems", color = GoldBorder, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                // Store Packs
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    StorePackRow(
                        title = if (language == Language.HI) "मुफ़्त वीडियो बोनस" else "Free Ad Reward",
                        reward = "+500 🪙 +5 💎",
                        buttonText = if (language == Language.HI) "मुफ़्त क्लेम" else "Claim Free",
                        onClick = { onAddCurrency(500, 5) }
                    )
                    StorePackRow(
                        title = if (language == Language.HI) "शुरुआती पैक" else "Starter Pack",
                        reward = "+2,000 🪙 +25 💎",
                        buttonText = "₹25",
                        onClick = { onAddCurrency(2000, 25) }
                    )
                    StorePackRow(
                        title = if (language == Language.HI) "रॉयल बंडल" else "Royal Bundle",
                        reward = "+10,000 🪙 +100 💎",
                        buttonText = "₹99",
                        onClick = { onAddCurrency(10000, 100) }
                    )
                }
            }
        }
    }
}

@Composable
private fun StorePackRow(
    title: String,
    reward: String,
    buttonText: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF1E293B))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
            Text(reward, fontSize = 11.sp, color = GoldBorder)
        }
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
            shape = RoundedCornerShape(6.dp)
        ) {
            Text(buttonText, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        }
    }
}

@Composable
fun ProfileEditDialog(
    currentName: String,
    currentAvatar: String,
    language: Language,
    onSave: (name: String, avatar: String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(currentName) }
    var avatar by remember { mutableStateOf(currentAvatar) }
    val avatarOptions = listOf("👑", "🦁", "🐯", "🦅", "💎", "🔥", "⚡", "🍀")

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF0F172A))
                .border(2.dp, GoldPrimary, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = if (language == Language.HI) "प्रोफ़ाइल संपादित करें" else "Edit Profile",
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    color = GoldBorder
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(if (language == Language.HI) "आपका नाम" else "Your Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(if (language == Language.HI) "अवतार चुनें" else "Choose Avatar", fontSize = 12.sp, color = Color.LightGray)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    avatarOptions.forEach { av ->
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (avatar == av) GoldPrimary else Color(0xFF1E293B))
                                .clickable { avatar = av },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(av, fontSize = 18.sp)
                        }
                    }
                }

                Button(
                    onClick = {
                        onSave(name.ifBlank { "Player 1" }, avatar)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                ) {
                    Text(if (language == Language.HI) "सहेजें (Save)" else "Save", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun PassNPlaySetupDialog(
    language: Language,
    onStartMatch: (List<String>, List<String>) -> Unit,
    onDismiss: () -> Unit
) {
    var p1Name by remember { mutableStateOf("Player 1") }
    var p2Name by remember { mutableStateOf("Player 2") }
    var p3Name by remember { mutableStateOf("Player 3") }
    var p4Name by remember { mutableStateOf("Player 4") }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF0F172A))
                .border(2.dp, GoldPrimary, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "👥 " + if (language == Language.HI) "पास एंड प्ले खिलाड़ी सेटअप" else "Pass N Play Local Setup",
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    color = GoldBorder
                )

                OutlinedTextField(value = p1Name, onValueChange = { p1Name = it }, label = { Text("Player 1") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = p2Name, onValueChange = { p2Name = it }, label = { Text("Player 2") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = p3Name, onValueChange = { p3Name = it }, label = { Text("Player 3") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = p4Name, onValueChange = { p4Name = it }, label = { Text("Player 4") }, singleLine = true, modifier = Modifier.fillMaxWidth())

                Button(
                    onClick = {
                        onStartMatch(
                            listOf(p1Name, p2Name, p3Name, p4Name),
                            listOf("👑", "🦁", "🐯", "🦅")
                        )
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                ) {
                    Text(if (language == Language.HI) "खेल शुरू करें" else "Start Match", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun CareerStatsDialog(
    stats: CareerStats,
    language: Language,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF0F172A))
                .border(2.dp, GoldPrimary, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🎖 " + if (language == Language.HI) "करियर आँकड़े" else "Career Stats",
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        color = GoldBorder
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                StatRow(label = if (language == Language.HI) "खेले गए कुल राउंड" else "Total Rounds Played", value = "${stats.totalRoundsPlayed}")
                StatRow(label = if (language == Language.HI) "जीते गए राउंड" else "Rounds Won", value = "${stats.totalRoundsWon}")
                StatRow(label = if (language == Language.HI) "क्लीन जीत (० पॉइंट)" else "Clean Wins (0 pts)", value = "${stats.cleanWins}")
                StatRow(label = if (language == Language.HI) "कुल पेनल्टी पॉइंट" else "Total Penalty Points", value = "${stats.totalPenaltyPoints}")
                StatRow(label = if (language == Language.HI) "६♥ से शुरुआत" else "6♥ Openings", value = "${stats.sixHeartOpenings}")

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                ) {
                    Text(if (language == Language.HI) "बंद करें" else "Close", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFF1E293B))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.LightGray, fontSize = 12.sp)
        Text(value, color = GoldBorder, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}

@Composable
fun InventoryDialog(
    currentTheme: TableTheme,
    language: Language,
    onSelectTheme: (TableTheme) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF0F172A))
                .border(2.dp, GoldPrimary, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🎨 " + if (language == Language.HI) "टेबल रंग थीम" else "Table Felt Themes",
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        color = GoldBorder
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                TableTheme.entries.forEach { th ->
                    val isSel = th == currentTheme
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(th.feltColorHex))
                            .border(if (isSel) 2.dp else 0.5.dp, if (isSel) GoldPrimary else Color.White.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .clickable { onSelectTheme(th) }
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (language == Language.HI) th.hindiName else th.displayName,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        if (isSel) {
                            Text("✔", color = GoldBorder, fontWeight = FontWeight.Black)
                        }
                    }
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                ) {
                    Text(if (language == Language.HI) "लागू करें" else "Apply", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
