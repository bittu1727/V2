package com.badamchhakka.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.badamchhakka.app.ui.dialogs.CareerStatsDialog
import com.badamchhakka.app.ui.dialogs.InventoryDialog
import com.badamchhakka.app.ui.dialogs.PassNPlaySetupDialog
import com.badamchhakka.app.ui.dialogs.ProfileEditDialog
import com.badamchhakka.app.ui.dialogs.RoundSummaryDialog
import com.badamchhakka.app.ui.dialogs.RulesDialog
import com.badamchhakka.app.ui.dialogs.SettingsDialog
import com.badamchhakka.app.ui.dialogs.SevenUpDownDialog
import com.badamchhakka.app.ui.dialogs.SpinWheelDialog
import com.badamchhakka.app.ui.dialogs.StoreDialog
import com.badamchhakka.app.ui.screens.GameScreen
import com.badamchhakka.app.ui.screens.LobbyScreen
import com.badamchhakka.app.ui.theme.BadamChhakkaTheme
import com.badamchhakka.app.viewmodel.BadamViewModel
import com.badamchhakka.app.viewmodel.ScreenState

class MainActivity : ComponentActivity() {

    private val viewModel: BadamViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            BadamChhakkaTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .safeDrawingPadding(),
                    color = Color(0xFF020B07)
                ) {
                    BadamApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun BadamApp(viewModel: BadamViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    when (state.currentScreen) {
        ScreenState.LOBBY -> LobbyScreen(viewModel = viewModel, state = state)
        ScreenState.GAME -> GameScreen(viewModel = viewModel, state = state)
    }

    // Modal Dialogs
    if (state.isRulesOpen) {
        RulesDialog(
            language = state.settings.language,
            onDismiss = { viewModel.setRulesOpen(false) }
        )
    }

    if (state.isSettingsOpen) {
        SettingsDialog(
            settings = state.settings,
            language = state.settings.language,
            onUpdateSettings = { viewModel.updateSettings(it) },
            onResetTournament = { viewModel.resetTournament() },
            onDismiss = { viewModel.setSettingsOpen(false) }
        )
    }

    if (state.isSummaryOpen && state.winnerIndex != null) {
        RoundSummaryDialog(
            winnerIndex = state.winnerIndex!!,
            players = state.players,
            scores = state.scores,
            roundNumber = state.roundNumber,
            language = state.settings.language,
            onNextRound = { viewModel.nextRound() },
            onResetTournament = { viewModel.resetTournament() },
            onDismiss = { viewModel.setSummaryOpen(false) }
        )
    }

    if (state.isSevenUpOpen) {
        SevenUpDownDialog(
            coins = state.coins,
            language = state.settings.language,
            onUpdateCoins = { viewModel.updateCurrency(it, 0) },
            onDismiss = { viewModel.setSevenUpOpen(false) }
        )
    }

    if (state.isSpinOpen) {
        SpinWheelDialog(
            language = state.settings.language,
            onReward = { coins, gems -> viewModel.updateCurrency(coins, gems) },
            onDismiss = { viewModel.setSpinOpen(false) }
        )
    }

    if (state.isStoreOpen) {
        StoreDialog(
            coins = state.coins,
            gems = state.gems,
            language = state.settings.language,
            onAddCurrency = { coins, gems -> viewModel.updateCurrency(coins, gems) },
            onDismiss = { viewModel.setStoreOpen(false) }
        )
    }

    if (state.isProfileOpen) {
        ProfileEditDialog(
            currentName = state.playerName,
            currentAvatar = state.playerAvatar,
            language = state.settings.language,
            onSave = { name, avatar -> viewModel.updateProfile(name, avatar) },
            onDismiss = { viewModel.setProfileOpen(false) }
        )
    }

    if (state.isPassSetupOpen) {
        PassNPlaySetupDialog(
            language = state.settings.language,
            onStartMatch = { names, avatars -> viewModel.startPassNPlayMatch(names, avatars) },
            onDismiss = { viewModel.setPassSetupOpen(false) }
        )
    }

    if (state.isCareerOpen) {
        CareerStatsDialog(
            stats = state.careerStats,
            language = state.settings.language,
            onDismiss = { viewModel.setCareerOpen(false) }
        )
    }

    if (state.isInventoryOpen) {
        InventoryDialog(
            currentTheme = state.settings.theme,
            language = state.settings.language,
            onSelectTheme = { viewModel.updateSettings(state.settings.copy(theme = it)) },
            onDismiss = { viewModel.setInventoryOpen(false) }
        )
    }
}
