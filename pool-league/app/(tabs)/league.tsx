import { useState, useRef, useCallback } from 'react';
import {
  View, Text, ScrollView, TouchableOpacity,
  Animated, NativeSyntheticEvent, NativeScrollEvent,
  FlatList,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

// ─── Types ────────────────────────────────────────────────────────────────────

type League = {
  id: string;
  name: string;
  initials: string;
  colour: string;
  textColour: string;
  venue: string;
  teamCount: number;
  status: 'active' | 'completed';
  myRole: 'playing' | 'following';
};

type TeamRow = {
  pos: number;
  name: string;
  isMe: boolean;
  played: number;
  won: number;
  lost: number;
  drawn: number;
  points: number;
  form: ('W' | 'L' | 'D')[];
};

type PlayerRow = {
  pos: number;
  name: string;
  isMe: boolean;
  framesPlayed: number;
  framesWon: number;
  winPct: number;
  breakDishes: number;
  breakDishPct: number;
};

type Fixture = {
  id: string;
  date: string;        // ISO date string
  dateLabel: string;   // e.g. "Thu 19 Jun"
  week: string;
  type: string;
  home: string;
  away: string;
  homeIsMe: boolean;
  awayIsMe: boolean;
  homeScore?: number;
  awayScore?: number;
  result?: 'W' | 'L' | 'D';   // from current user's perspective
};

// ─── Mock data ────────────────────────────────────────────────────────────────

const LEAGUES: League[] = [
  { id: '1', name: 'Division A', initials: 'DA', colour: '#1e3a5f', textColour: '#60a5fa', venue: 'The Crown', teamCount: 12, status: 'active', myRole: 'playing' },
  { id: '2', name: 'Division B', initials: 'DB', colour: '#2d1b4e', textColour: '#c084fc', venue: 'The Crown', teamCount: 10, status: 'active', myRole: 'following' },
];

const TEAMS: TeamRow[] = [
  { pos: 1, name: 'The Crown A', isMe: true,  played: 10, won: 7, lost: 2, drawn: 1, points: 22, form: ['W','W','L','W','D'] },
  { pos: 2, name: 'Red Lion A',  isMe: false, played: 10, won: 6, lost: 3, drawn: 1, points: 19, form: ['W','L','W','W','W'] },
  { pos: 3, name: 'Anchor B',    isMe: false, played: 10, won: 5, lost: 3, drawn: 2, points: 17, form: ['L','W','W','D','W'] },
  { pos: 4, name: "King's Head", isMe: false, played: 10, won: 4, lost: 4, drawn: 2, points: 14, form: ['W','L','L','W','L'] },
  { pos: 5, name: 'Swan A',      isMe: false, played: 10, won: 3, lost: 5, drawn: 2, points: 11, form: ['L','L','W','L','W'] },
  { pos: 6, name: 'The Plough',  isMe: false, played: 10, won: 2, lost: 6, drawn: 2, points:  8, form: ['L','W','L','L','L'] },
];

const PLAYERS: PlayerRow[] = [
  { pos: 1, name: 'J. Davies',   isMe: true,  framesPlayed: 32, framesWon: 22, winPct: 68.8, breakDishes: 9,  breakDishPct: 28.1 },
  { pos: 2, name: 'R. Hughes',   isMe: false, framesPlayed: 30, framesWon: 20, winPct: 66.7, breakDishes: 7,  breakDishPct: 23.3 },
  { pos: 3, name: 'S. Owen',     isMe: false, framesPlayed: 28, framesWon: 18, winPct: 64.3, breakDishes: 10, breakDishPct: 35.7 },
  { pos: 4, name: 'M. Patel',    isMe: false, framesPlayed: 31, framesWon: 19, winPct: 61.3, breakDishes: 6,  breakDishPct: 19.4 },
  { pos: 5, name: 'T. Walsh',    isMe: false, framesPlayed: 29, framesWon: 17, winPct: 58.6, breakDishes: 5,  breakDishPct: 17.2 },
  { pos: 6, name: 'G. Brooks',   isMe: false, framesPlayed: 27, framesWon: 15, winPct: 55.6, breakDishes: 4,  breakDishPct: 14.8 },
];

// Fixtures split into past and upcoming — today is the anchor
const PAST_FIXTURES: Fixture[] = [
  { id: 'p1', date: '2025-06-05', dateLabel: 'Thu 5 Jun',  week: 'Week 2', type: 'Singles', home: 'J. Davies',  away: 'T. Walsh',   homeIsMe: true,  awayIsMe: false, homeScore: 5, awayScore: 3, result: 'W' },
  { id: 'p2', date: '2025-06-05', dateLabel: 'Thu 5 Jun',  week: 'Week 2', type: 'Singles', home: 'R. Hughes',  away: 'S. Owen',    homeIsMe: false, awayIsMe: false, homeScore: 4, awayScore: 5 },
  { id: 'p3', date: '2025-06-12', dateLabel: 'Thu 12 Jun', week: 'Week 3', type: 'Singles', home: 'J. Davies',  away: 'M. Patel',   homeIsMe: true,  awayIsMe: false, homeScore: 5, awayScore: 4, result: 'W' },
  { id: 'p4', date: '2025-06-12', dateLabel: 'Thu 12 Jun', week: 'Week 3', type: 'Team',    home: 'Crown A',    away: 'Red Lion A', homeIsMe: true,  awayIsMe: false, homeScore: 4, awayScore: 7, result: 'L' },
];

const UPCOMING_FIXTURES: Fixture[] = [
  { id: 'u1', date: '2025-06-19', dateLabel: 'Thu 19 Jun', week: 'Week 4', type: 'Singles', home: 'J. Davies', away: 'R. Hughes',  homeIsMe: true,  awayIsMe: false },
  { id: 'u2', date: '2025-06-19', dateLabel: 'Thu 19 Jun', week: 'Week 4', type: 'Singles', home: 'S. Owen',   away: 'T. Walsh',   homeIsMe: false, awayIsMe: false },
  { id: 'u3', date: '2025-06-19', dateLabel: 'Thu 19 Jun', week: 'Week 4', type: 'Singles', home: 'M. Patel',  away: 'G. Brooks',  homeIsMe: false, awayIsMe: false },
  { id: 'u4', date: '2025-06-26', dateLabel: 'Thu 26 Jun', week: 'Week 5', type: 'Team',    home: 'Crown A',   away: 'Anchor B',   homeIsMe: true,  awayIsMe: false },
  { id: 'u5', date: '2025-06-26', dateLabel: 'Thu 26 Jun', week: 'Week 5', type: 'Singles', home: 'R. Hughes', away: 'M. Patel',   homeIsMe: false, awayIsMe: false },
];

// ─── Helpers ──────────────────────────────────────────────────────────────────

const DOT_COLOUR: Record<string, string> = { W: '#16a34a', L: '#b91c1c', D: '#b45309' };
const RESULT_BORDER: Record<string, string> = { W: '#16a34a', L: '#b91c1c', D: '#b45309' };

function groupByDate<T extends { dateLabel: string }>(items: T[]): { label: string; items: T[] }[] {
  const map = new Map<string, T[]>();
  items.forEach(item => {
    if (!map.has(item.dateLabel)) map.set(item.dateLabel, []);
    map.get(item.dateLabel)!.push(item);
  });
  return Array.from(map.entries()).map(([label, items]) => ({ label, items }));
}

// ─── Sub-components ───────────────────────────────────────────────────────────

function FormDots({ form }: { form: ('W' | 'L' | 'D')[] }) {
  return (
    <View className="flex-row gap-0.5 justify-center">
      {form.map((f, i) => (
        <View key={i} style={{ width: 7, height: 7, borderRadius: 4, backgroundColor: DOT_COLOUR[f] }} />
      ))}
    </View>
  );
}

function FixtureCard({ fixture }: { fixture: Fixture }) {
  const isPast = fixture.homeScore !== undefined;
  const borderColour = fixture.result ? RESULT_BORDER[fixture.result] : 'transparent';

  return (
    <TouchableOpacity
      activeOpacity={0.7}
      style={{ borderLeftWidth: isPast ? 2 : 0, borderLeftColor: borderColour, borderRadius: 10, marginBottom: 6 }}
    >
      <View className="bg-zinc-900 border border-zinc-800 rounded-xl p-3"
        style={{ borderTopLeftRadius: isPast ? 0 : 10, borderBottomLeftRadius: isPast ? 0 : 10 }}>
        <Text className="text-zinc-600 text-xs mb-1.5">{fixture.week} · {fixture.type}</Text>
        <View className="flex-row items-center justify-between">
          <Text
            className="text-sm flex-1"
            style={{ color: fixture.homeIsMe ? '#60a5fa' : '#fff' }}
          >
            {fixture.home}
          </Text>
          {isPast ? (
            <View className="flex-row items-center gap-1.5 px-2">
              <Text className="text-white text-base font-medium">{fixture.homeScore}</Text>
              <Text className="text-zinc-600 text-sm">–</Text>
              <Text className="text-white text-base font-medium">{fixture.awayScore}</Text>
            </View>
          ) : (
            <Text className="text-zinc-600 text-xs px-3">8:00 pm</Text>
          )}
          <Text
            className="text-sm flex-1 text-right"
            style={{ color: fixture.awayIsMe ? '#60a5fa' : '#fff' }}
          >
            {fixture.away}
          </Text>
        </View>
      </View>
    </TouchableOpacity>
  );
}

// ─── Fixtures tab (anchored scroll) ───────────────────────────────────────────

function FixturesTab() {
  const pastHintOpacity = useRef(new Animated.Value(1)).current;
  const futureHintOpacity = useRef(new Animated.Value(1)).current;
  const hasScrolled = useRef(false);

  const onScroll = useCallback((e: NativeSyntheticEvent<NativeScrollEvent>) => {
    if (!hasScrolled.current) {
      hasScrolled.current = true;
      Animated.parallel([
        Animated.timing(pastHintOpacity, { toValue: 0, duration: 300, useNativeDriver: true }),
        Animated.timing(futureHintOpacity, { toValue: 0, duration: 300, useNativeDriver: true }),
      ]).start();
    }
  }, []);

  const pastGroups = groupByDate(PAST_FIXTURES);
  const upcomingGroups = groupByDate(UPCOMING_FIXTURES);

  return (
    <ScrollView
      showsVerticalScrollIndicator={false}
      onScroll={onScroll}
      scrollEventThrottle={16}
    >
      {/* Past hint */}
      <Animated.View style={{ opacity: pastHintOpacity }} className="items-center py-2 gap-0.5">
        <Text className="text-zinc-600 text-xs">↑</Text>
        <Text className="text-zinc-600 text-xs">Past results above</Text>
      </Animated.View>

      {/* Past fixtures */}
      {pastGroups.map(group => (
        <View key={group.label} className="px-3">
          <Text className="text-zinc-600 text-xs uppercase tracking-wider py-2">{group.label}</Text>
          {group.items.map(f => <FixtureCard key={f.id} fixture={f} />)}
        </View>
      ))}

      {/* Today anchor */}
      <View className="flex-row items-center gap-2 px-3 py-3 bg-zinc-950 border-t border-b border-zinc-800">
        <View className="flex-1 h-px bg-zinc-700" />
        <Text className="text-zinc-400 text-xs">Today · 19 Jun</Text>
        <View className="flex-1 h-px bg-zinc-700" />
      </View>

      {/* Upcoming fixtures */}
      {upcomingGroups.map(group => (
        <View key={group.label} className="px-3">
          <Text className="text-zinc-600 text-xs uppercase tracking-wider py-2">{group.label}</Text>
          {group.items.map(f => <FixtureCard key={f.id} fixture={f} />)}
        </View>
      ))}

      {/* Future hint */}
      <Animated.View style={{ opacity: futureHintOpacity }} className="items-center py-2 gap-0.5">
        <Text className="text-zinc-600 text-xs">More fixtures below</Text>
        <Text className="text-zinc-600 text-xs">↓</Text>
      </Animated.View>

      <View className="h-6" />
    </ScrollView>
  );
}

// ─── Teams tab ────────────────────────────────────────────────────────────────

function TeamsTab() {
  return (
    <ScrollView showsVerticalScrollIndicator={false}>
      <View className="px-3 pt-2 pb-6">
        {/* Header row */}
        <View className="flex-row py-2 border-b border-zinc-800 mb-1">
          <Text className="text-zinc-500 text-xs w-5">#</Text>
          <Text className="text-zinc-500 text-xs flex-1">Team</Text>
          {['P','W','L','Pts'].map(h => (
            <Text key={h} className="text-zinc-500 text-xs w-7 text-center">{h}</Text>
          ))}
          <Text className="text-zinc-500 text-xs w-20 text-center">Form</Text>
        </View>

        {TEAMS.map(team => (
          <TouchableOpacity
            key={team.pos}
            activeOpacity={0.7}
            className="flex-row items-center py-2.5 border-b border-zinc-900"
          >
            <Text className="text-zinc-500 text-xs w-5">{team.pos}</Text>
            <Text className="text-sm flex-1" style={{ color: team.isMe ? '#60a5fa' : '#fff' }}>
              {team.name}
            </Text>
            <Text className="text-zinc-400 text-xs w-7 text-center">{team.played}</Text>
            <Text className="text-zinc-400 text-xs w-7 text-center">{team.won}</Text>
            <Text className="text-zinc-400 text-xs w-7 text-center">{team.lost}</Text>
            <Text className="text-white text-xs font-medium w-7 text-center">{team.points}</Text>
            <View className="w-20 items-center">
              <FormDots form={team.form} />
            </View>
          </TouchableOpacity>
        ))}
      </View>
    </ScrollView>
  );
}

// ─── Players tab ──────────────────────────────────────────────────────────────

function PlayersTab() {
  return (
    <ScrollView showsVerticalScrollIndicator={false}>
      <View className="px-3 pt-2 pb-6">
        {/* Header */}
        <View className="flex-row py-2 border-b border-zinc-800 mb-1">
          <Text className="text-zinc-500 text-xs w-5">#</Text>
          <Text className="text-zinc-500 text-xs flex-1">Player</Text>
          <Text className="text-zinc-500 text-xs w-7 text-center">FP</Text>
          <Text className="text-zinc-500 text-xs w-7 text-center">FW</Text>
          <Text className="text-zinc-500 text-xs w-10 text-center">Win%</Text>
          <Text className="text-zinc-500 text-xs w-7 text-center">BD</Text>
          <Text className="text-zinc-500 text-xs w-10 text-center">BD%</Text>
        </View>

        {PLAYERS.map(player => (
          <TouchableOpacity
            key={player.pos}
            activeOpacity={0.7}
            className="flex-row items-center py-2.5 border-b border-zinc-900"
          >
            <Text className="text-zinc-500 text-xs w-5">{player.pos}</Text>
            <Text className="text-sm flex-1" style={{ color: player.isMe ? '#60a5fa' : '#fff' }}>
              {player.name}
            </Text>
            <Text className="text-zinc-400 text-xs w-7 text-center">{player.framesPlayed}</Text>
            <Text className="text-zinc-400 text-xs w-7 text-center">{player.framesWon}</Text>
            <Text className="text-white text-xs font-medium w-10 text-center">{player.winPct}%</Text>
            <Text className="text-zinc-400 text-xs w-7 text-center">{player.breakDishes}</Text>
            <Text className="text-zinc-400 text-xs w-10 text-center">{player.breakDishPct}%</Text>
          </TouchableOpacity>
        ))}

        {/* Legend */}
        <View className="mt-4 gap-1">
          <Text className="text-zinc-600 text-xs">FP = Frames Played · FW = Frames Won</Text>
          <Text className="text-zinc-600 text-xs">BD = Break Dishes (incl. reverse) · BD% = Break dish rate</Text>
        </View>
      </View>
    </ScrollView>
  );
}

// ─── League detail (3 tabs) ───────────────────────────────────────────────────

function LeagueDetail({ league, onBack }: { league: League; onBack: () => void }) {
  const [activeTab, setActiveTab] = useState<'teams' | 'players' | 'fixtures'>('teams');

  return (
    <SafeAreaView className="flex-1 bg-zinc-950" edges={['top']}>
      {/* Header */}
      <View className="flex-row items-center px-4 py-3 border-b border-zinc-800">
        <TouchableOpacity onPress={onBack} activeOpacity={0.7} className="mr-3">
          <Text className="text-blue-400 text-base">‹</Text>
        </TouchableOpacity>
        <View className="flex-1">
          <Text className="text-white text-base font-medium">{league.name}</Text>
          <Text className="text-zinc-500 text-xs">{league.venue} · {league.teamCount} teams</Text>
        </View>
      </View>

      {/* Tab bar */}
      <View className="flex-row border-b border-zinc-800">
        {(['teams', 'players', 'fixtures'] as const).map(tab => (
          <TouchableOpacity
            key={tab}
            onPress={() => setActiveTab(tab)}
            className="flex-1 py-3 items-center"
            style={{ borderBottomWidth: 2, borderBottomColor: activeTab === tab ? '#fff' : 'transparent' }}
            activeOpacity={0.7}
          >
            <Text className="text-sm capitalize" style={{ color: activeTab === tab ? '#fff' : '#71717a' }}>
              {tab === 'teams' ? 'Teams' : tab === 'players' ? 'Players' : 'Fixtures'}
            </Text>
          </TouchableOpacity>
        ))}
      </View>

      {/* Tab content */}
      <View className="flex-1">
        {activeTab === 'teams'    && <TeamsTab />}
        {activeTab === 'players'  && <PlayersTab />}
        {activeTab === 'fixtures' && <FixturesTab />}
      </View>
    </SafeAreaView>
  );
}

// ─── League list ──────────────────────────────────────────────────────────────

function LeagueList({ onSelect }: { onSelect: (l: League) => void }) {
  return (
    <SafeAreaView className="flex-1 bg-zinc-950" edges={['top']}>
      <Text className="text-white text-xl font-medium px-4 pt-4 pb-2">Leagues</Text>
      <ScrollView className="flex-1" contentContainerStyle={{ padding: 12, gap: 8 }} showsVerticalScrollIndicator={false}>
        {LEAGUES.map(league => (
          <TouchableOpacity
            key={league.id}
            onPress={() => onSelect(league)}
            activeOpacity={0.7}
            className="bg-zinc-900 border border-zinc-800 rounded-2xl p-3 flex-row items-center gap-3"
          >
            <View style={{ width: 44, height: 44, borderRadius: 10, backgroundColor: league.colour, alignItems: 'center', justifyContent: 'center' }}>
              <Text style={{ color: league.textColour, fontSize: 13, fontWeight: '500' }}>{league.initials}</Text>
            </View>
            <View className="flex-1">
              <Text className="text-white text-sm font-medium">{league.name}</Text>
              <Text className="text-zinc-500 text-xs mt-0.5">{league.venue} · {league.teamCount} teams</Text>
              <View className="flex-row gap-1.5 mt-1.5">
                <View className="bg-green-950 px-2 py-0.5 rounded-full">
                  <Text className="text-green-400 text-xs font-medium">Active</Text>
                </View>
                <View style={{ backgroundColor: league.myRole === 'playing' ? '#1e3a5f' : '#27272a', paddingHorizontal: 8, paddingVertical: 2, borderRadius: 20 }}>
                  <Text style={{ color: league.myRole === 'playing' ? '#60a5fa' : '#71717a', fontSize: 12, fontWeight: '500' }}>
                    {league.myRole === 'playing' ? 'Playing' : 'Following'}
                  </Text>
                </View>
              </View>
            </View>
            <Text className="text-zinc-600 text-xl">›</Text>
          </TouchableOpacity>
        ))}
      </ScrollView>
    </SafeAreaView>
  );
}

// ─── Root ─────────────────────────────────────────────────────────────────────

export default function LeagueScreen() {
  const [selectedLeague, setSelectedLeague] = useState<League | null>(null);

  if (selectedLeague) {
    return <LeagueDetail league={selectedLeague} onBack={() => setSelectedLeague(null)} />;
  }
  return <LeagueList onSelect={setSelectedLeague} />;
}