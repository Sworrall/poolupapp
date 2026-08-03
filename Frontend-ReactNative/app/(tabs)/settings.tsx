import { useState } from 'react';
import {
    View, Text, ScrollView, TouchableOpacity, Switch, StatusBar,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

// ─── Types ────────────────────────────────────────────────────────────────────

type Screen = 'settings' | 'notifications';

type NotificationSettings = {
    matchAlerts: boolean;
    results: boolean;
    matchInvites: boolean;
    messages: boolean;
    mailInbox: boolean;
    newFollower: boolean;
    friendRequests: boolean;
    disputes: boolean;
    leagueUpdates: boolean;
};

// ─── Shared components ────────────────────────────────────────────────────────

function SectionLabel({ label }: { label: string }) {
    return (
        <Text className="text-zinc-600 text-xs uppercase tracking-widest px-1 pt-5 pb-1">
            {label}
        </Text>
    );
}

function SubLabel({ label }: { label: string }) {
    return (
        <View className="bg-zinc-900 border-b border-zinc-800 px-4 py-2">
            <Text className="text-zinc-600 text-xs uppercase tracking-widest">{label}</Text>
        </View>
    );
}

type RowProps = {
    icon: string;
    iconBg: string;
    iconColor: string;
    title: string;
    subtitle?: string;
    onPress?: () => void;
    isLast?: boolean;
    danger?: boolean;
    badge?: string;
};

function Row({ icon, iconBg, iconColor, title, subtitle, onPress, isLast, danger, badge }: RowProps) {
    return (
        <TouchableOpacity
            onPress={onPress}
            activeOpacity={onPress ? 0.7 : 1}
            className={`flex-row items-center gap-3 px-4 py-3 bg-zinc-900 ${!isLast ? 'border-b border-zinc-800' : ''}`}
        >
            <View style={{ width: 30, height: 30, borderRadius: 8, backgroundColor: iconBg, alignItems: 'center', justifyContent: 'center' }}>
                <Text style={{ fontSize: 15 }}>{icon}</Text>
            </View>
            <View className="flex-1">
                <View className="flex-row items-center gap-2">
                    <Text className="text-sm" style={{ color: danger ? '#f87171' : '#fff' }}>{title}</Text>
                    {badge && (
                        <View className="bg-purple-950 px-1.5 py-0.5 rounded-full">
                            <Text className="text-purple-400 text-xs">{badge}</Text>
                        </View>
                    )}
                </View>
                {subtitle && <Text className="text-zinc-500 text-xs mt-0.5">{subtitle}</Text>}
            </View>
            {onPress && <Text className="text-zinc-600 text-lg">›</Text>}
        </TouchableOpacity>
    );
}

type ToggleRowProps = {
    icon: string;
    iconBg: string;
    title: string;
    subtitle?: string;
    value: boolean;
    onValueChange: (v: boolean) => void;
    isLast?: boolean;
    badge?: string;
};

function ToggleRow({ icon, iconBg, title, subtitle, value, onValueChange, isLast, badge }: ToggleRowProps) {
    return (
        <View className={`flex-row items-center gap-3 px-4 py-3 bg-zinc-900 ${!isLast ? 'border-b border-zinc-800' : ''}`}>
            <View style={{ width: 30, height: 30, borderRadius: 8, backgroundColor: iconBg, alignItems: 'center', justifyContent: 'center' }}>
                <Text style={{ fontSize: 15 }}>{icon}</Text>
            </View>
            <View className="flex-1">
                <View className="flex-row items-center gap-2">
                    <Text className="text-white text-sm">{title}</Text>
                    {badge && (
                        <View className="bg-purple-950 px-1.5 py-0.5 rounded-full">
                            <Text className="text-purple-400 text-xs">{badge}</Text>
                        </View>
                    )}
                </View>
                {subtitle && <Text className="text-zinc-500 text-xs mt-0.5">{subtitle}</Text>}
            </View>
            <Switch
                value={value}
                onValueChange={onValueChange}
                trackColor={{ false: '#3f3f46', true: '#16a34a' }}
                thumbColor="#fff"
            />
        </View>
    );
}

function Card({ children }: { children: React.ReactNode }) {
    return (
        <View className="rounded-xl overflow-hidden border border-zinc-800">
            {children}
        </View>
    );
}

// ─── Notifications screen ─────────────────────────────────────────────────────

function NotificationsScreen({
                                 settings,
                                 onChange,
                                 onBack,
                             }: {
    settings: NotificationSettings;
    onChange: (key: keyof NotificationSettings, value: boolean) => void;
    onBack: () => void;
}) {
    // Admin role — swap to true to see disputes toggle
    const isAdmin = false;

    return (
        <SafeAreaView className="flex-1 bg-zinc-950" edges={['top']}>
            <StatusBar barStyle="light-content" />
            <View className="flex-row items-center px-4 py-3 border-b border-zinc-800">
                <TouchableOpacity onPress={onBack} activeOpacity={0.7}>
                    <Text className="text-blue-400 text-base">‹ Back</Text>
                </TouchableOpacity>
                <Text className="text-white text-base font-medium ml-3">Notifications</Text>
            </View>

            <ScrollView showsVerticalScrollIndicator={false} contentContainerStyle={{ padding: 12, gap: 6 }}>
                <Card>
                    <ToggleRow icon="🔔" iconBg="#14532d" title="Match alerts" subtitle="Live match updates" value={settings.matchAlerts} onValueChange={v => onChange('matchAlerts', v)} />
                    <ToggleRow icon="🏆" iconBg="#14532d" title="Results" subtitle="When matches are confirmed" value={settings.results} onValueChange={v => onChange('results', v)} />
                    <ToggleRow icon="⚔️" iconBg="#14532d" title="Match invites" subtitle="When you're invited to a match" value={settings.matchInvites} onValueChange={v => onChange('matchInvites', v)} />
                    <ToggleRow icon="💬" iconBg="#14532d" title="Messages" subtitle="Direct messages" value={settings.messages} onValueChange={v => onChange('messages', v)} />
                    <ToggleRow icon="📬" iconBg="#14532d" title="Mail inbox" subtitle="Announcements & updates" value={settings.mailInbox} onValueChange={v => onChange('mailInbox', v)} />
                    <ToggleRow icon="👤" iconBg="#14532d" title="New follower" subtitle="When someone follows you" value={settings.newFollower} onValueChange={v => onChange('newFollower', v)} />
                    <ToggleRow icon="👥" iconBg="#14532d" title="Friend requests" subtitle="When someone sends a request" value={settings.friendRequests} onValueChange={v => onChange('friendRequests', v)} />
                    {isAdmin && (
                        <ToggleRow icon="⚠️" iconBg="#14532d" title="Disputes" subtitle="When a result is contested" badge="Admin only" value={settings.disputes} onValueChange={v => onChange('disputes', v)} />
                    )}
                    <ToggleRow icon="📰" iconBg="#14532d" title="League updates" subtitle="Admin announcements" value={settings.leagueUpdates} onValueChange={v => onChange('leagueUpdates', v)} isLast />
                </Card>
                <View className="h-6" />
            </ScrollView>
        </SafeAreaView>
    );
}

// ─── Main settings screen ─────────────────────────────────────────────────────

export default function SettingsScreen() {
    const [screen, setScreen] = useState<Screen>('settings');
    const [notifications, setNotifications] = useState<NotificationSettings>({
        matchAlerts: true,
        results: true,
        matchInvites: true,
        messages: true,
        mailInbox: true,
        newFollower: true,
        friendRequests: true,
        disputes: true,
        leagueUpdates: false,
    });

    const updateNotification = (key: keyof NotificationSettings, value: boolean) => {
        setNotifications(prev => ({ ...prev, [key]: value }));
    };

    if (screen === 'notifications') {
        return (
            <NotificationsScreen
                settings={notifications}
                onChange={updateNotification}
                onBack={() => setScreen('settings')}
            />
        );
    }

    return (
        <SafeAreaView className="flex-1 bg-zinc-950" edges={['top']}>
            <StatusBar barStyle="light-content" />
            <ScrollView
                showsVerticalScrollIndicator={false}
                contentContainerStyle={{ padding: 12, paddingBottom: 60 }}
            >
                <Text className="text-white text-xl font-medium px-1 pb-2">Settings</Text>

                {/* ── Account ── */}
                <SectionLabel label="Account" />
                <Card>
                    <Row icon="👤" iconBg="#1e3a5f" iconColor="#60a5fa" title="Edit profile" subtitle="Name, nickname, avatar" onPress={() => {}} />
                    <Row icon="✉️" iconBg="#1e3a5f" iconColor="#60a5fa" title="Email address" subtitle="j.davies@email.com" onPress={() => {}} />
                    <Row icon="🔒" iconBg="#1e3a5f" iconColor="#60a5fa" title="Password" subtitle="Change your password" onPress={() => {}} />
                    <Row icon="🔗" iconBg="#27272a" iconColor="#71717a" title="Linked accounts" subtitle="Google · Apple" onPress={() => {}} isLast />
                </Card>

                {/* ── Privacy ── */}
                <SectionLabel label="Privacy" />
                <Card>
                    <Row icon="👁️" iconBg="#2d1b4e" iconColor="#c084fc" title="Profile visibility" subtitle="Who can find your profile" onPress={() => {}} />
                    <Row icon="📊" iconBg="#2d1b4e" iconColor="#c084fc" title="Profile information visibility" subtitle="Stats, info · Everyone" onPress={() => {}} isLast />
                </Card>

                {/* ── App ── */}
                <SectionLabel label="App" />
                <Card>
                    <SubLabel label="Visual" />
                    <Row icon="🌙" iconBg="#27272a" iconColor="#a1a1aa" title="Theme" subtitle="Dark" onPress={() => {}} isLast />
                    <SubLabel label="Notifications" />
                    <Row icon="🔔" iconBg="#14532d" iconColor="#4ade80" title="Notifications" subtitle="Manage all notification preferences" onPress={() => setScreen('notifications')} isLast />
                </Card>

                {/* ── Accessibility ── */}
                <SectionLabel label="Accessibility" />
                <Card>
                    <Row icon="🔡" iconBg="#27272a" iconColor="#a1a1aa" title="Font size" subtitle="Medium" onPress={() => {}} />
                    <ToggleRow icon="𝐁" iconBg="#27272a" title="Bold text" value={false} onValueChange={() => {}} />
                    <ToggleRow icon="📖" iconBg="#1e3a5f" title="Dyslexic font" subtitle="Uses OpenDyslexic" value={false} onValueChange={() => {}} />
                    <ToggleRow icon="◑" iconBg="#27272a" title="High contrast" subtitle="Increases text & border contrast" value={false} onValueChange={() => {}} />
                    <ToggleRow icon="👆" iconBg="#27272a" title="Large tap targets" subtitle="Bigger buttons & touch areas" value={false} onValueChange={() => {}} />
                    <ToggleRow icon="◻️" iconBg="#27272a" title="Simplified UI" subtitle="Reduces visual noise" value={false} onValueChange={() => {}} />
                    <ToggleRow icon="📳" iconBg="#27272a" title="Vibration alerts" subtitle="Haptic feedback for notifications" value={true} onValueChange={() => {}} isLast />
                </Card>

                {/* ── Payments ── */}
                <SectionLabel label="Payments" />
                <Card>
                    <Row icon="🛡️" iconBg="#451a03" iconColor="#fb923c" title="Admin subscription" subtitle="Create leagues & tournaments" onPress={() => {}} />
                    <Row icon="📢" iconBg="#451a03" iconColor="#fb923c" title="Advertise" subtitle="Local business listings" onPress={() => {}} isLast />
                </Card>

                {/* ── Data & storage ── */}
                <SectionLabel label="Data & storage" />
                <Card>
                    <Row icon="🗑️" iconBg="#27272a" iconColor="#a1a1aa" title="Clear cache" subtitle="Free up local storage" onPress={() => {}} />
                    <Row icon="⬇️" iconBg="#27272a" iconColor="#a1a1aa" title="Download my data" subtitle="Export all your data" onPress={() => {}} isLast />
                </Card>

                {/* ── Report ── */}
                <SectionLabel label="Report" />
                <Card>
                    <Row icon="🐛" iconBg="#27272a" iconColor="#a1a1aa" title="Report a bug" subtitle="Sent to developers" onPress={() => {}} />
                    <Row icon="⚠️" iconBg="#450a0a" iconColor="#f87171" title="Report foul play" subtitle="Sent to league admin" onPress={() => {}} />
                    <Row icon="💡" iconBg="#14532d" iconColor="#4ade80" title="Suggest an improvement" subtitle="Sent to developers" onPress={() => {}} isLast />
                </Card>

                {/* ── Legal ── */}
                <SectionLabel label="Legal" />
                <Card>
                    <Row icon="📄" iconBg="#27272a" iconColor="#a1a1aa" title="Terms of service" onPress={() => {}} />
                    <Row icon="🛡️" iconBg="#27272a" iconColor="#a1a1aa" title="Privacy policy" onPress={() => {}} />
                    <Row icon="🍪" iconBg="#27272a" iconColor="#a1a1aa" title="Cookie policy" onPress={() => {}} />
                    <Row icon="ℹ️" iconBg="#27272a" iconColor="#a1a1aa" title="About" subtitle="Pool League · v1.0.0" onPress={() => {}} isLast />
                </Card>

                {/* ── Account actions ── */}
                <SectionLabel label="Account actions" />
                <Card>
                    <Row icon="🚪" iconBg="#450a0a" iconColor="#f87171" title="Log out" onPress={() => {}} danger />
                    <Row icon="❌" iconBg="#450a0a" iconColor="#f87171" title="Delete account" subtitle="Permanently remove your data" onPress={() => {}} danger isLast />
                </Card>

                <Text className="text-zinc-700 text-xs text-center mt-6 mb-2">Pool League v1.0.0</Text>
            </ScrollView>
        </SafeAreaView>
    );
}
