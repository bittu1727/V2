import React, { useState, useEffect, useRef } from 'react';
import {
  Settings,
  HelpCircle,
  Trophy,
  Sparkles,
  RotateCcw,
  ArrowLeft,
  Volume2,
  VolumeX,
  Plus,
  ShoppingCart,
  Award,
  ShieldAlert,
  CheckCircle2,
  Lock,
  X,
  Smartphone
} from 'lucide-react';

export type Suit = '♥' | '♦' | '♣' | '♠';

export interface Card {
  id: string;
  suit: Suit;
  rank: number; // 1 (A) to 13 (K)
}

export interface SuitBoardState {
  low: number | null;
  high: number | null;
}

export type BoardState = Record<Suit, SuitBoardState>;

export interface Player {
  id: number;
  name: string;
  isBot: boolean;
  avatar: string;
  hand: Card[];
  lastAction?: {
    type: 'play' | 'pass';
    card?: Card;
  };
}

const SUITS: Suit[] = ['♥', '♦', '♣', '♠'];

function createEmptyBoard(): BoardState {
  return {
    '♥': { low: null, high: null },
    '♦': { low: null, high: null },
    '♣': { low: null, high: null },
    '♠': { low: null, high: null },
  };
}

function createDeck(): Card[] {
  const deck: Card[] = [];
  for (const suit of SUITS) {
    for (let rank = 1; rank <= 13; rank++) {
      deck.push({ id: `${suit}-${rank}`, suit, rank });
    }
  }
  for (let i = deck.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [deck[i], deck[j]] = [deck[j], deck[i]];
  }
  return deck;
}

function isHeartFivePlayed(board: BoardState): boolean {
  return board['♥'].low !== null && board['♥'].low <= 5;
}

function isValidMove(card: Card, board: BoardState): boolean {
  const isFirst = SUITS.every((s) => board[s].low === null);
  if (isFirst) {
    return card.suit === '♥' && card.rank === 6;
  }

  const suitState = board[card.suit];
  if (suitState.low === null) {
    return card.rank === 6;
  }

  if (card.rank === suitState.low - 1 && card.rank >= 1) {
    if (card.rank === 5 && card.suit !== '♥') {
      if (!isHeartFivePlayed(board)) return false;
    }
    return true;
  }

  if (suitState.high !== null && card.rank === suitState.high + 1 && card.rank <= 13) {
    return true;
  }

  return false;
}

function getValidMoves(hand: Card[], board: BoardState): Card[] {
  return hand.filter((c) => isValidMove(c, board));
}

function applyMoveToBoard(board: BoardState, card: Card): BoardState {
  const current = board[card.suit];
  const newLow = current.low === null ? 6 : Math.min(card.rank, current.low);
  const newHigh = current.high === null ? 6 : Math.max(card.rank, current.high);

  return {
    ...board,
    [card.suit]: { low: newLow, high: newHigh },
  };
}

function getRankLabel(rank: number): string {
  if (rank === 1) return 'A';
  if (rank === 11) return 'J';
  if (rank === 12) return 'Q';
  if (rank === 13) return 'K';
  return rank.toString();
}

export default function App() {
  const [screen, setScreen] = useState<'lobby' | 'game'>('lobby');
  const [language, setLanguage] = useState<'hi' | 'en'>('hi');
  const [soundEnabled, setSoundEnabled] = useState(true);
  const [coins, setCoins] = useState(3920);
  const [gems, setGems] = useState(150);
  const [theme, setTheme] = useState<'emerald' | 'crimson' | 'sapphire' | 'obsidian'>('emerald');
  const [gameMode, setGameMode] = useState<'bot' | 'pass'>('bot');

  const [board, setBoard] = useState<BoardState>(createEmptyBoard);
  const [players, setPlayers] = useState<Player[]>([
    { id: 0, name: 'Player 1', isBot: false, avatar: '👑', hand: [] },
    { id: 1, name: 'कबीर (Kabir)', isBot: true, avatar: '🦁', hand: [] },
    { id: 2, name: 'प्रिया (Priya)', isBot: true, avatar: '🐯', hand: [] },
    { id: 3, name: 'राजेश (Rajesh)', isBot: true, avatar: '🦅', hand: [] },
  ]);
  const [currentTurn, setCurrentTurn] = useState<number>(0);
  const [roundNumber, setRoundNumber] = useState<number>(1);
  const [starterIndex, setStarterIndex] = useState<number>(0);
  const [scores, setScores] = useState<number[]>([0, 0, 0, 0]);
  const [statusMessage, setStatusMessage] = useState<string>('');
  const [winnerIndex, setWinnerIndex] = useState<number | null>(null);

  // Dialogs
  const [isRulesOpen, setIsRulesOpen] = useState(false);
  const [isSettingsOpen, setIsSettingsOpen] = useState(false);
  const [isSummaryOpen, setIsSummaryOpen] = useState(false);
  const [isSevenUpOpen, setIsSevenUpOpen] = useState(false);
  const [isSpinOpen, setIsSpinOpen] = useState(false);
  const [isStoreOpen, setIsStoreOpen] = useState(false);
  const [isAndroidInfoOpen, setIsAndroidInfoOpen] = useState(false);

  // 7 Up Down State
  const [sevenBetChoice, setSevenBetChoice] = useState<'down' | 'seven' | 'up' | null>(null);
  const [sevenBetAmt, setSevenBetAmt] = useState(100);
  const [sevenDrawn, setSevenDrawn] = useState<Card | null>(null);
  const [sevenMsg, setSevenMsg] = useState<string | null>(null);

  const startRound = (mode: 'bot' | 'pass' = gameMode) => {
    const deck = createDeck();
    const hands: Card[][] = [[], [], [], []];
    deck.forEach((c, idx) => hands[idx % 4].push(c));

    let holder = 0;
    for (let i = 0; i < 4; i++) {
      if (hands[i].some((c) => c.suit === '♥' && c.rank === 6)) {
        holder = i;
        break;
      }
    }

    const isPass = mode === 'pass';
    const newPlayers: Player[] = [
      { id: 0, name: isPass ? (language === 'hi' ? 'खिलाड़ी 1' : 'Player 1') : 'Player 1', isBot: false, avatar: '👑', hand: hands[0] },
      { id: 1, name: isPass ? (language === 'hi' ? 'खिलाड़ी 2' : 'Player 2') : (language === 'hi' ? 'कबीर (Kabir)' : 'Kabir (Bot)'), isBot: !isPass, avatar: '🦁', hand: hands[1] },
      { id: 2, name: isPass ? (language === 'hi' ? 'खिलाड़ी 3' : 'Player 3') : (language === 'hi' ? 'प्रिया (Priya)' : 'Priya (Bot)'), isBot: !isPass, avatar: '🐯', hand: hands[2] },
      { id: 3, name: isPass ? (language === 'hi' ? 'खिलाड़ी 4' : 'Player 4') : (language === 'hi' ? 'राजेश (Rajesh)' : 'Rajesh (Bot)'), isBot: !isPass, avatar: '🦅', hand: hands[3] },
    ];

    setBoard(createEmptyBoard());
    setPlayers(newPlayers);
    setStarterIndex(holder);
    setCurrentTurn(holder);
    setWinnerIndex(null);
    setIsSummaryOpen(false);

    const starterName = newPlayers[holder].name;
    setStatusMessage(
      language === 'hi'
        ? `${starterName} के पास ६♥ (बादाम छक्का) है, खेल शुरू करें।`
        : `${starterName} holds the 6♥ and must open the round.`
    );
  };

  useEffect(() => {
    startRound();
  }, []);

  // Bot Turn Loop
  useEffect(() => {
    if (screen !== 'game' || winnerIndex !== null) return;
    const active = players[currentTurn];
    if (!active || !active.isBot) return;

    const timer = setTimeout(() => {
      const valid = getValidMoves(active.hand, board);
      if (valid.length > 0) {
        // Choose smart move
        let chosen = valid[0];
        // Priority to 6 or runs
        const sixes = valid.filter((c) => c.rank === 6);
        if (sixes.length > 0) chosen = sixes[0];
        else chosen = valid[Math.floor(Math.random() * valid.length)];

        const newBoard = applyMoveToBoard(board, chosen);
        const newHand = active.hand.filter((c) => c.id !== chosen.id);

        setBoard(newBoard);
        setPlayers((prev) =>
          prev.map((p) =>
            p.id === active.id
              ? { ...p, hand: newHand, lastAction: { type: 'play', card: chosen } }
              : p
          )
        );

        if (newHand.length === 0) {
          handleRoundEnd(active.id);
          return;
        }

        setStatusMessage(`${active.name} played ${getRankLabel(chosen.rank)}${chosen.suit}`);
      } else {
        // Pass
        setPlayers((prev) =>
          prev.map((p) =>
            p.id === active.id ? { ...p, lastAction: { type: 'pass' } } : p
          )
        );
        setStatusMessage(`${active.name} passed`);
      }

      setCurrentTurn((prev) => (prev + 1) % 4);
    }, 750);

    return () => clearTimeout(timer);
  }, [currentTurn, board, screen, players, winnerIndex]);

  const handleRoundEnd = (winnerId: number) => {
    setWinnerIndex(winnerId);
    setIsSummaryOpen(true);

    const penalties = players.map((p, idx) =>
      idx === winnerId ? 0 : p.hand.reduce((acc, c) => acc + c.rank, 0)
    );
    setScores((prev) => prev.map((s, idx) => s + penalties[idx]));

    if (winnerId === 0) {
      setCoins((c) => c + 350);
    }
  };

  const handlePlayCard = (card: Card) => {
    const active = players[currentTurn];
    if (!active || active.isBot) return;

    if (!isValidMove(card, board)) {
      setStatusMessage(language === 'hi' ? 'अवैध चाल! नियम अनुसार पत्ता चलें।' : 'Invalid move! Play a legal card.');
      return;
    }

    const newBoard = applyMoveToBoard(board, card);
    const newHand = active.hand.filter((c) => c.id !== card.id);

    setBoard(newBoard);
    setPlayers((prev) =>
      prev.map((p) =>
        p.id === active.id
          ? { ...p, hand: newHand, lastAction: { type: 'play', card } }
          : p
      )
    );

    if (newHand.length === 0) {
      handleRoundEnd(active.id);
      return;
    }

    setStatusMessage(`${active.name} played ${getRankLabel(card.rank)}${card.suit}`);
    setCurrentTurn((prev) => (prev + 1) % 4);
  };

  const handlePass = () => {
    const active = players[currentTurn];
    if (!active || active.isBot) return;

    const valid = getValidMoves(active.hand, board);
    if (valid.length > 0) {
      setStatusMessage(language === 'hi' ? 'चलने योग्य पत्ता है, पास नहीं कर सकते!' : 'You have valid cards, cannot pass!');
      return;
    }

    setPlayers((prev) =>
      prev.map((p) => (p.id === active.id ? { ...p, lastAction: { type: 'pass' } } : p))
    );
    setStatusMessage(`${active.name} passed`);
    setCurrentTurn((prev) => (prev + 1) % 4);
  };

  const humanPlayer = players[gameMode === 'pass' ? currentTurn : 0] || players[0];
  const isHumanTurn = currentTurn === humanPlayer.id && !humanPlayer.isBot;
  const humanValidMoves = isHumanTurn ? getValidMoves(humanPlayer.hand, board) : [];
  const heartFiveOut = isHeartFivePlayed(board);

  const themeGradients = {
    emerald: 'bg-[radial-gradient(ellipse_at_center,_#0b3823_0%,_#041a10_60%,_#020b07_100%)]',
    crimson: 'bg-[radial-gradient(ellipse_at_center,_#4a0e17_0%,_#28070d_60%,_#110205_100%)]',
    sapphire: 'bg-[radial-gradient(ellipse_at_center,_#0a2540_0%,_#061524_60%,_#02080f_100%)]',
    obsidian: 'bg-[radial-gradient(ellipse_at_center,_#1c1c1c_0%,_#0d0d0d_60%,_#000000_100%)]',
  };

  // -------------------------------------------------------------
  // LOBBY SCREEN (LUDO KING STYLE)
  // -------------------------------------------------------------
  if (screen === 'lobby') {
    return (
      <div className="min-h-screen w-full bg-[#032363] text-neutral-100 flex flex-col justify-between select-none relative font-sans overflow-x-hidden">
        {/* Background gradient */}
        <div className="absolute inset-0 bg-gradient-to-b from-[#0a55d4] via-[#083ea8] to-[#021847] pointer-events-none" />

        {/* Top Header */}
        <header className="relative z-10 w-full px-3 py-2 flex items-center justify-between bg-black/50 border-b border-white/10">
          <div className="flex items-center gap-2">
            <div className="w-10 h-10 rounded-xl bg-slate-900 border-2 border-amber-400 flex items-center justify-center text-2xl shadow">
              👑
            </div>
            <button
              onClick={() => setIsSettingsOpen(true)}
              className="w-8 h-8 rounded-lg bg-blue-600 border border-blue-300 flex items-center justify-center text-white"
            >
              <Settings size={16} />
            </button>
            <button
              onClick={() => setIsAndroidInfoOpen(true)}
              className="px-2 py-1 rounded-lg bg-emerald-600 border border-emerald-300 flex items-center gap-1 text-[11px] font-bold text-white shadow animate-pulse"
              title="View Native Android APK Info"
            >
              <Smartphone size={13} />
              <span>Android APK</span>
            </button>
          </div>

          <div className="flex items-center gap-2">
            {/* Gems */}
            <div
              onClick={() => setIsStoreOpen(true)}
              className="flex items-center gap-1 bg-[#061d49] border border-cyan-400 rounded-full px-2.5 py-0.5 cursor-pointer"
            >
              <span className="text-xs">💎</span>
              <span className="text-xs font-bold text-cyan-200">{gems}</span>
              <div className="w-3.5 h-3.5 rounded-full bg-emerald-500 text-black flex items-center justify-center text-[10px] font-black">
                +
              </div>
            </div>

            {/* Coins */}
            <div
              onClick={() => setIsStoreOpen(true)}
              className="flex items-center gap-1 bg-[#061d49] border border-amber-400 rounded-full px-2.5 py-0.5 cursor-pointer"
            >
              <span className="text-xs">🪙</span>
              <span className="text-xs font-bold text-amber-300">{coins.toLocaleString()}</span>
              <div className="w-3.5 h-3.5 rounded-full bg-emerald-500 text-black flex items-center justify-center text-[10px] font-black">
                +
              </div>
            </div>

            {/* Language */}
            <button
              onClick={() => setLanguage((l) => (l === 'hi' ? 'en' : 'hi'))}
              className="w-7 h-7 rounded-lg bg-indigo-600 text-white font-black text-xs border border-indigo-300"
            >
              {language === 'hi' ? 'HI' : 'EN'}
            </button>

            {/* Rules */}
            <button
              onClick={() => setIsRulesOpen(true)}
              className="w-7 h-7 rounded-full bg-amber-400 text-black flex items-center justify-center"
            >
              <HelpCircle size={16} />
            </button>
          </div>
        </header>

        {/* Center Stage & Modes */}
        <main className="relative z-10 flex-1 max-w-md w-full mx-auto p-4 flex flex-col justify-around items-center gap-3">
          {/* Logo */}
          <div className="flex flex-col items-center text-center">
            <span className="text-4xl animate-bounce">👑</span>
            <div className="flex items-center gap-1 my-1">
              <span className="w-7 h-9 rounded bg-sky-500 border border-white text-white font-black text-lg flex items-center justify-center">B</span>
              <span className="w-7 h-9 rounded bg-rose-500 border border-white text-white font-black text-lg flex items-center justify-center">A</span>
              <span className="w-7 h-9 rounded bg-emerald-500 border border-white text-white font-black text-lg flex items-center justify-center">D</span>
              <span className="w-7 h-9 rounded bg-amber-500 border border-white text-white font-black text-lg flex items-center justify-center">A</span>
              <span className="w-7 h-9 rounded bg-purple-500 border border-white text-white font-black text-lg flex items-center justify-center">M</span>
              <div className="bg-amber-400 px-2 py-0.5 rounded border border-yellow-100 text-black font-black text-sm ml-1">
                KING
              </div>
            </div>
            <div className="bg-black/50 px-3 py-0.5 rounded-full border border-amber-400/40 text-[10px] text-amber-300 font-bold">
              🔴 6♥ {language === 'hi' ? 'असली बादाम छक्का' : 'Classic Indian Sevens'} 🎲
            </div>
          </div>

          {/* Quick Row: Play Online / Friends */}
          <div className="grid grid-cols-3 gap-2 w-full">
            <button
              onClick={() => {
                setGameMode('bot');
                setScreen('game');
                startRound('bot');
              }}
              className="p-2 rounded-xl bg-gradient-to-b from-amber-400 via-amber-500 to-amber-700 border-2 border-yellow-200 text-black font-black flex flex-col items-center shadow-lg active:scale-95"
            >
              <div className="w-9 h-9 rounded-full bg-blue-600 text-white flex items-center justify-center text-base border border-white">
                🌍
              </div>
              <span className="text-[11px] mt-1">{language === 'hi' ? 'ऑनलाइन' : 'ONLINE'}</span>
              <span className="text-[9px] font-bold text-neutral-900">🟢 187k</span>
            </button>

            <button
              onClick={() => {
                setGameMode('bot');
                setScreen('game');
                startRound('bot');
              }}
              className="p-2 rounded-xl bg-gradient-to-b from-amber-400 via-amber-500 to-amber-700 border-2 border-yellow-200 text-black font-black flex flex-col items-center shadow-lg active:scale-95"
            >
              <div className="w-9 h-9 rounded-full bg-amber-500 text-white flex items-center justify-center text-base border border-white">
                ⚔️
              </div>
              <span className="text-[11px] mt-1">{language === 'hi' ? 'टीम अप' : 'TEAM UP'}</span>
              <span className="text-[9px] font-bold text-neutral-900">🟢 6.9k</span>
            </button>

            <button
              onClick={() => {
                setGameMode('pass');
                setScreen('game');
                startRound('pass');
              }}
              className="p-2 rounded-xl bg-gradient-to-b from-amber-400 via-amber-500 to-amber-700 border-2 border-yellow-200 text-black font-black flex flex-col items-center shadow-lg active:scale-95"
            >
              <div className="w-9 h-9 rounded-full bg-rose-600 text-white flex items-center justify-center text-base border border-white">
                ❤️
              </div>
              <span className="text-[11px] mt-1">{language === 'hi' ? 'दोस्त' : 'FRIENDS'}</span>
              <span className="text-[9px] font-bold text-neutral-900">🟢 45k</span>
            </button>
          </div>

          {/* Wide Modes: VS Computer & Pass N Play */}
          <div className="grid grid-cols-2 gap-2.5 w-full">
            <button
              onClick={() => {
                setGameMode('bot');
                setScreen('game');
                startRound('bot');
              }}
              className="p-2.5 rounded-xl bg-gradient-to-b from-amber-400 to-amber-600 border-2 border-yellow-200 text-black font-black flex items-center gap-2 shadow-lg active:scale-95 text-left"
            >
              <div className="w-9 h-9 rounded-lg bg-indigo-700 border border-white flex items-center justify-center text-white text-base">
                🤖
              </div>
              <div>
                <div className="text-xs uppercase">{language === 'hi' ? 'कंप्यूटर' : 'VS COMPUTER'}</div>
                <div className="text-[9px] text-amber-950 font-bold">{language === 'hi' ? 'स्मार्ट AI बॉट्स' : 'Smart Bots'}</div>
              </div>
            </button>

            <button
              onClick={() => {
                setGameMode('pass');
                setScreen('game');
                startRound('pass');
              }}
              className="p-2.5 rounded-xl bg-gradient-to-b from-amber-400 to-amber-600 border-2 border-yellow-200 text-black font-black flex items-center gap-2 shadow-lg active:scale-95 text-left"
            >
              <div className="w-9 h-9 rounded-lg bg-emerald-700 border border-white flex items-center justify-center text-white text-base">
                👥
              </div>
              <div>
                <div className="text-xs uppercase">{language === 'hi' ? 'पास एंड प्ले' : 'PASS N PLAY'}</div>
                <div className="text-[9px] text-amber-950 font-bold">{language === 'hi' ? '१ फ़ोन पर खेलें' : '1 Phone Local'}</div>
              </div>
            </button>
          </div>

          {/* 7 Up Down & Tournament */}
          <div className="flex items-center gap-2 w-full">
            <button
              onClick={() => setIsSevenUpOpen(true)}
              className="flex flex-col items-center active:scale-95"
            >
              <div className="w-11 h-11 rounded-full bg-gradient-to-b from-amber-400 to-red-600 border-2 border-yellow-200 flex items-center justify-center text-xl shadow">
                🎰
              </div>
              <span className="text-[9px] font-black text-amber-300 mt-0.5">7 UP</span>
            </button>

            <button
              onClick={() => {
                setGameMode('bot');
                setScreen('game');
                startRound('bot');
              }}
              className="flex-1 py-2.5 px-3 rounded-xl bg-gradient-to-r from-amber-600 via-yellow-400 to-amber-600 border-2 border-yellow-100 text-black font-black flex items-center justify-between shadow-lg"
            >
              <div className="flex items-center gap-2">
                <span className="text-xl">🏆</span>
                <div className="text-left">
                  <div className="text-xs">{language === 'hi' ? 'टूर्नामेंट खेलें' : 'GRAND TOURNAMENT'}</div>
                  <div className="text-[9px] text-amber-950">{language === 'hi' ? 'कम स्कोर वाला जीतेगा' : 'Lowest Score Wins'}</div>
                </div>
              </div>
              <span className="text-base font-bold">▶</span>
            </button>
          </div>
        </main>

        {/* Bottom Bar */}
        <footer className="relative z-10 w-full bg-slate-950/80 px-4 py-2 flex items-center justify-around border-t border-white/10 text-xs">
          <button onClick={() => setIsSpinOpen(true)} className="flex flex-col items-center gap-0.5 text-amber-400 font-bold">
            <span>🎁</span>
            <span>{language === 'hi' ? 'स्पिन' : 'Spin'}</span>
          </button>
          <button onClick={() => setIsStoreOpen(true)} className="flex flex-col items-center gap-0.5 text-cyan-300 font-bold">
            <span>🛒</span>
            <span>{language === 'hi' ? 'दुकान' : 'Store'}</span>
          </button>
          <button onClick={() => setIsRulesOpen(true)} className="flex flex-col items-center gap-0.5 text-white">
            <span>📜</span>
            <span>{language === 'hi' ? 'नियम' : 'Rules'}</span>
          </button>
          <button onClick={() => setIsAndroidInfoOpen(true)} className="flex flex-col items-center gap-0.5 text-emerald-400 font-bold">
            <span>📱</span>
            <span>{language === 'hi' ? 'एंड्रॉयड APK' : 'Android'}</span>
          </button>
        </footer>

        {/* Dialogs rendered below */}
        {renderDialogs()}
      </div>
    );
  }

  // -------------------------------------------------------------
  // GAME SCREEN (PLAYING TABLE FELT)
  // -------------------------------------------------------------
  const opponents = players.filter((p) => p.id !== humanPlayer.id);

  return (
    <div className={`min-h-screen ${themeGradients[theme]} text-neutral-100 flex flex-col justify-between select-none font-sans p-2 sm:p-4`}>
      {/* Game Header */}
      <header className="flex items-center justify-between bg-black/60 backdrop-blur-md rounded-xl p-2 border border-amber-500/30">
        <div className="flex items-center gap-2">
          <button
            onClick={() => setScreen('lobby')}
            className="w-8 h-8 rounded-lg bg-neutral-800 text-amber-400 border border-amber-500/30 flex items-center justify-center"
          >
            <ArrowLeft size={16} />
          </button>
          <div>
            <div className="text-xs font-bold text-amber-300">
              {language === 'hi' ? `राउंड ${roundNumber}` : `Round ${roundNumber}`}
            </div>
            <div className="text-[10px] text-neutral-400">
              {gameMode === 'pass' ? 'Pass N Play' : 'Vs AI Bots'}
            </div>
          </div>
        </div>

        <div className="flex items-center gap-1.5">
          <button
            onClick={() => setLanguage((l) => (l === 'hi' ? 'en' : 'hi'))}
            className="px-2 py-1 rounded bg-slate-800 text-[10px] font-bold text-white border border-slate-600"
          >
            {language === 'hi' ? 'HI' : 'EN'}
          </button>
          <button
            onClick={() => setSoundEnabled(!soundEnabled)}
            className="p-1 rounded bg-slate-800 text-white border border-slate-600"
          >
            {soundEnabled ? <Volume2 size={14} /> : <VolumeX size={14} />}
          </button>
          <button
            onClick={() => {
              setRoundNumber((r) => r + 1);
              startRound();
            }}
            className="p-1 rounded bg-slate-800 text-amber-300 border border-slate-600"
            title="Next Round"
          >
            <RotateCcw size={14} />
          </button>
          <button
            onClick={() => setIsRulesOpen(true)}
            className="p-1 rounded bg-slate-800 text-white border border-slate-600"
          >
            <HelpCircle size={14} />
          </button>
        </div>
      </header>

      {/* Opponents Row */}
      <section className="flex items-center justify-between gap-1.5 my-1">
        {opponents.map((opp) => {
          const isTurn = currentTurn === opp.id;
          return (
            <div
              key={opp.id}
              className={`flex-1 flex flex-col items-center p-1.5 rounded-xl border transition-all ${
                isTurn
                  ? 'bg-amber-950/70 border-amber-400 shadow-md scale-102'
                  : 'bg-black/40 border-white/10'
              }`}
            >
              <div className="relative">
                <span className="text-xl">{opp.avatar}</span>
                {isTurn && (
                  <span className="absolute -top-1 -right-1 w-2.5 h-2.5 bg-amber-400 rounded-full animate-ping" />
                )}
              </div>
              <span className="text-[10px] font-bold text-white truncate max-w-[70px]">{opp.name}</span>
              <div className="flex items-center gap-1 text-[9px] text-amber-300 font-mono">
                <span>🎴 {opp.hand.length}</span>
                <span>•</span>
                <span>{scores[opp.id]}p</span>
              </div>
              {opp.lastAction && (
                <span
                  className={`text-[8px] font-bold px-1.5 py-0.2 rounded mt-0.5 ${
                    opp.lastAction.type === 'play'
                      ? 'bg-emerald-700 text-white'
                      : 'bg-rose-800 text-white'
                  }`}
                >
                  {opp.lastAction.type === 'play' && opp.lastAction.card
                    ? `${getRankLabel(opp.lastAction.card.rank)}${opp.lastAction.card.suit}`
                    : 'Pass'}
                </span>
              )}
            </div>
          );
        })}
      </section>

      {/* Felt Board */}
      <main className="w-full my-auto flex flex-col gap-1.5 bg-black/60 backdrop-blur-md rounded-2xl p-2.5 border border-amber-500/30">
        {/* 5♥ Status Alert */}
        <div className="flex items-center justify-between text-[10px] border-b border-white/10 pb-1">
          <div className="flex items-center gap-1 font-bold text-amber-300">
            <span>♠</span>
            <span>{language === 'hi' ? 'टेबल बोर्ड' : 'Table Board'}</span>
          </div>
          {!heartFiveOut ? (
            <div className="flex items-center gap-1 text-amber-300 bg-amber-950/80 px-2 py-0.5 rounded-full border border-amber-500/40">
              <ShieldAlert size={12} className="text-amber-400" />
              <span>{language === 'hi' ? '५♥ लॉक: पान ५ से पहले अन्य ५ बंद' : '5♥ Locked: 5♥ opens other 5s'}</span>
            </div>
          ) : (
            <div className="flex items-center gap-1 text-emerald-300 bg-emerald-950/80 px-2 py-0.5 rounded-full border border-emerald-500/40">
              <CheckCircle2 size={12} className="text-emerald-400" />
              <span>{language === 'hi' ? '५♥ खुला: सभी ५ सक्रिय' : '5♥ Played: All 5s unlocked'}</span>
            </div>
          )}
        </div>

        {/* 4 Suit Rows */}
        {SUITS.map((suit) => {
          const state = board[suit];
          const isOpen = state.low !== null;
          const isRed = suit === '♥' || suit === '♦';

          return (
            <div
              key={suit}
              className={`flex items-center gap-1.5 p-1 rounded-lg border ${
                isOpen ? 'bg-black/40 border-amber-500/20' : 'bg-black/20 border-white/5 opacity-80'
              }`}
            >
              {/* Suit icon */}
              <div
                className={`w-7 h-9 rounded flex items-center justify-center font-bold text-lg border ${
                  isRed ? 'bg-red-950/50 text-red-500 border-red-500/30' : 'bg-slate-900 text-white border-slate-700'
                }`}
              >
                {suit}
              </div>

              {/* Cards line */}
              <div className="flex-1 flex items-center gap-0.5 overflow-x-auto">
                {/* Low wing: 1 to 5 */}
                {[1, 2, 3, 4, 5].map((r) => {
                  const isPlayed = isOpen && state.low !== null && r >= state.low;
                  const playable = humanValidMoves.some((c) => c.suit === suit && c.rank === r);
                  return (
                    <div
                      key={r}
                      onClick={() => {
                        const c = humanValidMoves.find((x) => x.suit === suit && x.rank === r);
                        if (c) handlePlayCard(c);
                      }}
                      className={`w-6 h-8 rounded text-[9px] font-bold flex items-center justify-center border transition-all ${
                        isPlayed
                          ? isRed
                            ? 'bg-white text-red-600 border-red-300 shadow-sm'
                            : 'bg-white text-slate-900 border-slate-300 shadow-sm'
                          : playable
                          ? 'bg-amber-400 text-black border-yellow-200 animate-pulse cursor-pointer'
                          : 'bg-white/5 text-white/30 border-white/10'
                      }`}
                    >
                      {getRankLabel(r)}
                    </div>
                  );
                })}

                {/* Center 6 */}
                {(() => {
                  const isPlayed = isOpen;
                  const playable = humanValidMoves.some((c) => c.suit === suit && c.rank === 6);
                  return (
                    <div
                      onClick={() => {
                        const c = humanValidMoves.find((x) => x.suit === suit && x.rank === 6);
                        if (c) handlePlayCard(c);
                      }}
                      className={`w-7 h-9 rounded text-xs font-black flex items-center justify-center border-2 transition-all ${
                        isPlayed
                          ? isRed
                            ? 'bg-white text-red-600 border-amber-400 shadow'
                            : 'bg-white text-slate-900 border-amber-400 shadow'
                          : playable
                          ? 'bg-amber-400 text-black border-white animate-bounce cursor-pointer'
                          : 'bg-amber-950/30 text-amber-500/40 border-amber-500/20'
                      }`}
                    >
                      6
                    </div>
                  );
                })()}

                {/* High wing: 7 to 13 */}
                {[7, 8, 9, 10, 11, 12, 13].map((r) => {
                  const isPlayed = isOpen && state.high !== null && r <= state.high;
                  const playable = humanValidMoves.some((c) => c.suit === suit && c.rank === r);
                  return (
                    <div
                      key={r}
                      onClick={() => {
                        const c = humanValidMoves.find((x) => x.suit === suit && x.rank === r);
                        if (c) handlePlayCard(c);
                      }}
                      className={`w-6 h-8 rounded text-[9px] font-bold flex items-center justify-center border transition-all ${
                        isPlayed
                          ? isRed
                            ? 'bg-white text-red-600 border-red-300 shadow-sm'
                            : 'bg-white text-slate-900 border-slate-300 shadow-sm'
                          : playable
                          ? 'bg-amber-400 text-black border-yellow-200 animate-pulse cursor-pointer'
                          : 'bg-white/5 text-white/30 border-white/10'
                      }`}
                    >
                      {getRankLabel(r)}
                    </div>
                  );
                })}
              </div>
            </div>
          );
        })}
      </main>

      {/* Commentary Pill */}
      <div className="flex items-center justify-center my-1">
        <div className="bg-black/80 border border-amber-400/40 text-amber-300 text-xs px-3 py-1 rounded-full shadow">
          {statusMessage}
        </div>
      </div>

      {/* Human Player Hand Section */}
      <section className="bg-slate-950/80 rounded-2xl p-2.5 border border-amber-500/30 flex flex-col gap-2">
        <div className="flex items-center justify-between text-xs">
          <div className="flex items-center gap-1.5">
            <span className="text-base">{humanPlayer.avatar}</span>
            <span className="font-bold text-white">{humanPlayer.name}</span>
            <span className="text-neutral-400">({humanPlayer.hand.length} {language === 'hi' ? 'पत्ते' : 'cards'})</span>
          </div>

          <div className="flex items-center gap-1 text-[11px]">
            <span className="text-amber-400 font-bold">{scores[humanPlayer.id]} pts</span>
          </div>
        </div>

        {/* Hand Cards */}
        <div className="flex items-center gap-1 overflow-x-auto pb-1">
          {humanPlayer.hand.length === 0 ? (
            <div className="w-full text-center py-4 text-sm font-bold text-amber-300">
              🎉 {language === 'hi' ? 'सभी पत्ते समाप्त!' : 'All cards cleared!'}
            </div>
          ) : (
            humanPlayer.hand.map((card) => {
              const isPlayable = isHumanTurn && humanValidMoves.some((c) => c.id === card.id);
              const isRed = card.suit === '♥' || card.suit === '♦';

              return (
                <button
                  key={card.id}
                  onClick={() => {
                    if (isPlayable) handlePlayCard(card);
                  }}
                  className={`w-10 h-15 rounded-lg border-2 flex flex-col justify-between p-1 shrink-0 transition-transform active:scale-95 ${
                    isPlayable
                      ? 'bg-amber-100 border-amber-500 -translate-y-1 shadow-lg cursor-pointer ring-2 ring-amber-400'
                      : 'bg-white border-slate-300 opacity-90'
                  }`}
                >
                  <div className={`text-[11px] font-black leading-none ${isRed ? 'text-red-600' : 'text-slate-900'}`}>
                    {getRankLabel(card.rank)}
                  </div>
                  <div className={`text-base leading-none text-center ${isRed ? 'text-red-600' : 'text-slate-900'}`}>
                    {card.suit}
                  </div>
                  <div className={`text-[9px] font-black leading-none self-end ${isRed ? 'text-red-600' : 'text-slate-900'}`}>
                    {getRankLabel(card.rank)}
                  </div>
                </button>
              );
            })
          )}
        </div>

        {/* Actions: Pass */}
        <div className="flex items-center gap-2">
          <button
            onClick={() => {
              if (humanValidMoves.length > 0) {
                setStatusMessage(`Playable: ${humanValidMoves.map((c) => `${getRankLabel(c.rank)}${c.suit}`).join(', ')}`);
              }
            }}
            className="flex-1 py-1.5 rounded-lg border border-amber-400 text-amber-300 font-bold text-xs"
          >
            💡 {language === 'hi' ? 'संकेत' : 'Hint'} ({humanValidMoves.length})
          </button>

          <button
            onClick={handlePass}
            disabled={!isHumanTurn || humanValidMoves.length > 0}
            className={`flex-1 py-1.5 rounded-lg font-bold text-xs transition-colors ${
              isHumanTurn && humanValidMoves.length === 0
                ? 'bg-red-600 text-white cursor-pointer active:scale-95'
                : 'bg-slate-800 text-slate-500 cursor-not-allowed'
            }`}
          >
            ✖ {language === 'hi' ? 'पास (Pass)' : 'Pass'}
          </button>
        </div>
      </section>

      {/* Dialogs rendered below */}
      {renderDialogs()}
    </div>
  );

  // -------------------------------------------------------------
  // DIALOGS & MODALS HELPER
  // -------------------------------------------------------------
  function renderDialogs() {
    return (
      <>
        {/* Rules Dialog */}
        {isRulesOpen && (
          <div className="fixed inset-0 z-50 bg-black/70 flex items-center justify-center p-4">
            <div className="bg-slate-900 border-2 border-amber-400 rounded-2xl max-w-sm w-full p-4 max-h-[85vh] overflow-y-auto">
              <div className="flex items-center justify-between border-b border-white/10 pb-2">
                <span className="font-bold text-amber-300 text-sm">
                  {language === 'hi' ? 'बादाम छक्का के नियम' : 'Badam Chhakka Rules'}
                </span>
                <button onClick={() => setIsRulesOpen(false)} className="text-white">
                  <X size={18} />
                </button>
              </div>

              <div className="text-xs text-neutral-300 flex flex-col gap-2.5 mt-3 leading-relaxed">
                <div className="bg-slate-800 p-2 rounded-lg">
                  <strong className="text-amber-400 block mb-0.5">1. {language === 'hi' ? '६♥ से शुरुआत' : 'Start with 6♥'}</strong>
                  {language === 'hi'
                    ? 'जिस खिलाड़ी के पास पान का छक्का (6♥) है, वही अनिवार्य रूप से इसे चलकर राउंड शुरू करता है।'
                    : 'The player with 6 of Hearts (6♥) must play it first.'}
                </div>
                <div className="bg-slate-800 p-2 rounded-lg">
                  <strong className="text-amber-400 block mb-0.5">2. {language === 'hi' ? 'अन्य सूट ६ से खुलते हैं' : 'Suits open with 6'}</strong>
                  {language === 'hi'
                    ? 'ईंट, चिड़ी या हुकुम के पत्ते चलने के लिए पहले उस सूट का ६ चलना ज़रूरी है।'
                    : 'To play Diamonds, Clubs, or Spades, that suit must be opened with its 6.'}
                </div>
                <div className="bg-slate-800 p-2 rounded-lg">
                  <strong className="text-amber-400 block mb-0.5">3. {language === 'hi' ? '५♥ का प्रसिद्ध ब्लॉकिंग नियम!' : '5♥ Blocking Rule'}</strong>
                  {language === 'hi'
                    ? 'विशेष नियम: जब तक टेबल पर ५♥ नहीं चला जाता, तब तक किसी अन्य सूट का ५ नहीं चला जा सकता!'
                    : 'No other suit can play its 5 until the 5 of Hearts has been played!'}
                </div>
                <div className="bg-slate-800 p-2 rounded-lg">
                  <strong className="text-amber-400 block mb-0.5">4. {language === 'hi' ? 'पॉइंट व पेनल्टी' : 'Scoring'}</strong>
                  {language === 'hi'
                    ? 'जो खिलाड़ी अपने सभी पत्ते पहले समाप्त कर लेता है उसे ० पॉइंट मिलते हैं। बाकी सभी को बचे पत्तों के मान बराबर पेनल्टी मिलती है (A=1, K=13)। कम स्कोर वाला जीतता है!'
                    : 'Winner gets 0 points. Opponents receive penalty points equal to leftover card ranks (A=1, K=13). Lowest total score wins!'}
                </div>
              </div>

              <button
                onClick={() => setIsRulesOpen(false)}
                className="w-full mt-3 py-2 bg-amber-400 text-black font-bold text-xs rounded-xl"
              >
                {language === 'hi' ? 'समझ गया' : 'Got It'}
              </button>
            </div>
          </div>
        )}

        {/* 7 Up Down Minigame */}
        {isSevenUpOpen && (
          <div className="fixed inset-0 z-50 bg-black/70 flex items-center justify-center p-4">
            <div className="bg-slate-900 border-2 border-amber-400 rounded-2xl max-w-sm w-full p-4 flex flex-col items-center gap-3">
              <div className="flex items-center justify-between w-full border-b border-white/10 pb-2">
                <span className="font-bold text-amber-300 text-sm">🎰 7 Up 7 Down</span>
                <button onClick={() => setIsSevenUpOpen(false)} className="text-white">
                  <X size={18} />
                </button>
              </div>

              <div className="text-sm font-bold text-amber-400">🪙 {coins.toLocaleString()} Coins</div>

              {/* Card display */}
              <div className="w-16 h-22 rounded-xl bg-slate-800 border-2 border-amber-400 flex items-center justify-center text-3xl font-bold">
                {sevenDrawn ? (
                  <span className={sevenDrawn.suit === '♥' || sevenDrawn.suit === '♦' ? 'text-red-500' : 'text-white'}>
                    {getRankLabel(sevenDrawn.rank)}{sevenDrawn.suit}
                  </span>
                ) : (
                  '🎴'
                )}
              </div>

              {sevenMsg && <div className="text-xs font-bold text-amber-300 text-center">{sevenMsg}</div>}

              {/* 3 Choices */}
              <div className="grid grid-cols-3 gap-2 w-full text-center">
                <button
                  onClick={() => setSevenBetChoice('down')}
                  className={`p-2 rounded-xl border text-xs font-bold ${
                    sevenBetChoice === 'down' ? 'bg-amber-400 text-black border-white' : 'bg-slate-800 text-white border-white/10'
                  }`}
                >
                  <div>7 Down</div>
                  <div className="text-[10px] text-amber-400">(2-6) 2x</div>
                </button>
                <button
                  onClick={() => setSevenBetChoice('seven')}
                  className={`p-2 rounded-xl border text-xs font-bold ${
                    sevenBetChoice === 'seven' ? 'bg-amber-400 text-black border-white' : 'bg-slate-800 text-white border-white/10'
                  }`}
                >
                  <div>Lucky 7</div>
                  <div className="text-[10px] text-amber-400">(7) 5x 🔥</div>
                </button>
                <button
                  onClick={() => setSevenBetChoice('up')}
                  className={`p-2 rounded-xl border text-xs font-bold ${
                    sevenBetChoice === 'up' ? 'bg-amber-400 text-black border-white' : 'bg-slate-800 text-white border-white/10'
                  }`}
                >
                  <div>7 Up</div>
                  <div className="text-[10px] text-amber-400">(8-K) 2x</div>
                </button>
              </div>

              {/* Bet Amounts */}
              <div className="flex items-center gap-2">
                {[50, 100, 250, 500].map((amt) => (
                  <button
                    key={amt}
                    onClick={() => setSevenBetAmt(amt)}
                    className={`px-2.5 py-1 rounded text-xs font-bold ${
                      sevenBetAmt === amt ? 'bg-amber-400 text-black' : 'bg-slate-800 text-white'
                    }`}
                  >
                    {amt}
                  </button>
                ))}
              </div>

              {/* Roll button */}
              <button
                disabled={!sevenBetChoice || coins < sevenBetAmt}
                onClick={() => {
                  if (coins < sevenBetAmt || !sevenBetChoice) return;
                  setCoins((c) => c - sevenBetAmt);
                  const suits: Suit[] = ['♥', '♦', '♣', '♠'];
                  const randRank = Math.floor(Math.random() * 13) + 1;
                  const randSuit = suits[Math.floor(Math.random() * 4)];
                  const drawn: Card = { id: `draw-${Date.now()}`, suit: randSuit, rank: randRank };
                  setSevenDrawn(drawn);

                  const isWin =
                    (sevenBetChoice === 'down' && randRank < 7) ||
                    (sevenBetChoice === 'up' && randRank > 7) ||
                    (sevenBetChoice === 'seven' && randRank === 7);

                  const mult = sevenBetChoice === 'seven' ? 5 : 2;
                  if (isWin) {
                    const wonCoins = sevenBetAmt * mult;
                    setCoins((c) => c + wonCoins);
                    setSevenMsg(language === 'hi' ? `शानदार! आप जीते ${wonCoins} कॉइन्स!` : `Winner! Won ${wonCoins} Coins!`);
                  } else {
                    setSevenMsg(language === 'hi' ? `कोई बात नहीं! पत्ता था ${getRankLabel(randRank)}${randSuit}` : `Better luck! Card was ${getRankLabel(randRank)}${randSuit}`);
                  }
                }}
                className="w-full py-2 bg-amber-400 text-black font-bold text-xs rounded-xl disabled:opacity-50"
              >
                {language === 'hi' ? 'रोल करें' : 'Draw Card'}
              </button>
            </div>
          </div>
        )}

        {/* Spin Dialog */}
        {isSpinOpen && (
          <div className="fixed inset-0 z-50 bg-black/70 flex items-center justify-center p-4">
            <div className="bg-slate-900 border-2 border-amber-400 rounded-2xl max-w-sm w-full p-4 flex flex-col items-center gap-3">
              <div className="flex items-center justify-between w-full border-b border-white/10 pb-2">
                <span className="font-bold text-amber-300 text-sm">🎁 {language === 'hi' ? 'दैनिक स्पिन' : 'Daily Free Spin'}</span>
                <button onClick={() => setIsSpinOpen(false)} className="text-white">
                  <X size={18} />
                </button>
              </div>

              <div className="text-4xl my-2">🎡</div>

              <button
                onClick={() => {
                  const rew = [500, 1000, 1500, 2000][Math.floor(Math.random() * 4)];
                  setCoins((c) => c + rew);
                  setGems((g) => g + 5);
                  alert(language === 'hi' ? `बधाई! आपको मिले +${rew} कॉइन्स और +5 रत्न!` : `Congratulations! You received +${rew} Coins and +5 Gems!`);
                  setIsSpinOpen(false);
                }}
                className="w-full py-2 bg-amber-400 text-black font-bold text-xs rounded-xl"
              >
                {language === 'hi' ? 'मुफ़्त स्पिन करें' : 'Spin Now'}
              </button>
            </div>
          </div>
        )}

        {/* Store Dialog */}
        {isStoreOpen && (
          <div className="fixed inset-0 z-50 bg-black/70 flex items-center justify-center p-4">
            <div className="bg-slate-900 border-2 border-amber-400 rounded-2xl max-w-sm w-full p-4 flex flex-col gap-3">
              <div className="flex items-center justify-between border-b border-white/10 pb-2">
                <span className="font-bold text-amber-300 text-sm">🛒 {language === 'hi' ? 'स्टोर' : 'Store'}</span>
                <button onClick={() => setIsStoreOpen(false)} className="text-white">
                  <X size={18} />
                </button>
              </div>

              <div className="flex flex-col gap-2 text-xs">
                <div className="p-2.5 rounded-lg bg-slate-800 flex items-center justify-between">
                  <div>
                    <div className="font-bold text-white">{language === 'hi' ? 'मुफ़्त वीडियो बोनस' : 'Free Video Bonus'}</div>
                    <div className="text-amber-400">+500 🪙 +5 💎</div>
                  </div>
                  <button
                    onClick={() => {
                      setCoins((c) => c + 500);
                      setGems((g) => g + 5);
                      alert('Bonus Claimed!');
                    }}
                    className="px-3 py-1 bg-emerald-500 text-black font-bold rounded-lg text-xs"
                  >
                    Claim
                  </button>
                </div>

                <div className="p-2.5 rounded-lg bg-slate-800 flex items-center justify-between">
                  <div>
                    <div className="font-bold text-white">{language === 'hi' ? 'शुरुआती पैक' : 'Starter Pack'}</div>
                    <div className="text-amber-400">+2,000 🪙 +25 💎</div>
                  </div>
                  <button
                    onClick={() => {
                      setCoins((c) => c + 2000);
                      setGems((g) => g + 25);
                      alert('Starter Pack Added!');
                    }}
                    className="px-3 py-1 bg-amber-400 text-black font-bold rounded-lg text-xs"
                  >
                    ₹25
                  </button>
                </div>
              </div>
            </div>
          </div>
        )}

        {/* Round Summary Dialog */}
        {isSummaryOpen && winnerIndex !== null && (
          <div className="fixed inset-0 z-50 bg-black/70 flex items-center justify-center p-4">
            <div className="bg-slate-900 border-2 border-amber-400 rounded-2xl max-w-sm w-full p-4 flex flex-col gap-3">
              <div className="text-center">
                <span className="text-3xl">🏆</span>
                <h3 className="font-black text-amber-300 text-sm mt-1">
                  {players[winnerIndex]?.name} {language === 'hi' ? 'राउंड जीता!' : 'Won Round!'}
                </h3>
              </div>

              <div className="flex flex-col gap-1.5 text-xs">
                {players.map((p, idx) => {
                  const isWin = idx === winnerIndex;
                  const pen = isWin ? 0 : p.hand.reduce((acc, c) => acc + c.rank, 0);
                  return (
                    <div
                      key={p.id}
                      className={`p-2 rounded-lg flex items-center justify-between ${
                        isWin ? 'bg-emerald-950/60 border border-emerald-500' : 'bg-slate-800'
                      }`}
                    >
                      <div className="flex items-center gap-1.5">
                        <span>{p.avatar}</span>
                        <span className="font-bold text-white">{p.name}</span>
                      </div>
                      <div className="text-right font-mono">
                        <span className={isWin ? 'text-emerald-400 font-bold' : 'text-rose-400'}>+{pen}p</span>
                        <span className="text-neutral-400 text-[10px] block">Total: {scores[idx]}p</span>
                      </div>
                    </div>
                  );
                })}
              </div>

              <div className="flex items-center gap-2">
                <button
                  onClick={() => {
                    setScores([0, 0, 0, 0]);
                    setRoundNumber(1);
                    startRound();
                  }}
                  className="flex-1 py-2 bg-slate-800 text-neutral-300 font-bold text-xs rounded-xl"
                >
                  {language === 'hi' ? 'रीसेट' : 'Reset'}
                </button>
                <button
                  onClick={() => {
                    setRoundNumber((r) => r + 1);
                    startRound();
                  }}
                  className="flex-1 py-2 bg-amber-400 text-black font-bold text-xs rounded-xl"
                >
                  {language === 'hi' ? 'अगला राउंड' : 'Next Round'}
                </button>
              </div>
            </div>
          </div>
        )}

        {/* Android Info Modal */}
        {isAndroidInfoOpen && (
          <div className="fixed inset-0 z-50 bg-black/70 flex items-center justify-center p-4">
            <div className="bg-slate-900 border-2 border-emerald-400 rounded-2xl max-w-sm w-full p-4 flex flex-col gap-2.5 text-xs text-neutral-200">
              <div className="flex items-center justify-between border-b border-white/10 pb-2">
                <span className="font-bold text-emerald-400 text-sm flex items-center gap-1">
                  <Smartphone size={16} /> Native Android APK Project
                </span>
                <button onClick={() => setIsAndroidInfoOpen(false)} className="text-white">
                  <X size={18} />
                </button>
              </div>

              <p className="text-neutral-300 leading-relaxed">
                The native Android version of this app is completely implemented in <strong className="text-white">Kotlin & Jetpack Compose</strong> under the <code className="bg-black/50 px-1 rounded text-amber-300">/app</code> directory!
              </p>

              <div className="bg-slate-800 p-2.5 rounded-lg flex flex-col gap-1 font-mono text-[11px]">
                <div>• <span className="text-emerald-300">Package:</span> com.badamchhakka.app</div>
                <div>• <span className="text-emerald-300">Application ID:</span> com.aistudio.badamchhakka.kxvyqp</div>
                <div>• <span className="text-emerald-300">UI Toolkit:</span> Jetpack Compose & M3</div>
                <div>• <span className="text-emerald-300">Architecture:</span> MVVM + Kotlin StateFlow</div>
                <div>• <span className="text-emerald-300">GitHub Actions:</span> Automated APK Build (.github/workflows/build-apk.yml)</div>
              </div>

              <button
                onClick={() => setIsAndroidInfoOpen(false)}
                className="w-full mt-1 py-2 bg-emerald-500 text-black font-bold rounded-xl"
              >
                Close
              </button>
            </div>
          </div>
        )}
      </>
    );
  }
}
