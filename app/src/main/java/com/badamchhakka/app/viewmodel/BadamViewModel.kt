package com.badamchhakka.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.badamchhakka.app.audio.SoundEffects
import com.badamchhakka.app.logic.GameLogic
import com.badamchhakka.app.model.BoardState
import com.badamchhakka.app.model.Card
import com.badamchhakka.app.model.CareerStats
import com.badamchhakka.app.model.GameSettings
import com.badamchhakka.app.model.HandSortMode
import com.badamchhakka.app.model.Language
import com.badamchhakka.app.model.Player
import com.badamchhakka.app.model.PlayerAction
import com.badamchhakka.app.model.RoundResult
import com.badamchhakka.app.model.Suit
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class ScreenState {
    LOBBY, GAME
}

enum class GameSubMode {
    BOT, PASS_N_PLAY
}

data class BadamUiState(
    val currentScreen: ScreenState = ScreenState.LOBBY,
    val gameSubMode: GameSubMode = GameSubMode.BOT,
    val coins: Int = 3920,
    val gems: Int = 150,
    val playerName: String = "Player 1",
    val playerAvatar: String = "👑",
    val settings: GameSettings = GameSettings(),
    val board: BoardState = BoardState.empty(),
    val players: List<Player> = emptyList(),
    val currentTurn: Int = 0,
    val roundNumber: Int = 1,
    val starterIndex: Int = 0,
    val firstMoveMade: Boolean = false,
    val scores: List<Int> = listOf(0, 0, 0, 0),
    val winnerIndex: Int? = null,
    val statusMessage: String = "",
    val sortMode: HandSortMode = HandSortMode.SUIT,
    val careerStats: CareerStats = CareerStats(),
    val roundHistory: List<RoundResult> = emptyList(),

    // Dialogs
    val isRulesOpen: Boolean = false,
    val isSettingsOpen: Boolean = false,
    val isSummaryOpen: Boolean = false,
    val isSevenUpOpen: Boolean = false,
    val isSpinOpen: Boolean = false,
    val isStoreOpen: Boolean = false,
    val isProfileOpen: Boolean = false,
    val isPassSetupOpen: Boolean = false,
    val isCareerOpen: Boolean = false,
    val isInventoryOpen: Boolean = false
)

class BadamViewModel(application: Application) : AndroidViewModel(application) {

    val soundEffects = SoundEffects(application)

    private val _uiState = MutableStateFlow(BadamUiState())
    val uiState: StateFlow<BadamUiState> = _uiState.asStateFlow()

    private var botJob: Job? = null

    init {
        initSoloGame()
    }

    private fun initSoloGame(subMode: GameSubMode = GameSubMode.BOT) {
        val deck = GameLogic.createDeck()
        val hands = List(4) { mutableListOf<Card>() }
        deck.forEachIndexed { index, card ->
            hands[index % 4].add(card)
        }

        // Find holder of 6 of Hearts
        var holder = 0
        for (i in 0 until 4) {
            if (hands[i].any { it.suit == Suit.HEARTS && it.rank == 6 }) {
                holder = i
                break
            }
        }

        val state = _uiState.value
        val lang = state.settings.language

        val isPass = subMode == GameSubMode.PASS_N_PLAY
        val newPlayers = listOf(
            Player(0, if (isPass) (if (lang == Language.HI) "खिलाड़ी १" else "Player 1") else state.playerName, false, state.playerAvatar, hands[0]),
            Player(1, if (isPass) (if (lang == Language.HI) "खिलाड़ी २" else "Player 2") else (if (lang == Language.HI) "कबीर (Kabir)" else "Kabir (Bot)"), !isPass, "🦁", hands[1]),
            Player(2, if (isPass) (if (lang == Language.HI) "खिलाड़ी ३" else "Player 3") else (if (lang == Language.HI) "प्रिया (Priya)" else "Priya (Bot)"), !isPass, "🐯", hands[2]),
            Player(3, if (isPass) (if (lang == Language.HI) "खिलाड़ी ४" else "Player 4") else (if (lang == Language.HI) "राजेश (Rajesh)" else "Rajesh (Bot)"), !isPass, "🦅", hands[3])
        )

        val starterName = newPlayers[holder].name
        val starterMsg = if (lang == Language.HI)
            "$starterName के पास ६♥ (बादाम छक्का) है, खेल शुरू करना होगा।"
        else
            "$starterName holds the 6♥ and must start the round."

        _uiState.update {
            it.copy(
                board = BoardState.empty(),
                players = newPlayers,
                starterIndex = holder,
                currentTurn = holder,
                firstMoveMade = false,
                winnerIndex = null,
                statusMessage = starterMsg,
                isSummaryOpen = false
            )
        }

        // If bot starts
        triggerBotIfTurn()
    }

    fun startSoloGame(subMode: GameSubMode = GameSubMode.BOT) {
        soundEffects.playClick()
        _uiState.update {
            it.copy(
                currentScreen = ScreenState.GAME,
                gameSubMode = subMode
            )
        }
        initSoloGame(subMode)
    }

    fun startPassNPlayMatch(names: List<String>, avatars: List<String>) {
        soundEffects.playClick()
        val deck = GameLogic.createDeck()
        val hands = List(4) { mutableListOf<Card>() }
        deck.forEachIndexed { index, card -> hands[index % 4].add(card) }

        var holder = 0
        for (i in 0 until 4) {
            if (hands[i].any { it.suit == Suit.HEARTS && it.rank == 6 }) {
                holder = i
                break
            }
        }

        val players = (0 until 4).map { i ->
            Player(
                id = i,
                name = names.getOrElse(i) { "Player ${i + 1}" },
                isBot = false,
                avatar = avatars.getOrElse(i) { "👤" },
                hand = hands[i]
            )
        }

        _uiState.update {
            it.copy(
                currentScreen = ScreenState.GAME,
                gameSubMode = GameSubMode.PASS_N_PLAY,
                board = BoardState.empty(),
                players = players,
                starterIndex = holder,
                currentTurn = holder,
                firstMoveMade = false,
                winnerIndex = null,
                isSummaryOpen = false,
                statusMessage = "${players[holder].name} starts with 6♥"
            )
        }
    }

    fun playHumanCard(card: Card) {
        val state = _uiState.value
        val activePlayer = state.players.getOrNull(state.currentTurn) ?: return
        if (activePlayer.isBot) return

        if (!GameLogic.isValidMove(card, state.board)) {
            soundEffects.playInvalid()
            _uiState.update {
                it.copy(statusMessage = if (it.settings.language == Language.HI) "अवैध चाल! नियम अनुसार पत्ता चलें।" else "Invalid move! Please play a legal card.")
            }
            return
        }

        soundEffects.playCardClack()
        val newBoard = GameLogic.applyMove(state.board, card)
        val newHand = activePlayer.hand.filter { it.id != card.id }

        val updatedPlayers = state.players.map { p ->
            if (p.id == activePlayer.id) {
                p.copy(hand = newHand, lastAction = PlayerAction.Play(card))
            } else p
        }

        if (newHand.isEmpty()) {
            handleRoundEnd(activePlayer.id, updatedPlayers, newBoard)
            return
        }

        val nextTurn = (state.currentTurn + 1) % 4
        _uiState.update {
            it.copy(
                board = newBoard,
                players = updatedPlayers,
                currentTurn = nextTurn,
                firstMoveMade = true,
                statusMessage = "${activePlayer.name} played ${card.shortRank}${card.suit.symbol}"
            )
        }

        triggerBotIfTurn()
    }

    fun passHumanTurn() {
        val state = _uiState.value
        val activePlayer = state.players.getOrNull(state.currentTurn) ?: return
        if (activePlayer.isBot) return

        val valid = GameLogic.getValidMoves(activePlayer.hand, state.board)
        if (valid.isNotEmpty()) {
            soundEffects.playInvalid()
            _uiState.update {
                it.copy(statusMessage = if (it.settings.language == Language.HI) "आपके पास चलने योग्य पत्ता है, पास नहीं कर सकते!" else "You have valid cards, cannot pass!")
            }
            return
        }

        soundEffects.playPassSound()
        val updatedPlayers = state.players.map { p ->
            if (p.id == activePlayer.id) p.copy(lastAction = PlayerAction.Pass()) else p
        }

        val nextTurn = (state.currentTurn + 1) % 4
        _uiState.update {
            it.copy(
                players = updatedPlayers,
                currentTurn = nextTurn,
                statusMessage = "${activePlayer.name} passed"
            )
        }

        triggerBotIfTurn()
    }

    private fun triggerBotIfTurn() {
        botJob?.cancel()
        val state = _uiState.value
        val activePlayer = state.players.getOrNull(state.currentTurn) ?: return
        if (!activePlayer.isBot || state.winnerIndex != null) return

        botJob = viewModelScope.launch {
            delay(state.settings.speed.delayMillis)

            val currentState = _uiState.value
            val currentBot = currentState.players.getOrNull(currentState.currentTurn) ?: return@launch
            if (!currentBot.isBot || currentState.winnerIndex != null) return@launch

            val chosenCard: Card? = if (!currentState.firstMoveMade && currentState.board.isFirstMoveOfRound()) {
                currentBot.hand.find { it.suit == Suit.HEARTS && it.rank == 6 }
            } else {
                GameLogic.chooseBotMove(currentBot.hand, currentState.board, currentState.players, currentState.settings.botDifficulty)
            }

            if (chosenCard != null) {
                soundEffects.playCardClack()
                val newBoard = GameLogic.applyMove(currentState.board, chosenCard)
                val newHand = currentBot.hand.filter { it.id != chosenCard.id }

                val updatedPlayers = currentState.players.map { p ->
                    if (p.id == currentBot.id) {
                        p.copy(hand = newHand, lastAction = PlayerAction.Play(chosenCard))
                    } else p
                }

                if (newHand.isEmpty()) {
                    handleRoundEnd(currentBot.id, updatedPlayers, newBoard)
                    return@launch
                }

                val nextTurn = (currentState.currentTurn + 1) % 4
                _uiState.update {
                    it.copy(
                        board = newBoard,
                        players = updatedPlayers,
                        currentTurn = nextTurn,
                        firstMoveMade = true,
                        statusMessage = "${currentBot.name} played ${chosenCard.shortRank}${chosenCard.suit.symbol}"
                    )
                }

                triggerBotIfTurn()
            } else {
                soundEffects.playPassSound()
                val updatedPlayers = currentState.players.map { p ->
                    if (p.id == currentBot.id) p.copy(lastAction = PlayerAction.Pass()) else p
                }
                val nextTurn = (currentState.currentTurn + 1) % 4
                _uiState.update {
                    it.copy(
                        players = updatedPlayers,
                        currentTurn = nextTurn,
                        statusMessage = "${currentBot.name} passed"
                    )
                }

                triggerBotIfTurn()
            }
        }
    }

    private fun handleRoundEnd(winnerId: Int, finalPlayers: List<Player>, finalBoard: BoardState) {
        val state = _uiState.value
        val roundPenalties = finalPlayers.map { p ->
            if (p.id == winnerId) 0 else p.hand.sumOf { it.penaltyPoints }
        }
        val newScores = state.scores.mapIndexed { idx, s -> s + roundPenalties[idx] }

        val roundResult = RoundResult(
            roundNumber = state.roundNumber,
            winnerIndex = winnerId,
            roundPenaltyPoints = roundPenalties,
            playerScores = newScores,
            playerCardCounts = finalPlayers.map { it.hand.size },
            leftoverCards = finalPlayers.map { it.hand }
        )

        val userWon = winnerId == 0
        if (userWon) {
            soundEffects.playWin()
            soundEffects.playCoinCollect()
        }

        val newStats = state.careerStats.let { cs ->
            cs.copy(
                totalRoundsPlayed = cs.totalRoundsPlayed + 1,
                totalRoundsWon = cs.totalRoundsWon + (if (userWon) 1 else 0),
                totalPenaltyPoints = cs.totalPenaltyPoints + roundPenalties[0],
                cleanWins = cs.cleanWins + (if (userWon) 1 else 0),
                sixHeartOpenings = cs.sixHeartOpenings + (if (state.starterIndex == 0) 1 else 0)
            )
        }

        _uiState.update {
            it.copy(
                board = finalBoard,
                players = finalPlayers,
                winnerIndex = winnerId,
                scores = newScores,
                coins = it.coins + (if (userWon) 350 else 0),
                careerStats = newStats,
                roundHistory = listOf(roundResult) + it.roundHistory,
                isSummaryOpen = true
            )
        }
    }

    fun nextRound() {
        soundEffects.playClick()
        _uiState.update { it.copy(roundNumber = it.roundNumber + 1) }
        initSoloGame(_uiState.value.gameSubMode)
    }

    fun resetTournament() {
        soundEffects.playClick()
        _uiState.update {
            it.copy(
                roundNumber = 1,
                scores = listOf(0, 0, 0, 0),
                roundHistory = emptyList()
            )
        }
        initSoloGame(_uiState.value.gameSubMode)
    }

    fun navigateTo(screen: ScreenState) {
        soundEffects.playClick()
        _uiState.update { it.copy(currentScreen = screen) }
    }

    fun updateCurrency(deltaCoins: Int, deltaGems: Int) {
        soundEffects.playCoinCollect()
        _uiState.update {
            it.copy(
                coins = (it.coins + deltaCoins).coerceAtLeast(0),
                gems = (it.gems + deltaGems).coerceAtLeast(0)
            )
        }
    }

    fun updateProfile(name: String, avatar: String) {
        soundEffects.playClick()
        _uiState.update { it.copy(playerName = name, playerAvatar = avatar) }
    }

    fun updateSettings(newSettings: GameSettings) {
        soundEffects.enabled = newSettings.soundEnabled
        _uiState.update { it.copy(settings = newSettings) }
    }

    fun toggleLanguage() {
        soundEffects.playClick()
        _uiState.update {
            val next = if (it.settings.language == Language.HI) Language.EN else Language.HI
            it.copy(settings = it.settings.copy(language = next))
        }
    }

    fun toggleSound() {
        val curr = _uiState.value.settings.soundEnabled
        soundEffects.enabled = !curr
        _uiState.update {
            it.copy(settings = it.settings.copy(soundEnabled = !curr))
        }
    }

    fun setSortMode(mode: HandSortMode) {
        soundEffects.playClick()
        _uiState.update { it.copy(sortMode = mode) }
    }

    // Modal Visibility Helpers
    fun setRulesOpen(open: Boolean) = _uiState.update { it.copy(isRulesOpen = open) }
    fun setSettingsOpen(open: Boolean) = _uiState.update { it.copy(isSettingsOpen = open) }
    fun setSummaryOpen(open: Boolean) = _uiState.update { it.copy(isSummaryOpen = open) }
    fun setSevenUpOpen(open: Boolean) = _uiState.update { it.copy(isSevenUpOpen = open) }
    fun setSpinOpen(open: Boolean) = _uiState.update { it.copy(isSpinOpen = open) }
    fun setStoreOpen(open: Boolean) = _uiState.update { it.copy(isStoreOpen = open) }
    fun setProfileOpen(open: Boolean) = _uiState.update { it.copy(isProfileOpen = open) }
    fun setPassSetupOpen(open: Boolean) = _uiState.update { it.copy(isPassSetupOpen = open) }
    fun setCareerOpen(open: Boolean) = _uiState.update { it.copy(isCareerOpen = open) }
    fun setInventoryOpen(open: Boolean) = _uiState.update { it.copy(isInventoryOpen = open) }
}
