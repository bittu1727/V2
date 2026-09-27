package com.badamchhakka.app.ui.screens

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.badamchhakka.app.model.Language
import com.badamchhakka.app.ui.theme.CoinAmber
import com.badamchhakka.app.ui.theme.GemCyan
import com.badamchhakka.app.ui.theme.GoldBorder
import com.badamchhakka.app.ui.theme.GoldDark
import com.badamchhakka.app.ui.theme.GoldPrimary
import com.badamchhakka.app.ui.theme.LobbyBlueBottom
import com.badamchhakka.app.ui.theme.LobbyBlueMid
import com.badamchhakka.app.ui.theme.LobbyBlueTop
import com.badamchhakka.app.viewmodel.BadamUiState
import com.badamchhakka.app.viewmodel.BadamViewModel
import com.badamchhakka.app.viewmodel.GameSubMode
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun LobbyScreen(
    viewModel: BadamViewModel,
    state: BadamUiState,
    modifier: Modifier = Modifier
) {
    val lang = state.settings.language
    var onlineCount by remember { mutableIntStateOf(187060) }

    // Live player count oscillation
    LaunchedEffect(Unit) {
        while (true) {
            delay(3500)
            onlineCount += Random.nextInt(-5, 8)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(LobbyBlueTop, LobbyBlueMid, LobbyBlueBottom)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // TOP BAR: Profile, Level, Settings, Mail, Gems, Coins, Store Cart
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x80000000))
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile Avatar & Level
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clickable { viewModel.setProfileOpen(true) }
                            .testTag("profile_button")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF1E293B))
                                .border(2.dp, GoldPrimary, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(state.playerAvatar, fontSize = 22.sp)
                        }
                        // Star badge
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .offset(x = (-3).dp, y = 3.dp)
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(GoldPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("⭐", fontSize = 8.sp)
                        }
                    }

                    // Settings Button
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF2563EB))
                            .border(1.dp, Color(0xFF93C5FD), RoundedCornerShape(8.dp))
                            .clickable { viewModel.setSettingsOpen(true) }
                            .testTag("settings_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White, modifier = Modifier.size(16.dp))
                    }

                    // Mail Button
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(GoldPrimary)
                            .border(1.dp, GoldBorder, RoundedCornerShape(8.dp))
                            .clickable { viewModel.setCareerOpen(true) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Email, contentDescription = "Mail", tint = Color.Black, modifier = Modifier.size(16.dp))
                    }
                }

                // Currency Bars: Gems & Coins + Cart
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Gems Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xE6061D49))
                            .border(1.5.dp, GemCyan, RoundedCornerShape(16.dp))
                            .clickable { viewModel.setStoreOpen(true) }
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Text("💎", fontSize = 11.sp)
                            Text("${state.gems}", color = Color.White, fontWeight = FontWeight.Black, fontSize = 11.sp)
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF10B981)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.Black, modifier = Modifier.size(10.dp))
                            }
                        }
                    }

                    // Coins Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xE6061D49))
                            .border(1.5.dp, GoldPrimary, RoundedCornerShape(16.dp))
                            .clickable { viewModel.setStoreOpen(true) }
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Text("🪙", fontSize = 11.sp)
                            Text("${state.coins}", color = GoldBorder, fontWeight = FontWeight.Black, fontSize = 11.sp)
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF10B981)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.Black, modifier = Modifier.size(10.dp))
                            }
                        }
                    }

                    // Cart
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                Brush.verticalGradient(listOf(GoldPrimary, GoldDark))
                            )
                            .border(1.dp, GoldBorder, RoundedCornerShape(8.dp))
                            .clickable { viewModel.setStoreOpen(true) }
                            .testTag("store_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Store", tint = Color.Black, modifier = Modifier.size(16.dp))
                    }
                }
            }

            // Quick Badges Row (₹25 Offer, Free Spin, Rules, Language)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Quick Badges
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFDC2626))
                            .clickable { viewModel.setStoreOpen(true) }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("₹25 ONLY 🔥", color = Color.White, fontWeight = FontWeight.Black, fontSize = 9.sp)
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF10B981))
                            .clickable { viewModel.setSpinOpen(true) }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("🎁 FREE SPIN", color = Color.White, fontWeight = FontWeight.Black, fontSize = 9.sp)
                    }
                }

                // Right Quick Badges
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF4F46E5))
                            .clickable { viewModel.toggleLanguage() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(if (lang == Language.HI) "HI" else "EN", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    }

                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(GoldPrimary)
                            .clickable { viewModel.setRulesOpen(true) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.HelpOutline, contentDescription = "Rules", tint = Color.Black, modifier = Modifier.size(16.dp))
                    }
                }
            }

            // CENTER STAGE: Brand Logo BADAM KING with Crown
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text("👑", fontSize = 34.sp)

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LogoLetterBox("B", Color(0xFF0284C7))
                    LogoLetterBox("A", Color(0xFFE11D48))
                    LogoLetterBox("D", Color(0xFF059669))
                    LogoLetterBox("A", Color(0xFFD97706))
                    LogoLetterBox("M", Color(0xFF7C3AED))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                Brush.verticalGradient(listOf(Color(0xFFFEF08A), Color(0xFFF59E0B)))
                            )
                            .border(1.5.dp, Color.White, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("KING", fontWeight = FontWeight.Black, color = Color.Black, fontSize = 16.sp)
                    }
                }

                // Subtitle Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x99000000))
                        .border(1.dp, GoldPrimary.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (lang == Language.HI) "🔴 6♥ असली बादाम छक्का 🎲" else "🔴 Classic 6♥ Indian Sevens 🎲",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldBorder
                    )
                }
            }

            // PRIMARY GAME MODES
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // ROW 1: ONLINE, TEAM UP, FRIENDS
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ModeTile(
                        icon = "🌍",
                        title = if (lang == Language.HI) "ऑनलाइन" else "ONLINE",
                        subtitle = "🟢 ${onlineCount}",
                        onClick = { viewModel.startSoloGame(GameSubMode.BOT) },
                        modifier = Modifier.weight(1f)
                    )
                    ModeTile(
                        icon = "⚔️",
                        title = if (lang == Language.HI) "टीम अप" else "TEAM UP",
                        subtitle = "🟢 6,950",
                        onClick = { viewModel.startSoloGame(GameSubMode.BOT) },
                        modifier = Modifier.weight(1f)
                    )
                    ModeTile(
                        icon = "❤️",
                        title = if (lang == Language.HI) "दोस्त" else "FRIENDS",
                        subtitle = "🟢 45,600",
                        onClick = { viewModel.setPassSetupOpen(true) },
                        modifier = Modifier.weight(1f)
                    )
                }

                // ROW 2: VS COMPUTER & PASS N PLAY (WIDE)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    WideModeButton(
                        icon = "🤖",
                        title = if (lang == Language.HI) "कंप्यूटर" else "VS COMPUTER",
                        subtitle = if (lang == Language.HI) "ऑफ़लाइन स्मार्ट बॉट्स" else "Smart AI Bots",
                        onClick = { viewModel.startSoloGame(GameSubMode.BOT) },
                        testTag = "play_vs_computer",
                        modifier = Modifier.weight(1f)
                    )

                    WideModeButton(
                        icon = "👥",
                        title = if (lang == Language.HI) "पास एंड प्ले" else "PASS N PLAY",
                        subtitle = if (lang == Language.HI) "१ फ़ोन पर खेलें" else "1 Device Local",
                        onClick = { viewModel.setPassSetupOpen(true) },
                        testTag = "play_pass_n_play",
                        modifier = Modifier.weight(1f)
                    )
                }

                // ROW 3: 7 UP DOWN MINIGAME & TOURNAMENT
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 7 Up Down Cup
                    Column(
                        modifier = Modifier
                            .clickable { viewModel.setSevenUpOpen(true) }
                            .testTag("seven_up_down_button"),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.verticalGradient(listOf(Color(0xFFF59E0B), Color(0xFFDC2626)))
                                )
                                .border(2.dp, GoldBorder, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🎰", fontSize = 20.sp)
                        }
                        Text("7 UP", color = GoldBorder, fontWeight = FontWeight.Black, fontSize = 9.sp)
                    }

                    // Tournament Banner
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(GoldDark, GoldPrimary, GoldDark)
                                )
                            )
                            .border(1.5.dp, GoldBorder, RoundedCornerShape(14.dp))
                            .clickable { viewModel.startSoloGame(GameSubMode.BOT) }
                            .padding(vertical = 10.dp, horizontal = 14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("🏆", fontSize = 22.sp)
                                Column {
                                    Text(
                                        text = if (lang == Language.HI) "टूर्नामेंट खेलें" else "GRAND TOURNAMENT",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 13.sp,
                                        color = Color.Black
                                    )
                                    Text(
                                        text = if (lang == Language.HI) "लक्ष्य १०० पॉइंट" else "Target 100 Points",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF451A03)
                                    )
                                }
                            }
                            Text("▶", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 16.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // BOTTOM NAVIGATION BAR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xE6020617))
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavItem(icon = "🏠", label = if (lang == Language.HI) "होम" else "Home", isSelected = true) {}
                BottomNavItem(icon = "🎨", label = if (lang == Language.HI) "थीम" else "Themes", isSelected = false) {
                    viewModel.setInventoryOpen(true)
                }
                BottomNavItem(icon = "🎖", label = if (lang == Language.HI) "आँकड़े" else "Career", isSelected = false) {
                    viewModel.setCareerOpen(true)
                }
                BottomNavItem(icon = "📜", label = if (lang == Language.HI) "नियम" else "Rules", isSelected = false) {
                    viewModel.setRulesOpen(true)
                }
            }
        }
    }
}

@Composable
private fun LogoLetterBox(letter: String, color: Color) {
    Box(
        modifier = Modifier
            .size(26.dp, 32.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(color)
            .border(1.5.dp, Color.White, RoundedCornerShape(6.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(letter, color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
    }
}

@Composable
private fun ModeTile(
    icon: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFFFDF5D), Color(0xFFF59E0B), Color(0xFFB45309))
                )
            )
            .border(2.dp, GoldBorder, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1E3A8A))
                    .border(1.5.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(icon, fontSize = 16.sp)
            }
            Text(title, fontWeight = FontWeight.Black, fontSize = 10.sp, color = Color.Black)
            Text(subtitle, fontWeight = FontWeight.Bold, fontSize = 8.sp, color = Color(0xFF451A03))
        }
    }
}

@Composable
private fun WideModeButton(
    icon: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFFFDF5D), Color(0xFFF59E0B), Color(0xFFB45309))
                )
            )
            .border(2.dp, GoldBorder, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag(testTag)
            .padding(10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF1E3A8A))
                    .border(1.dp, Color.White, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(icon, fontSize = 18.sp)
            }
            Column {
                Text(title, fontWeight = FontWeight.Black, fontSize = 11.sp, color = Color.Black)
                Text(subtitle, fontWeight = FontWeight.Bold, fontSize = 9.sp, color = Color(0xFF451A03))
            }
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: String,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(icon, fontSize = 18.sp)
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal,
            color = if (isSelected) GoldPrimary else Color.LightGray
        )
    }
}
