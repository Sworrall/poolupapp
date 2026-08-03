import { useState } from 'react';
import { View, Text, ScrollView, TouchableOpacity, StatusBar } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

// ─── Types ────────────────────────────────────────────────────────────────────

type TournamentFormat = 'ko' | 'roundrobin' | 'groups';
type TournamentStatus = 'active' | 'upcoming' | 'completed';
type EntrantType = 'singles' | 'doubles' | 'teams';
type BracketView = 'visual' | 'list';
type MatchResult = 'W' | 'L' | undefined;

type Tournament = {
    id: string;
    name: string;
    initials: string;
    colour: string;
    textColour: string;
    format: TournamentFormat;
    entrantType: EntrantType;
    entrantCount: number;
    status: TournamentStatus;
    currentStage: string;
    isPlaying: boolean;
};

type BracketMatch = {
    id: string;
    home: string;
    away: string;
    homeIsMe?: boolean;
    homeScore?: number;
    awayScore?: number;
    isLive?: boolean;
    result?: MatchResult;
    time?: string;
};

type BracketRound = {
    label: string;
    matches: (BracketMatch | null)[];
};

type GroupRow = {
    name: string;
    isMe?: boolean;
    played: number;
    won: number;
    lost: number;
    points: number;
    qualifies?: boolean;
};

type Group = {
    label: string;
    qualifyCount: number;
    rows: GroupRow[];
};

// ─── Mock data ────────────────────────────────────────────────────────────────

const TOURNAMENTS: Tournament[] = [
    { id: '1', name: 'Spring Cup 2025',      initials: 'SC', colour: '#1e3a5f', textColour: '#60a5fa', format: 'ko',          entrantType: 'singles', entrantCount: 16, status: 'active',   currentStage: 'Semi finals', isPlaying: true },
    { id: '2', name: 'Winter League Cup',    initials: 'WL', colour: '#2d1b4e', textColour: '#c084fc', format: 'groups',      entrantType: 'teams',   entrantCount: 8,  status: 'active',   currentStage: 'Group stage', isPlaying: false },
    { id: '3', name: 'Doubles Round Robin',  initials: 'DR', colour: '#451a03', textColour: '#fb923c', format: 'roundrobin',  entrantType: 'doubles', entrantCount: 6,  status: 'upcoming', currentStage: 'Not started', isPlaying: false },
];

const KO_ROUNDS: BracketRound[] = [
    {
        label: 'QF',
        matches: [
            { id: 'qf1', home: 'J. Davies', away: 'T. Walsh',   homeIsMe: true, homeScore: 5, awayScore: 3, result: 'W' },
            { id: 'qf2', home: 'R. Hughes', away: 'G. Brooks',  homeScore: 5, awayScore: 2 },
            { id: 'qf3', home: 'S. Owen',   away: 'P. Morris',  homeScore: 5, awayScore: 4 },
            { id: 'qf4', home: 'M. Patel',  away: 'C. Evans',   homeScore: 5, awayScore: 1 },
        ],
    },
    {
        label: 'SF',
        matches: [
            { id: 'sf1', home: 'J. Davies', away: 'R. Hughes', homeIsMe: true, homeScore: 3, awayScore: 2, isLive: true },
            null,
            { id: 'sf2', home: 'S. Owen',   away: 'M. Patel' },
            null,
        ],
    },
    {
        label: 'Final',
        matches: [
            null,
            null,
            { id: 'f1', home: 'TBD', away: 'TBD' },
            null,
        ],
    },
];

const GROUPS: Group[] = [
    {
        label: 'Group A', qualifyCount: 2,
        rows: [
            { name: 'Crown A',   isMe: true, played: 3, won: 3, lost: 0, points: 9, qualifies: true },
            { name: 'Red Lion A',            played: 3, won: 2, lost: 1, points: 6, qualifies: true },
            { name: 'Anchor B',              played: 3, won: 1, lost: 2, points: 3 },
            { name: 'Swan A',                played: 3, won: 0, lost: 3, points: 0 },
        ],
    },
    {
        label: 'Group B', qualifyCount: 2,
        rows: [
            { name: "King's Head", played: 2, won: 2, lost: 0, points: 6, qualifies: true },
            { name: 'The Plough',  played: 2, won: 1, lost: 1, points: 3, qualifies: true },
            { name: 'Fox & Hound', played: 2, won: 0, lost: 2, points: 0 },
        ],
    },
];

const RR_ROWS: GroupRow[] = [
    { name: 'J. Davies & S. Owen', isMe: true, played: 4, won: 3, lost: 1, points: 9 },
    { name: 'R. Hughes & M. Patel',             played: 4, won: 3, lost: 1, points: 9 },
    { name: 'T. Walsh & G. Brooks',             played: 4, won: 2, lost: 2, points: 6 },
    { name: 'P. Morris & C. Evans',             played: 3, won: 1, lost: 2, points: 3 },
    { name: 'B. Clarke & D. Rees',             played: 3, won: 1, lost: 2, points: 3 },
    { name: 'A. Lloyd & K. Price',             played: 4, won: 0, lost: 4, points: 0 },
];

// ─── Helpers ──────────────────────────────────────────────────────────────────

const FORMAT_LABELS: Record<TournamentFormat, string> = { ko: 'KO', roundrobin: 'Round robin', groups: 'Groups + KO' };
const FORMAT_COLOURS: Record<TournamentFormat, { bg: string; text: string }> = {
    ko:         { bg: '#2d1b4e', text: '#c084fc' },
    roundrobin: { bg: '#451a03', text: '#fb923c' },
    groups:     { bg: '#27272a', text: '#a1a1aa' },
};
const STATUS_COLOURS: Record<TournamentStatus, { bg: string; text: string }> = {
    active:    { bg: '#14532d', text: '#4ade80' },
    upcoming:  { bg: '#1e3a5f', text: '#60a5fa' },
    completed: { bg: '#27272a', text: '#a1a1aa' },
};
const ENTRANT_LABELS: Record<EntrantType, string> = { singles: 'Singles', doubles: 'Doubles', teams: 'Teams' };
const RESULT_BORDER: Record<string, string> = { W: '#16a34a', L: '#b91c1c' };

// ─── Shared components ────────────────────────────────────────────────────────

function Pill({ label, bg, text }: { label: string; bg: string; text: string }) {
    return (
        <View style={{ backgroundColor: bg, paddingHorizontal: 7, paddingVertical: 2, borderRadius: 20 }}>
            <Text style={{ color: text, fontSize: 10, fontWeight: '500' }}>{label}</Text>
        </View>
    );
}

function SectionHeader({ title }: { title: string }) {
    return (
        <Text className="text-zinc-600 text-xs uppercase tracking-widest py-2">{title}</Text>
    );
}

// ─── KO Bracket visual ────────────────────────────────────────────────────────

function BracketVisual() {
    return (
        <ScrollView horizontal showsHorizontalScrollIndicator={false} className="px-3 py-2">
            <View className="flex-row gap-2">
                {KO_ROUNDS.map((round, ri) => (
                    <View key={round.label} style={{ width: 90 }}>
                        <Text className="text-zinc-600 text-xs uppercase tracking-wider text-center mb-2">{round.label}</Text>
                        <View className="gap-2">
                            {round.matches.map((match, mi) => {
                                if (!match) return <View key={`spacer-${ri}-${mi}`} style={{ height: 52 }} />;
                                const isTBD = match.home === 'TBD';
                                return (
                                    <View key={match.id} className="bg-zinc-900 border border-zinc-800 rounded-lg overflow-hidden">
                                        <View style={{ padding: 5, borderBottomWidth: 0.5, borderBottomColor: '#27272a' }}>
                                            <Text style={{ fontSize: 11, color: match.homeIsMe ? '#60a5fa' : isTBD ? '#52525b' : '#fff', fontWeight: match.homeScore !== undefined && match.homeScore > (match.awayScore ?? 0) ? '500' : '400' }} numberOfLines={1}>
                                                {match.home}
                                                {match.homeScore !== undefined ? <Text style={{ color: match.homeIsMe ? '#60a5fa' : '#a1a1aa' }}> {match.homeScore}</Text> : null}
                                            </Text>
                                        </View>
                                        <View style={{ padding: 5 }}>
                                            <Text style={{ fontSize: 11, color: isTBD ? '#52525b' : '#fff' }} numberOfLines={1}>
                                                {match.away}
                                                {match.awayScore !== undefined ? <Text style={{ color: '#a1a1aa' }}> {match.awayScore}</Text> : null}
                                            </Text>
                                        </View>
                                    </View>
                                );
                            })}
                        </View>
                    </View>
                ))}
            </View>
        </ScrollView>
    );
}

// ─── KO List view ─────────────────────────────────────────────────────────────

function BracketList() {
    return (
        <ScrollView showsVerticalScrollIndicator={false} className="px-3">
            {KO_ROUNDS.map(round => {
                const realMatches = round.matches.filter(Boolean) as BracketMatch[];
                return (
                    <View key={round.label}>
                        <SectionHeader title={round.label === 'QF' ? 'Quarter finals' : round.label === 'SF' ? 'Semi finals' : 'Final'} />
                        {realMatches.map(match => {
                            const isPast = match.homeScore !== undefined && !match.isLive;
                            const borderColour = match.result ? RESULT_BORDER[match.result] : 'transparent';
                            return (
                                <TouchableOpacity
                                    key={match.id}
                                    activeOpacity={0.7}
                                    style={{
                                        borderLeftWidth: isPast ? 2 : 0,
                                        borderLeftColor: borderColour,
                                        borderRadius: 10,
                                        marginBottom: 6,
                                    }}
                                >
                                    <View
                                        className="bg-zinc-900 border border-zinc-800 p-3"
                                        style={{
                                            borderTopLeftRadius: isPast ? 0 : 10,
                                            borderBottomLeftRadius: isPast ? 0 : 10,
                                            borderRadius: isPast ? undefined : 10,
                                            borderColor: match.isLive ? '#60a5fa44' : '#27272a',
                                        }}
                                    >
                                        {match.isLive && (
                                            <View className="flex-row items-center gap-1.5 mb-1.5">
                                                <View className="w-1.5 h-1.5 rounded-full bg-green-500" />
                                                <Text className="text-green-400 text-xs font-medium">Live</Text>
                                            </View>
                                        )}
                                        <View className="flex-row items-center justify-between">
                                            <Text className="text-sm flex-1" style={{ color: match.homeIsMe ? '#60a5fa' : '#fff' }}>
                                                {match.home}
                                            </Text>
                                            {match.homeScore !== undefined ? (
                                                <View className="flex-row items-center gap-1.5 px-2">
                                                    <Text className="text-white text-base font-medium">{match.homeScore}</Text>
                                                    <Text className="text-zinc-600">–</Text>
                                                    <Text className="text-white text-base font-medium">{match.awayScore}</Text>
                                                </View>
                                            ) : (
                                                <Text className="text-zinc-600 text-xs px-3">vs</Text>
                                            )}
                                            <Text className="text-white text-sm flex-1 text-right">{match.away}</Text>
                                        </View>
                                    </View>
                                </TouchableOpacity>
                            );
                        })}
                    </View>
                );
            })}
            <View className="h-6" />
        </ScrollView>
    );
}

// ─── KO tournament detail ─────────────────────────────────────────────────────

function KODetail({ tournament, onBack }: { tournament: Tournament; onBack: () => void }) {
    const [activeTab, setActiveTab] = useState<'bracket' | 'fixtures' | 'players'>('bracket');
    const [bracketView, setBracketView] = useState<BracketView>('visual');

    return (
        <SafeAreaView className="flex-1 bg-zinc-950" edges={['top']}>
            <View className="flex-row items-center px-4 py-3 border-b border-zinc-800 gap-3">
                <TouchableOpacity onPress={onBack} activeOpacity={0.7}>
                    <Text className="text-blue-400 text-base">‹</Text>
                </TouchableOpacity>
                <View className="flex-1">
                    <Text className="text-white text-base font-medium">{tournament.name}</Text>
                    <Text className="text-zinc-500 text-xs">{ENTRANT_LABELS[tournament.entrantType]} · KO · {tournament.currentStage}</Text>
                </View>
            </View>

            <View className="flex-row border-b border-zinc-800">
                {(['bracket', 'fixtures', 'players'] as const).map(tab => (
                    <TouchableOpacity
                        key={tab}
                        onPress={() => setActiveTab(tab)}
                        className="flex-1 py-3 items-center"
                        style={{ borderBottomWidth: 2, borderBottomColor: activeTab === tab ? '#fff' : 'transparent' }}
                        activeOpacity={0.7}
                    >
                        <Text className="text-sm capitalize" style={{ color: activeTab === tab ? '#fff' : '#71717a' }}>
                            {tab === 'bracket' ? 'Bracket' : tab === 'fixtures' ? 'Fixtures' : 'Players'}
                        </Text>
                    </TouchableOpacity>
                ))}
            </View>

            {activeTab === 'bracket' && (
                <View className="flex-1">
                    <View className="flex-row bg-zinc-800 rounded-lg mx-3 mt-3 p-0.5">
                        {(['visual', 'list'] as const).map(v => (
                            <TouchableOpacity
                                key={v}
                                onPress={() => setBracketView(v)}
                                className="flex-1 py-1.5 items-center rounded-md"
                                style={{ backgroundColor: bracketView === v ? '#18181b' : 'transparent' }}
                                activeOpacity={0.7}
                            >
                                <Text className="text-xs" style={{ color: bracketView === v ? '#fff' : '#71717a' }}>
                                    {v === 'visual' ? 'Visual bracket' : 'List view'}
                                </Text>
                            </TouchableOpacity>
                        ))}
                    </View>
                    <View className="flex-1 mt-2">
                        {bracketView === 'visual' ? <BracketVisual /> : <BracketList />}
                    </View>
                </View>
            )}

            {activeTab === 'fixtures' && (
                <ScrollView className="flex-1 px-3" showsVerticalScrollIndicator={false}>
                    <SectionHeader title="All fixtures" />
                    {KO_ROUNDS.flatMap(r => r.matches.filter(Boolean) as BracketMatch[]).map(match => (
                        <TouchableOpacity
                            key={match.id}
                            activeOpacity={0.7}
                            className="bg-zinc-900 border border-zinc-800 rounded-xl p-3 mb-2"
                        >
                            <View className="flex-row items-center justify-between">
                                <Text className="text-sm flex-1" style={{ color: match.homeIsMe ? '#60a5fa' : '#fff' }}>{match.home}</Text>
                                {match.homeScore !== undefined ? (
                                    <View className="flex-row items-center gap-1.5 px-2">
                                        <Text className="text-white text-base font-medium">{match.homeScore}</Text>
                                        <Text className="text-zinc-600">–</Text>
                                        <Text className="text-white text-base font-medium">{match.awayScore}</Text>
                                    </View>
                                ) : (
                                    <Text className="text-zinc-600 text-xs px-3">vs</Text>
                                )}
                                <Text className="text-white text-sm flex-1 text-right">{match.away}</Text>
                            </View>
                        </TouchableOpacity>
                    ))}
                    <View className="h-6" />
                </ScrollView>
            )}

            {activeTab === 'players' && (
                <ScrollView className="flex-1 px-3" showsVerticalScrollIndicator={false}>
                    <SectionHeader title="Entrants" />
                    {['J. Davies', 'R. Hughes', 'S. Owen', 'M. Patel', 'T. Walsh', 'G. Brooks', 'P. Morris', 'C. Evans'].map((name, i) => (
                        <TouchableOpacity
                            key={name}
                            activeOpacity={0.7}
                            className="bg-zinc-900 border border-zinc-800 rounded-xl p-3 mb-2 flex-row items-center gap-3"
                        >
                            <View className="w-8 h-8 rounded-full bg-blue-950 items-center justify-center">
                                <Text className="text-blue-400 text-xs font-medium">{name.split('. ')[1]?.charAt(0) ?? name.charAt(0)}{name.split(' ')[1]?.charAt(0) ?? ''}</Text>
                            </View>
                            <Text className="text-white text-sm flex-1">{name}</Text>
                            {i < 2 && <Text className="text-green-400 text-xs">SF</Text>}
                            {i === 0 && <Text className="text-blue-400 text-xs ml-1">You</Text>}
                        </TouchableOpacity>
                    ))}
                    <View className="h-6" />
                </ScrollView>
            )}
        </SafeAreaView>
    );
}

// ─── Group stage table ────────────────────────────────────────────────────────

function GroupTable({ group }: { group: Group }) {
    return (
        <View className="bg-zinc-900 border border-zinc-800 rounded-xl overflow-hidden mb-3">
            <View className="flex-row items-center px-3 py-2 bg-zinc-800 border-b border-zinc-700">
                <Text className="text-white text-xs font-medium flex-1">{group.label}</Text>
                <Text className="text-zinc-500 text-xs">Top {group.qualifyCount} qualify</Text>
            </View>
            <View className="flex-row px-3 py-1.5 border-b border-zinc-800">
                <Text className="text-zinc-600 text-xs flex-1">Team</Text>
                {['P','W','L','Pts'].map(h => (
                    <Text key={h} className="text-zinc-600 text-xs w-7 text-center">{h}</Text>
                ))}
            </View>
            {group.rows.map((row, i) => (
                <View
                    key={row.name}
                    className="flex-row items-center px-3 py-2"
                    style={{
                        borderBottomWidth: i < group.rows.length - 1 ? 0.5 : 0,
                        borderBottomColor: '#27272a',
                        borderLeftWidth: row.qualifies ? 2 : 0,
                        borderLeftColor: '#16a34a',
                        backgroundColor: row.qualifies ? '#14532d11' : 'transparent',
                    }}
                >
                    <Text className="text-sm flex-1" style={{ color: row.isMe ? '#60a5fa' : '#fff' }} numberOfLines={1}>
                        {row.name}
                    </Text>
                    <Text className="text-zinc-400 text-xs w-7 text-center">{row.played}</Text>
                    <Text className="text-zinc-400 text-xs w-7 text-center">{row.won}</Text>
                    <Text className="text-zinc-400 text-xs w-7 text-center">{row.lost}</Text>
                    <Text className="text-white text-xs font-medium w-7 text-center">{row.points}</Text>
                </View>
            ))}
        </View>
    );
}

// ─── Groups tournament detail ─────────────────────────────────────────────────

function GroupsDetail({ tournament, onBack }: { tournament: Tournament; onBack: () => void }) {
    const [activeTab, setActiveTab] = useState<'groups' | 'fixtures' | 'teams'>('groups');

    return (
        <SafeAreaView className="flex-1 bg-zinc-950" edges={['top']}>
            <View className="flex-row items-center px-4 py-3 border-b border-zinc-800 gap-3">
                <TouchableOpacity onPress={onBack} activeOpacity={0.7}>
                    <Text className="text-blue-400 text-base">‹</Text>
                </TouchableOpacity>
                <View className="flex-1">
                    <Text className="text-white text-base font-medium">{tournament.name}</Text>
                    <Text className="text-zinc-500 text-xs">{ENTRANT_LABELS[tournament.entrantType]} · Groups + KO · {tournament.currentStage}</Text>
                </View>
            </View>

            <View className="flex-row border-b border-zinc-800">
                {(['groups', 'fixtures', 'teams'] as const).map(tab => (
                    <TouchableOpacity
                        key={tab}
                        onPress={() => setActiveTab(tab)}
                        className="flex-1 py-3 items-center"
                        style={{ borderBottomWidth: 2, borderBottomColor: activeTab === tab ? '#fff' : 'transparent' }}
                        activeOpacity={0.7}
                    >
                        <Text className="text-sm capitalize" style={{ color: activeTab === tab ? '#fff' : '#71717a' }}>
                            {tab === 'groups' ? 'Groups' : tab === 'fixtures' ? 'Fixtures' : 'Teams'}
                        </Text>
                    </TouchableOpacity>
                ))}
            </View>

            {activeTab === 'groups' && (
                <ScrollView className="flex-1 px-3" showsVerticalScrollIndicator={false}>
                    <View className="h-2" />
                    {GROUPS.map(group => <GroupTable key={group.label} group={group} />)}
                    <View className="flex-row items-center gap-2 mt-1 mb-6">
                        <View className="w-3 h-3 border-l-2 border-green-600" />
                        <Text className="text-zinc-600 text-xs">Qualifies for knockout stage</Text>
                    </View>
                </ScrollView>
            )}

            {activeTab === 'fixtures' && (
                <ScrollView className="flex-1 px-3" showsVerticalScrollIndicator={false}>
                    <SectionHeader title="Group fixtures" />
                    <Text className="text-zinc-500 text-sm text-center py-8">Fixtures coming soon</Text>
                </ScrollView>
            )}

            {activeTab === 'teams' && (
                <ScrollView className="flex-1 px-3" showsVerticalScrollIndicator={false}>
                    <SectionHeader title="All teams" />
                    {['Crown A', 'Red Lion A', 'Anchor B', 'Swan A', "King's Head", 'The Plough', 'Fox & Hound', 'The Griffin'].map(name => (
                        <TouchableOpacity
                            key={name}
                            activeOpacity={0.7}
                            className="bg-zinc-900 border border-zinc-800 rounded-xl p-3 mb-2 flex-row items-center gap-3"
                        >
                            <View className="w-8 h-8 rounded-lg bg-zinc-800 items-center justify-center">
                                <Text className="text-zinc-400 text-xs font-medium">{name.charAt(0)}</Text>
                            </View>
                            <Text className="text-white text-sm flex-1">{name}</Text>
                            {name === 'Crown A' && <Text className="text-blue-400 text-xs">Your team</Text>}
                        </TouchableOpacity>
                    ))}
                    <View className="h-6" />
                </ScrollView>
            )}
        </SafeAreaView>
    );
}

// ─── Round robin detail ───────────────────────────────────────────────────────

function RoundRobinDetail({ tournament, onBack }: { tournament: Tournament; onBack: () => void }) {
    const [activeTab, setActiveTab] = useState<'standings' | 'fixtures' | 'players'>('standings');

    return (
        <SafeAreaView className="flex-1 bg-zinc-950" edges={['top']}>
            <View className="flex-row items-center px-4 py-3 border-b border-zinc-800 gap-3">
                <TouchableOpacity onPress={onBack} activeOpacity={0.7}>
                    <Text className="text-blue-400 text-base">‹</Text>
                </TouchableOpacity>
                <View className="flex-1">
                    <Text className="text-white text-base font-medium">{tournament.name}</Text>
                    <Text className="text-zinc-500 text-xs">{ENTRANT_LABELS[tournament.entrantType]} · Round robin · {tournament.currentStage}</Text>
                </View>
            </View>

            <View className="flex-row border-b border-zinc-800">
                {(['standings', 'fixtures', 'players'] as const).map(tab => (
                    <TouchableOpacity
                        key={tab}
                        onPress={() => setActiveTab(tab)}
                        className="flex-1 py-3 items-center"
                        style={{ borderBottomWidth: 2, borderBottomColor: activeTab === tab ? '#fff' : 'transparent' }}
                        activeOpacity={0.7}
                    >
                        <Text className="text-sm capitalize" style={{ color: activeTab === tab ? '#fff' : '#71717a' }}>
                            {tab === 'standings' ? 'Standings' : tab === 'fixtures' ? 'Fixtures' : 'Players'}
                        </Text>
                    </TouchableOpacity>
                ))}
            </View>

            {activeTab === 'standings' && (
                <ScrollView className="flex-1 px-3" showsVerticalScrollIndicator={false}>
                    <View className="h-2" />
                    <View className="bg-zinc-900 border border-zinc-800 rounded-xl overflow-hidden">
                        <View className="flex-row px-3 py-2 border-b border-zinc-800">
                            <Text className="text-zinc-600 text-xs flex-1">Pair</Text>
                            {['P','W','L','Pts'].map(h => (
                                <Text key={h} className="text-zinc-600 text-xs w-7 text-center">{h}</Text>
                            ))}
                        </View>
                        {RR_ROWS.map((row, i) => (
                            <View
                                key={row.name}
                                className="flex-row items-center px-3 py-2.5"
                                style={{ borderBottomWidth: i < RR_ROWS.length - 1 ? 0.5 : 0, borderBottomColor: '#27272a' }}
                            >
                                <Text className="text-xs flex-1" style={{ color: row.isMe ? '#60a5fa' : '#fff' }} numberOfLines={1}>{row.name}</Text>
                                <Text className="text-zinc-400 text-xs w-7 text-center">{row.played}</Text>
                                <Text className="text-zinc-400 text-xs w-7 text-center">{row.won}</Text>
                                <Text className="text-zinc-400 text-xs w-7 text-center">{row.lost}</Text>
                                <Text className="text-white text-xs font-medium w-7 text-center">{row.points}</Text>
                            </View>
                        ))}
                    </View>
                    <View className="h-6" />
                </ScrollView>
            )}

            {activeTab !== 'standings' && (
                <View className="flex-1 items-center justify-center">
                    <Text className="text-zinc-500 text-sm">Coming soon</Text>
                </View>
            )}
        </SafeAreaView>
    );
}

// ─── Tournament list ──────────────────────────────────────────────────────────

function TournamentList({ onSelect }: { onSelect: (t: Tournament) => void }) {
    const [filter, setFilter] = useState<'all' | TournamentStatus>('all');

    const filtered = TOURNAMENTS.filter(t => filter === 'all' || t.status === filter);

    return (
        <SafeAreaView className="flex-1 bg-zinc-950" edges={['top']}>
            <Text className="text-white text-xl font-medium px-4 pt-4 pb-2">Tournaments</Text>

            <ScrollView
                horizontal
                showsHorizontalScrollIndicator={false}
                className="border-b border-zinc-800"
                contentContainerStyle={{ paddingHorizontal: 12, paddingBottom: 10, gap: 6, flexDirection: 'row' }}
            >
                {(['all', 'active', 'upcoming', 'completed'] as const).map(f => (
                    <TouchableOpacity
                        key={f}
                        onPress={() => setFilter(f)}
                        activeOpacity={0.7}
                        className="px-3 py-1.5 rounded-full"
                        style={{ backgroundColor: filter === f ? '#fff' : '#27272a' }}
                    >
                        <Text className="text-xs font-medium capitalize" style={{ color: filter === f ? '#000' : '#a1a1aa' }}>
                            {f === 'all' ? 'All' : f.charAt(0).toUpperCase() + f.slice(1)}
                        </Text>
                    </TouchableOpacity>
                ))}
            </ScrollView>

            <ScrollView className="flex-1" contentContainerStyle={{ padding: 12, gap: 8 }} showsVerticalScrollIndicator={false}>
                {filtered.map(tournament => {
                    const fmtColour = FORMAT_COLOURS[tournament.format];
                    const statusColour = STATUS_COLOURS[tournament.status];
                    return (
                        <TouchableOpacity
                            key={tournament.id}
                            onPress={() => onSelect(tournament)}
                            activeOpacity={0.7}
                            className="bg-zinc-900 border border-zinc-800 rounded-2xl p-3 flex-row items-center gap-3"
                        >
                            <View style={{ width: 44, height: 44, borderRadius: 10, backgroundColor: tournament.colour, alignItems: 'center', justifyContent: 'center' }}>
                                <Text style={{ color: tournament.textColour, fontSize: 13, fontWeight: '500' }}>{tournament.initials}</Text>
                            </View>
                            <View className="flex-1">
                                <Text className="text-white text-sm font-medium">{tournament.name}</Text>
                                <Text className="text-zinc-500 text-xs mt-0.5">{ENTRANT_LABELS[tournament.entrantType]} · {tournament.entrantCount} entrants</Text>
                                <View className="flex-row gap-1.5 mt-1.5 flex-wrap">
                                    <Pill label={tournament.status.charAt(0).toUpperCase() + tournament.status.slice(1)} bg={statusColour.bg} text={statusColour.text} />
                                    <Pill label={FORMAT_LABELS[tournament.format]} bg={fmtColour.bg} text={fmtColour.text} />
                                    {tournament.isPlaying && <Pill label="Playing" bg="#1e3a5f" text="#60a5fa" />}
                                </View>
                            </View>
                            <Text className="text-zinc-600 text-xl">›</Text>
                        </TouchableOpacity>
                    );
                })}
            </ScrollView>
        </SafeAreaView>
    );
}

// ─── Root ─────────────────────────────────────────────────────────────────────

export default function TournamentScreen() {
    const [selected, setSelected] = useState<Tournament | null>(null);

    if (selected) {
        if (selected.format === 'ko')         return <KODetail         tournament={selected} onBack={() => setSelected(null)} />;
        if (selected.format === 'groups')     return <GroupsDetail     tournament={selected} onBack={() => setSelected(null)} />;
        if (selected.format === 'roundrobin') return <RoundRobinDetail tournament={selected} onBack={() => setSelected(null)} />;
    }

    return <TournamentList onSelect={setSelected} />;
}
