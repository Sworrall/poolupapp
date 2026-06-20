import { View, Text, ScrollView, TouchableOpacity, StatusBar } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

const player = {
  initials: 'JD',
  name: 'Jamie Davies',
  league: 'Division A · The Crown',
  singlesPosition: 3,
  singlesPositionSuffix: 'rd',
  teamPosition: 1,
  teamPositionSuffix: 'st',
  teamName: 'The Crown A',
  wins: 62,
  draws: 13,
  losses: 25,
};

const liveMatches = [
  { id: '1', context: 'Div A · Week 4', home: 'R. Hughes', away: 'S. Owen', homeScore: 3, awayScore: 2, frame: 6, totalFrames: 9 },
  { id: '2', context: 'Spring Cup · QF', home: 'The Crown A', away: 'Red Lion B', homeScore: 4, awayScore: 3, frame: 8, totalFrames: 11 },
  { id: '3', context: 'Div B · Week 4', home: 'P. Morris', away: 'T. Walsh', homeScore: 1, awayScore: 1, frame: 3, totalFrames: 9 },
];

const recentResults = [
  { id: '1', opponent: 'vs M. Patel', context: 'Div A · Week 3 · 2 days ago', score: '5–4', result: 'W' },
  { id: '2', opponent: 'The Crown A vs Red Lion A', context: 'Spring Cup · R1 · 4 days ago', score: '4–7', result: 'L' },
  { id: '3', opponent: 'vs G. Brooks (doubles)', context: 'Div A Doubles · Week 2 · 6 days ago', score: '3–1', result: 'W' },
];

const quickActions = [
  { label: 'Mail', icon: '✉', bg: 'bg-blue-950', text: 'text-blue-400' },
  { label: 'Team', icon: '👥', bg: 'bg-green-950', text: 'text-green-400' },
  { label: 'Rules', icon: '📖', bg: 'bg-yellow-950', text: 'text-yellow-400' },
  { label: 'Fixtures', icon: '📅', bg: 'bg-zinc-800', text: 'text-zinc-300' },
];

const resultColours: Record<string, { bg: string; text: string }> = {
  W: { bg: 'bg-green-950', text: 'text-green-400' },
  L: { bg: 'bg-red-950', text: 'text-red-400' },
  D: { bg: 'bg-yellow-950', text: 'text-yellow-400' },
};

export default function HomeScreen() {
  return (
    <SafeAreaView className="flex-1 bg-zinc-950" edges={['top']}>
      <StatusBar barStyle="light-content" />
      <ScrollView
        className="flex-1"
        contentContainerStyle={{ padding: 16, gap: 14 }}
        showsVerticalScrollIndicator={false}
      >

        {/* ── Player card ── */}
        <View className="bg-zinc-900 rounded-2xl border border-zinc-800 p-4">
          <View className="flex-row items-center gap-3 mb-4">
            <View className="w-12 h-12 rounded-full bg-blue-950 items-center justify-center">
              <Text className="text-blue-400 text-base font-medium">{player.initials}</Text>
            </View>
            <View>
              <Text className="text-white text-base font-medium">{player.name}</Text>
              <Text className="text-zinc-400 text-xs mt-0.5">{player.league}</Text>
            </View>
          </View>

          {/* Positions */}
          <View className="flex-row gap-2 mb-4">
            <View className="flex-1 bg-zinc-800 rounded-xl p-3">
              <Text className="text-zinc-500 text-xs uppercase tracking-wider mb-1">Singles</Text>
              <Text className="text-white text-2xl font-medium">
                {player.singlesPosition}
                <Text className="text-zinc-400 text-sm font-normal">{player.singlesPositionSuffix}</Text>
              </Text>
              <Text className="text-zinc-500 text-xs mt-0.5">Div A</Text>
            </View>
            <View className="flex-1 bg-zinc-800 rounded-xl p-3">
              <Text className="text-zinc-500 text-xs uppercase tracking-wider mb-1">Team</Text>
              <Text className="text-white text-2xl font-medium">
                {player.teamPosition}
                <Text className="text-zinc-400 text-sm font-normal">{player.teamPositionSuffix}</Text>
              </Text>
              <Text className="text-zinc-500 text-xs mt-0.5">{player.teamName}</Text>
            </View>
          </View>

          {/* W/D/L bar */}
          <Text className="text-zinc-500 text-xs uppercase tracking-wider mb-2">Win / Draw / Loss</Text>
          <View className="flex-row h-1.5 rounded-full overflow-hidden gap-0.5">
            <View className="bg-green-600 rounded-full" style={{ flex: player.wins }} />
            <View className="bg-yellow-600 rounded-full" style={{ flex: player.draws }} />
            <View className="bg-red-700 rounded-full" style={{ flex: player.losses }} />
          </View>
          <View className="flex-row gap-4 mt-2">
            <View className="flex-row items-center gap-1.5">
              <View className="w-2 h-2 rounded-full bg-green-600" />
              <Text className="text-zinc-400 text-xs">{player.wins}% W</Text>
            </View>
            <View className="flex-row items-center gap-1.5">
              <View className="w-2 h-2 rounded-full bg-yellow-600" />
              <Text className="text-zinc-400 text-xs">{player.draws}% D</Text>
            </View>
            <View className="flex-row items-center gap-1.5">
              <View className="w-2 h-2 rounded-full bg-red-700" />
              <Text className="text-zinc-400 text-xs">{player.losses}% L</Text>
            </View>
          </View>
        </View>

        {/* ── Quick actions ── */}
        <View className="flex-row gap-2">
          {quickActions.map((action) => (
            <TouchableOpacity
              key={action.label}
              className="flex-1 bg-zinc-900 border border-zinc-800 rounded-xl p-3 items-center gap-2"
              activeOpacity={0.7}
            >
              <View className={`w-9 h-9 rounded-full ${action.bg} items-center justify-center`}>
                <Text className="text-base">{action.icon}</Text>
              </View>
              <Text className="text-zinc-400 text-xs">{action.label}</Text>
            </TouchableOpacity>
          ))}
        </View>

        {/* ── Live matches ── */}
        <View>
          <View className="flex-row justify-between items-baseline mb-2">
            <Text className="text-white text-sm font-medium">Live now</Text>
            <TouchableOpacity activeOpacity={0.7}>
              <Text className="text-blue-400 text-xs">See all</Text>
            </TouchableOpacity>
          </View>

          <View className="gap-2">
            {liveMatches.map((match, index) => (
              <TouchableOpacity
                key={match.id}
                className="bg-zinc-900 border border-zinc-800 rounded-xl p-3"
                style={{ opacity: index === 2 ? 0.4 : 1 }}
                activeOpacity={0.7}
              >
                <View className="flex-row justify-between items-center mb-2">
                  <Text className="text-zinc-400 text-xs">{match.context}</Text>
                  <View className="flex-row items-center gap-1.5 bg-green-950 px-2 py-0.5 rounded-full">
                    <View className="w-1.5 h-1.5 rounded-full bg-green-500" />
                    <Text className="text-green-400 text-xs font-medium">Live</Text>
                  </View>
                </View>
                <View className="flex-row items-center justify-between">
                  <Text className="text-white text-sm flex-1">{match.home}</Text>
                  <View className="flex-row items-center gap-2 px-2">
                    <Text className="text-white text-base font-medium">{match.homeScore}</Text>
                    <Text className="text-zinc-600 text-sm">–</Text>
                    <Text className="text-white text-base font-medium">{match.awayScore}</Text>
                  </View>
                  <Text className="text-white text-sm flex-1 text-right">{match.away}</Text>
                </View>
                <Text className="text-zinc-500 text-xs text-center mt-1.5">
                  Frame {match.frame} of {match.totalFrames}
                </Text>
              </TouchableOpacity>
            ))}
          </View>
        </View>

        {/* ── Recent results ── */}
        <View className="mb-6">
          <View className="flex-row justify-between items-baseline mb-2">
            <Text className="text-white text-sm font-medium">Recent results</Text>
            <TouchableOpacity activeOpacity={0.7}>
              <Text className="text-blue-400 text-xs">See all</Text>
            </TouchableOpacity>
          </View>

          <View className="gap-2">
            {recentResults.map((result) => {
              const colours = resultColours[result.result];
              return (
                <TouchableOpacity
                  key={result.id}
                  className="bg-zinc-900 border border-zinc-800 rounded-xl p-3 flex-row items-center gap-3"
                  activeOpacity={0.7}
                >
                  <View className={`w-8 h-8 rounded-lg ${colours.bg} items-center justify-center`}>
                    <Text className={`text-xs font-medium ${colours.text}`}>{result.result}</Text>
                  </View>
                  <View className="flex-1">
                    <Text className="text-white text-sm">{result.opponent}</Text>
                    <Text className="text-zinc-500 text-xs mt-0.5">{result.context}</Text>
                  </View>
                  <Text className="text-white text-sm font-medium">{result.score}</Text>
                </TouchableOpacity>
              );
            })}
          </View>
        </View>

      </ScrollView>
    </SafeAreaView>
  );
}