import { View, Text, ScrollView, TouchableOpacity } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

// ─── Types ────────────────────────────────────────────────────────────────────

type StatContext = {
    label: string;
    framesPlayed: number;
    framesWon: number;
    winPct: number;
    breakDishes: number;
    breakDishPct: number;
    matchesPlayed: number;
    matchesWon: number;
    matchDrawn: number;
    matchesLost: number;
};

type RecentResult = {
    id: string;
    opponent: string;
    context: string;
    score: string;
    result: 'W' | 'L' | 'D';
};

type PlayerProfileData = {
    initials: string;
    name: string;
    nickname?: string;
    teamName: string;
    joinDate: string;
    globalWinPct: number;
    globalBreakDishPct: number;
    isMe: boolean;
    stats: StatContext[];
    recentResults: RecentResult[];
};

// ─── Mock data ────────────────────────────────────────────────────────────────

const MY_PROFILE: PlayerProfileData = {
    initials: 'JD',
    name: 'Jamie Davies',
    nickname: 'The Jammer',
    teamName: 'The Crown A',
    joinDate: 'March 2023',
    globalWinPct: 64.2,
    globalBreakDishPct: 22.8,
    isMe: true,
    stats: [
        {
            label: 'Singles',
            framesPlayed: 32,
            framesWon: 22,
            winPct: 68.8,
            breakDishes: 9,
            breakDishPct: 28.1,
            matchesPlayed: 10,
            matchesWon: 7,
            matchDrawn: 1,
            matchesLost: 2,
        },
        {
            label: 'Doubles',
            framesPlayed: 18,
            framesWon: 11,
            winPct: 61.1,
            breakDishes: 4,
            breakDishPct: 22.2,
            matchesPlayed: 6,
            matchesWon: 4,
            matchDrawn: 0,
            matchesLost: 2,
        },
        {
            label: 'Team',
            framesPlayed: 24,
            framesWon: 14,
            winPct: 58.3,
            breakDishes: 5,
            breakDishPct: 20.8,
            matchesPlayed: 8,
            matchesWon: 4,
            matchDrawn: 2,
            matchesLost: 2,
        },
    ],
    recentResults: [
        { id: '1', opponent: 'vs M. Patel',            context: 'Div A · Singles · Week 3', score: '5–4', result: 'W' },
        { id: '2', opponent: 'Crown A vs Red Lion A',   context: 'Spring Cup · Team · R1',   score: '4–7', result: 'L' },
        { id: '3', opponent: 'vs G. Brooks (doubles)',  context: 'Div A · Doubles · Week 2', score: '3–1', result: 'W' },
        { id: '4', opponent: 'vs T. Walsh',             context: 'Div A · Singles · Week 2', score: '5–3', result: 'W' },
        { id: '5', opponent: 'vs R. Hughes',            context: 'Div A · Singles · Week 1', score: '3–5', result: 'L' },
    ],
};

// ─── Helpers ──────────────────────────────────────────────────────────────────

const RESULT_COLOURS: Record<string, { bg: string; text: string }> = {
    W: { bg: '#14532d', text: '#4ade80' },
    L: { bg: '#450a0a', text: '#f87171' },
    D: { bg: '#451a03', text: '#fbbf24' },
};

const RESULT_BORDER: Record<string, string> = {
    W: '#16a34a',
    L: '#b91c1c',
    D: '#b45309',
};

// ─── Stat block ───────────────────────────────────────────────────────────────

function StatBlock({ label, value, sub }: { label: string; value: string; sub?: string }) {
    return (
        <View className="flex-1 bg-zinc-800 rounded-xl p-3 items-center">
            <Text className="text-zinc-500 text-xs uppercase tracking-wider mb-1">{label}</Text>
            <Text className="text-white text-xl font-medium">{value}</Text>
            {sub && <Text className="text-zinc-500 text-xs mt-0.5">{sub}</Text>}
        </View>
    );
}

// ─── W/D/L bar ───────────────────────────────────────────────────────────────

function WDLBar({ won, drawn, lost }: { won: number; drawn: number; lost: number }) {
    const total = won + drawn + lost;
    if (total === 0) return null;
    return (
        <View className="flex-row h-1.5 rounded-full overflow-hidden gap-0.5">
            <View style={{ flex: won, backgroundColor: '#16a34a', borderRadius: 20 }} />
            <View style={{ flex: drawn, backgroundColor: '#b45309', borderRadius: 20 }} />
            <View style={{ flex: lost, backgroundColor: '#b91c1c', borderRadius: 20 }} />
        </View>
    );
}

// ─── Stats section ────────────────────────────────────────────────────────────

function StatsSection({ stats }: { stats: StatContext[] }) {
    return (
        <View className="gap-4">
            {stats.map(ctx => (
                <View key={ctx.label} className="bg-zinc-900 border border-zinc-800 rounded-2xl p-4 gap-3">
                    <Text className="text-white text-sm font-medium">{ctx.label}</Text>

                    {/* W/D/L bar */}
                    <View>
                        <WDLBar won={ctx.matchesWon} drawn={ctx.matchDrawn} lost={ctx.matchesLost} />
                        <View className="flex-row gap-4 mt-1.5">
                            <View className="flex-row items-center gap-1.5">
                                <View className="w-2 h-2 rounded-full bg-green-600" />
                                <Text className="text-zinc-400 text-xs">{ctx.matchesWon}W</Text>
                            </View>
                            <View className="flex-row items-center gap-1.5">
                                <View className="w-2 h-2 rounded-full bg-yellow-600" />
                                <Text className="text-zinc-400 text-xs">{ctx.matchDrawn}D</Text>
                            </View>
                            <View className="flex-row items-center gap-1.5">
                                <View className="w-2 h-2 rounded-full bg-red-700" />
                                <Text className="text-zinc-400 text-xs">{ctx.matchesLost}L</Text>
                            </View>
                            <Text className="text-zinc-600 text-xs ml-auto">{ctx.matchesPlayed} matches</Text>
                        </View>
                    </View>

                    {/* Stat blocks */}
                    <View className="flex-row gap-2">
                        <StatBlock label="Frames" value={`${ctx.framesWon}/${ctx.framesPlayed}`} sub={`${ctx.winPct}% win`} />
                        <StatBlock label="Break dishes" value={String(ctx.breakDishes)} sub={`${ctx.breakDishPct}% rate`} />
                    </View>
                </View>
            ))}
        </View>
    );
}

// ─── Recent results ───────────────────────────────────────────────────────────

function RecentResults({ results }: { results: RecentResult[] }) {
    return (
        <View className="bg-zinc-900 border border-zinc-800 rounded-2xl p-4 gap-2">
            <Text className="text-white text-sm font-medium mb-1">Last 5 results</Text>
            {results.map(result => {
                const colours = RESULT_COLOURS[result.result];
                return (
                    <TouchableOpacity
                        key={result.id}
                        activeOpacity={0.7}
                        className="flex-row items-center gap-3"
                        style={{
                            borderLeftWidth: 2,
                            borderLeftColor: RESULT_BORDER[result.result],
                            paddingLeft: 10,
                            paddingVertical: 4,
                        }}
                    >
                        <View style={{ width: 28, height: 28, borderRadius: 8, backgroundColor: colours.bg, alignItems: 'center', justifyContent: 'center' }}>
                            <Text style={{ color: colours.text, fontSize: 11, fontWeight: '500' }}>{result.result}</Text>
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
    );
}

// ─── Main component ───────────────────────────────────────────────────────────

type Props = {
    profile?: PlayerProfileData;
    onBack?: () => void;
};

export default function PlayerProfile({ profile = MY_PROFILE, onBack }: Props) {
    return (
        <SafeAreaView className="flex-1 bg-zinc-950" edges={['top']}>
            {/* Header */}
            {onBack && (
                <View className="flex-row items-center px-4 py-3 border-b border-zinc-800">
                    <TouchableOpacity onPress={onBack} activeOpacity={0.7}>
                        <Text className="text-blue-400 text-base">‹ Back</Text>
                    </TouchableOpacity>
                </View>
            )}

            <ScrollView
                className="flex-1"
                contentContainerStyle={{ padding: 16, gap: 14 }}
                showsVerticalScrollIndicator={false}
            >
                {/* ── Hero card ── */}
                <View className="bg-zinc-900 border border-zinc-800 rounded-2xl p-4">
                    <View className="flex-row items-center gap-4 mb-4">
                        {/* Avatar */}
                        <View className="w-16 h-16 rounded-full bg-blue-950 items-center justify-center">
                            <Text className="text-blue-400 text-xl font-medium">{profile.initials}</Text>
                        </View>

                        {/* Name block */}
                        <View className="flex-1">
                            <Text className="text-white text-lg font-medium">{profile.name}</Text>
                            {profile.nickname && (
                                <Text className="text-zinc-400 text-sm mt-0.5">"{profile.nickname}"</Text>
                            )}
                            <Text className="text-zinc-500 text-xs mt-1">{profile.teamName}</Text>
                        </View>

                        {/* Edit button (only for own profile) */}
                        {profile.isMe && (
                            <TouchableOpacity
                                activeOpacity={0.7}
                                className="bg-zinc-800 border border-zinc-700 rounded-lg px-3 py-1.5"
                            >
                                <Text className="text-zinc-300 text-xs">Edit</Text>
                            </TouchableOpacity>
                        )}
                    </View>

                    {/* Meta row */}
                    <View className="flex-row gap-4 mb-4">
                        <View>
                            <Text className="text-zinc-500 text-xs">Member since</Text>
                            <Text className="text-zinc-300 text-sm">{profile.joinDate}</Text>
                        </View>
                    </View>

                    {/* Global stats */}
                    <View className="flex-row gap-2">
                        <View className="flex-1 bg-zinc-800 rounded-xl p-3">
                            <Text className="text-zinc-500 text-xs uppercase tracking-wider mb-1">Global win %</Text>
                            <Text className="text-white text-2xl font-medium">{profile.globalWinPct}
                                <Text className="text-zinc-400 text-sm font-normal">%</Text>
                            </Text>
                        </View>
                        <View className="flex-1 bg-zinc-800 rounded-xl p-3">
                            <Text className="text-zinc-500 text-xs uppercase tracking-wider mb-1">Break dish %</Text>
                            <Text className="text-white text-2xl font-medium">{profile.globalBreakDishPct}
                                <Text className="text-zinc-400 text-sm font-normal">%</Text>
                            </Text>
                        </View>
                    </View>
                </View>

                {/* ── Stats by context ── */}
                <Text className="text-white text-sm font-medium">Stats</Text>
                <StatsSection stats={profile.stats} />

                {/* ── Recent results ── */}
                <RecentResults results={profile.recentResults} />

                <View className="h-4" />
            </ScrollView>
        </SafeAreaView>
    );
}
