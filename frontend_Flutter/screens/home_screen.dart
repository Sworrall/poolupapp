import 'package:flutter/material.dart';
import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:intl/intl.dart';

// ─── Re-use AppColors from tournaments_screen.dart ───────────────────────────
// (move to a shared theme.dart in your project)

class AppColors {
  static const background = Color(0xFF0F0F13);
  static const card = Color(0xFF1A1A24);
  static const muted = Color(0xFF1E1E2A);
  static const border = Color(0xFF2A2A3A);
  static const borderActive = Color(0x334F8EFF);
  static const foreground = Color(0xFFF0F0F5);
  static const mutedForeground = Color(0xFF6B6B80);
  static const primary = Color(0xFF4F8EFF);
  static const green = Color(0xFF4CAF50);
  static const amber = Color(0xFFFFC107);
  static const purple = Color(0xFF9C6FFF);
  static const blue = Color(0xFF4F8EFF);
}

// ─── Models ───────────────────────────────────────────────────────────────────

class PlayerProfile {
  final String id;
  final String? nickname;
  final String? teamName;
  final String? teamId;
  final bool isCaptain;
  final int matchesWon;
  final int matchesLost;
  final int matchesDrawn;
  final int matchesTotal;
  final int framesWon;
  final int framesTotal;
  final int breaks;

  PlayerProfile({
    required this.id,
    this.nickname,
    this.teamName,
    this.teamId,
    this.isCaptain = false,
    this.matchesWon = 0,
    this.matchesLost = 0,
    this.matchesDrawn = 0,
    this.matchesTotal = 0,
    this.framesWon = 0,
    this.framesTotal = 0,
    this.breaks = 0,
  });

  factory PlayerProfile.fromFirestore(DocumentSnapshot doc) {
    final d = doc.data() as Map<String, dynamic>;
    return PlayerProfile(
      id: doc.id,
      nickname: d['nickname'],
      teamName: d['team_name'],
      teamId: d['team_id'],
      isCaptain: d['is_captain'] ?? false,
      matchesWon: d['matches_won'] ?? 0,
      matchesLost: d['matches_lost'] ?? 0,
      matchesDrawn: d['matches_drawn'] ?? 0,
      matchesTotal: d['matches_total'] ?? 0,
      framesWon: d['frames_won'] ?? 0,
      framesTotal: d['frames_total'] ?? 0,
      breaks: d['breaks'] ?? 0,
    );
  }

  int? get matchWinRate =>
      matchesTotal > 0 ? ((matchesWon / matchesTotal) * 100).round() : null;
  int? get frameWinRate =>
      framesTotal > 0 ? ((framesWon / framesTotal) * 100).round() : null;
}

class MatchData {
  final String id;
  final String homeId;
  final String awayId;
  final String homeName;
  final String awayName;
  final String result; // 'home_win' | 'away_win' | 'draw' | 'pending'
  final String? score;
  final DateTime? matchDate;

  MatchData({
    required this.id,
    required this.homeId,
    required this.awayId,
    required this.homeName,
    required this.awayName,
    required this.result,
    this.score,
    this.matchDate,
  });

  factory MatchData.fromFirestore(DocumentSnapshot doc) {
    final d = doc.data() as Map<String, dynamic>;
    return MatchData(
      id: doc.id,
      homeId: d['home_id'] ?? '',
      awayId: d['away_id'] ?? '',
      homeName: d['home_name'] ?? 'Home',
      awayName: d['away_name'] ?? 'Away',
      result: d['result'] ?? 'pending',
      score: d['score'],
      matchDate: d['match_date'] != null
          ? (d['match_date'] as Timestamp).toDate()
          : null,
    );
  }
}

class TournamentData {
  final String id;
  final String name;
  final String type;
  final String participantType;
  final String status;

  TournamentData({
    required this.id,
    required this.name,
    required this.type,
    required this.participantType,
    required this.status,
  });

  factory TournamentData.fromFirestore(DocumentSnapshot doc) {
    final d = doc.data() as Map<String, dynamic>;
    return TournamentData(
      id: doc.id,
      name: d['name'] ?? '',
      type: d['type'] ?? '',
      participantType: d['participant_type'] ?? '',
      status: d['status'] ?? '',
    );
  }
}

// ─── Shared Animated Section ──────────────────────────────────────────────────

class FadeUpSection extends StatefulWidget {
  final Widget child;
  final int delayMs;

  const FadeUpSection({super.key, required this.child, this.delayMs = 0});

  @override
  State<FadeUpSection> createState() => _FadeUpSectionState();
}

class _FadeUpSectionState extends State<FadeUpSection>
    with SingleTickerProviderStateMixin {
  late final AnimationController _ctrl;
  late final Animation<double> _opacity;
  late final Animation<Offset> _slide;

  @override
  void initState() {
    super.initState();
    _ctrl = AnimationController(
        vsync: this, duration: const Duration(milliseconds: 350));
    _opacity =
        Tween<double>(begin: 0, end: 1).animate(CurvedAnimation(parent: _ctrl, curve: Curves.easeOut));
    _slide = Tween<Offset>(begin: const Offset(0, 0.1), end: Offset.zero)
        .animate(CurvedAnimation(parent: _ctrl, curve: Curves.easeOut));
    Future.delayed(Duration(milliseconds: widget.delayMs), () {
      if (mounted) _ctrl.forward();
    });
  }

  @override
  void dispose() {
    _ctrl.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) => FadeTransition(
    opacity: _opacity,
    child: SlideTransition(position: _slide, child: widget.child),
  );
}

// ─── Stat Card ────────────────────────────────────────────────────────────────

class StatCard extends StatelessWidget {
  final String label;
  final String value;
  final Color? accentColor;

  const StatCard({
    super.key,
    required this.label,
    required this.value,
    this.accentColor,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: AppColors.muted,
        borderRadius: BorderRadius.circular(12),
        border: Border.all(color: AppColors.border),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            label,
            style: const TextStyle(
                color: AppColors.mutedForeground,
                fontSize: 11,
                fontWeight: FontWeight.w500),
          ),
          const SizedBox(height: 4),
          Text(
            value,
            style: TextStyle(
              color: accentColor ?? AppColors.foreground,
              fontSize: 22,
              fontWeight: FontWeight.w800,
              letterSpacing: -0.5,
            ),
          ),
        ],
      ),
    );
  }
}

// ─── Win Rate Bar ─────────────────────────────────────────────────────────────

class WinRateBar extends StatelessWidget {
  final int won;
  final int lost;
  final int drawn;
  final int total;

  const WinRateBar({
    super.key,
    required this.won,
    required this.lost,
    required this.drawn,
    required this.total,
  });

  @override
  Widget build(BuildContext context) {
    if (total == 0) return const SizedBox.shrink();

    final wonFrac = won / total;
    final drawnFrac = drawn / total;
    final lostFrac = lost / total;

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            _label('W $won', AppColors.green),
            if (drawn > 0) _label('D $drawn', AppColors.mutedForeground),
            _label('L $lost', const Color(0xFFEF5350)),
          ],
        ),
        const SizedBox(height: 6),
        ClipRRect(
          borderRadius: BorderRadius.circular(4),
          child: Row(
            children: [
              _bar(wonFrac, AppColors.green),
              if (drawn > 0) _bar(drawnFrac, AppColors.mutedForeground),
              _bar(lostFrac, const Color(0xFFEF5350)),
            ],
          ),
        ),
      ],
    );
  }

  Widget _label(String text, Color color) => Text(
    text,
    style: TextStyle(
        color: color, fontSize: 12, fontWeight: FontWeight.w600),
  );

  Widget _bar(double fraction, Color color) => Expanded(
    flex: (fraction * 100).round(),
    child: Container(height: 6, color: color),
  );
}

// ─── Match Card ───────────────────────────────────────────────────────────────

class MatchCard extends StatelessWidget {
  final MatchData match;
  final String? currentPlayerId;

  const MatchCard({super.key, required this.match, this.currentPlayerId});

  @override
  Widget build(BuildContext context) {
    final isHome = match.homeId == currentPlayerId;
    final won = (isHome && match.result == 'home_win') ||
        (!isHome && match.result == 'away_win');
    final lost = (isHome && match.result == 'away_win') ||
        (!isHome && match.result == 'home_win');

    Color resultColor;
    String resultLabel;
    if (won) {
      resultColor = AppColors.green;
      resultLabel = 'W';
    } else if (lost) {
      resultColor = const Color(0xFFEF5350);
      resultLabel = 'L';
    } else {
      resultColor = AppColors.mutedForeground;
      resultLabel = 'D';
    }

    return Container(
      padding: const EdgeInsets.all(14),
      decoration: BoxDecoration(
        color: AppColors.card,
        borderRadius: BorderRadius.circular(14),
        border: Border.all(color: AppColors.border),
      ),
      child: Row(
        children: [
          Container(
            width: 32,
            height: 32,
            decoration: BoxDecoration(
              color: resultColor.withOpacity(0.15),
              borderRadius: BorderRadius.circular(8),
            ),
            child: Center(
              child: Text(
                resultLabel,
                style: TextStyle(
                    color: resultColor,
                    fontWeight: FontWeight.w800,
                    fontSize: 14),
              ),
            ),
          ),
          const SizedBox(width: 12),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  '${match.homeName} vs ${match.awayName}',
                  style: const TextStyle(
                    color: AppColors.foreground,
                    fontWeight: FontWeight.w600,
                    fontSize: 14,
                  ),
                ),
                if (match.matchDate != null)
                  Text(
                    DateFormat('d MMM yyyy').format(match.matchDate!),
                    style: const TextStyle(
                        color: AppColors.mutedForeground, fontSize: 11),
                  ),
              ],
            ),
          ),
          if (match.score != null)
            Text(
              match.score!,
              style: const TextStyle(
                color: AppColors.foreground,
                fontWeight: FontWeight.w700,
                fontSize: 15,
                letterSpacing: 0.5,
              ),
            ),
        ],
      ),
    );
  }
}

// ─── Quick Nav Card (for unlinked users) ─────────────────────────────────────

class QuickNavCard extends StatefulWidget {
  final IconData icon;
  final String label;
  final Color bgColor;
  final Color iconColor;
  final VoidCallback onTap;

  const QuickNavCard({
    super.key,
    required this.icon,
    required this.label,
    required this.bgColor,
    required this.iconColor,
    required this.onTap,
  });

  @override
  State<QuickNavCard> createState() => _QuickNavCardState();
}

class _QuickNavCardState extends State<QuickNavCard> {
  bool _pressed = false;

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTapDown: (_) => setState(() => _pressed = true),
      onTapUp: (_) {
        setState(() => _pressed = false);
        widget.onTap();
      },
      onTapCancel: () => setState(() => _pressed = false),
      child: AnimatedScale(
        scale: _pressed ? 0.97 : 1.0,
        duration: const Duration(milliseconds: 100),
        child: Container(
          padding: const EdgeInsets.all(16),
          decoration: BoxDecoration(
            color: widget.bgColor,
            borderRadius: BorderRadius.circular(16),
          ),
          child: Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text(
                widget.label,
                style: TextStyle(
                  color: widget.iconColor,
                  fontSize: 14,
                  fontWeight: FontWeight.w600,
                ),
              ),
              Icon(widget.icon,
                  size: 22,
                  color: widget.iconColor.withOpacity(0.6)),
            ],
          ),
        ),
      ),
    );
  }
}

// ─── Live Tournament Card ─────────────────────────────────────────────────────

class LiveTournamentCard extends StatefulWidget {
  final TournamentData tournament;
  final VoidCallback onTap;

  const LiveTournamentCard(
      {super.key, required this.tournament, required this.onTap});

  @override
  State<LiveTournamentCard> createState() => _LiveTournamentCardState();
}

class _LiveTournamentCardState extends State<LiveTournamentCard>
    with SingleTickerProviderStateMixin {
  late final AnimationController _pulse;

  @override
  void initState() {
    super.initState();
    _pulse = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 900),
    )..repeat(reverse: true);
  }

  @override
  void dispose() {
    _pulse.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: widget.onTap,
      child: Container(
        padding: const EdgeInsets.all(16),
        decoration: BoxDecoration(
          color: AppColors.card,
          borderRadius: BorderRadius.circular(16),
          border: Border.all(color: AppColors.borderActive, width: 1.5),
        ),
        child: Row(
          children: [
            AnimatedBuilder(
              animation: _pulse,
              builder: (_, __) => Container(
                width: 8,
                height: 8,
                decoration: BoxDecoration(
                  shape: BoxShape.circle,
                  color: AppColors.green
                      .withOpacity(0.4 + _pulse.value * 0.6),
                ),
              ),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    widget.tournament.name,
                    overflow: TextOverflow.ellipsis,
                    style: const TextStyle(
                      color: AppColors.foreground,
                      fontWeight: FontWeight.w700,
                      fontSize: 15,
                    ),
                  ),
                  Text(
                    '${widget.tournament.type} · ${widget.tournament.participantType}',
                    style: const TextStyle(
                        color: AppColors.mutedForeground, fontSize: 12),
                  ),
                ],
              ),
            ),
            const SizedBox(width: 8),
            const Icon(Icons.emoji_events_rounded,
                size: 18, color: AppColors.amber),
          ],
        ),
      ),
    );
  }
}

// ─── Section Header ───────────────────────────────────────────────────────────

class SectionHeader extends StatelessWidget {
  final String title;
  final String linkLabel;
  final VoidCallback onLinkTap;

  const SectionHeader({
    super.key,
    required this.title,
    required this.linkLabel,
    required this.onLinkTap,
  });

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Text(title,
            style: const TextStyle(
                color: AppColors.foreground,
                fontSize: 18,
                fontWeight: FontWeight.w800,
                letterSpacing: -0.3)),
        GestureDetector(
          onTap: onLinkTap,
          child: Text(linkLabel,
              style: const TextStyle(
                  color: AppColors.primary,
                  fontSize: 13,
                  fontWeight: FontWeight.w600)),
        ),
      ],
    );
  }
}

// ─── Skeleton pulse ───────────────────────────────────────────────────────────

class PulseSkeleton extends StatefulWidget {
  final double height;
  final double radius;

  const PulseSkeleton({super.key, required this.height, this.radius = 14});

  @override
  State<PulseSkeleton> createState() => _PulseSkeletonState();
}

class _PulseSkeletonState extends State<PulseSkeleton>
    with SingleTickerProviderStateMixin {
  late final AnimationController _ctrl;

  @override
  void initState() {
    super.initState();
    _ctrl = AnimationController(
        vsync: this, duration: const Duration(milliseconds: 900))
      ..repeat(reverse: true);
  }

  @override
  void dispose() {
    _ctrl.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return AnimatedBuilder(
      animation: _ctrl,
      builder: (_, __) => Container(
        height: widget.height,
        decoration: BoxDecoration(
          color: AppColors.muted.withOpacity(0.4 + _ctrl.value * 0.4),
          borderRadius: BorderRadius.circular(widget.radius),
        ),
      ),
    );
  }
}

// ─── Home Screen ──────────────────────────────────────────────────────────────

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  PlayerProfile? _currentPlayer;
  String? _fullName;
  List<MatchData> _recentMatches = [];
  List<TournamentData> _liveTournaments = [];
  bool _loadingMatches = true;

  @override
  void initState() {
    super.initState();
    _loadData();
  }

  Future<void> _loadData() async {
    try {
      final user = FirebaseAuth.instance.currentUser;
      if (user == null) return;

      // Fetch user profile
      final userDoc = await FirebaseFirestore.instance
          .collection('users')
          .doc(user.uid)
          .get();
      if (userDoc.exists) {
        final userData = userDoc.data()!;
        _fullName = userData['full_name'];
        final playerId = userData['player_id'];

        if (playerId != null) {
          final playerDoc = await FirebaseFirestore.instance
              .collection('players')
              .doc(playerId)
              .get();
          if (playerDoc.exists) {
            setState(() => _currentPlayer = PlayerProfile.fromFirestore(playerDoc));
          }
        }
      }

      // Parallel fetch: matches + tournaments
      final matchSnap = FirebaseFirestore.instance
          .collection('matches')
          .orderBy('match_date', descending: true)
          .limit(20)
          .get();
      final tourneySnap = FirebaseFirestore.instance
          .collection('tournaments')
          .orderBy('start_date', descending: true)
          .limit(10)
          .get();

      final results = await Future.wait([matchSnap, tourneySnap]);

      final matches =
      results[0].docs.map(MatchData.fromFirestore).toList();
      final tournaments =
      results[1].docs.map(TournamentData.fromFirestore).toList();

      final live = tournaments
          .where((t) => t.status == 'in_progress')
          .take(2)
          .toList();

      List<MatchData> recent;
      if (_currentPlayer != null) {
        recent = matches
            .where((m) =>
        (m.homeId == _currentPlayer!.id ||
            m.awayId == _currentPlayer!.id) &&
            m.result != 'pending')
            .take(5)
            .toList();
      } else {
        recent = matches.where((m) => m.result != 'pending').take(3).toList();
      }

      setState(() {
        _liveTournaments = live;
        _recentMatches = recent;
        _loadingMatches = false;
      });
    } catch (_) {
      setState(() => _loadingMatches = false);
    }
  }

  String get _greeting {
    if (_fullName != null) {
      final first = _fullName!.split(' ').first;
      return 'Hey, $first 👋';
    }
    return 'Welcome back 👋';
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.background,
      body: SafeArea(
        child: RefreshIndicator(
          onRefresh: _loadData,
          color: AppColors.primary,
          backgroundColor: AppColors.card,
          child: SingleChildScrollView(
            physics: const AlwaysScrollableScrollPhysics(),
            padding: const EdgeInsets.fromLTRB(16, 20, 16, 32),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                // ── Greeting ──────────────────────────────────────────
                FadeUpSection(
                  delayMs: 0,
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        DateFormat('EEEE, d MMMM yyyy').format(DateTime.now()),
                        style: const TextStyle(
                            color: AppColors.mutedForeground, fontSize: 13),
                      ),
                      const SizedBox(height: 4),
                      Text(
                        _greeting,
                        style: const TextStyle(
                          color: AppColors.foreground,
                          fontSize: 30,
                          fontWeight: FontWeight.w800,
                          letterSpacing: -0.5,
                        ),
                      ),
                      if (_currentPlayer != null) ...[
                        const SizedBox(height: 4),
                        Row(
                          children: [
                            Text(
                              [
                                if (_currentPlayer!.nickname != null)
                                  '"${_currentPlayer!.nickname}"',
                                _currentPlayer!.teamName ?? 'No team assigned',
                              ].join(' · '),
                              style: const TextStyle(
                                  color: AppColors.mutedForeground,
                                  fontSize: 13),
                            ),
                            if (_currentPlayer!.isCaptain) ...[
                              const SizedBox(width: 8),
                              Container(
                                padding: const EdgeInsets.symmetric(
                                    horizontal: 8, vertical: 2),
                                decoration: BoxDecoration(
                                  color: const Color(0xFF3A2A00),
                                  borderRadius: BorderRadius.circular(20),
                                ),
                                child: const Text(
                                  'Captain',
                                  style: TextStyle(
                                    color: AppColors.amber,
                                    fontSize: 11,
                                    fontWeight: FontWeight.w700,
                                  ),
                                ),
                              ),
                            ],
                          ],
                        ),
                      ],
                    ],
                  ),
                ),

                const SizedBox(height: 24),

                // ── Stats or Explore ───────────────────────────────────
                FadeUpSection(
                  delayMs: 50,
                  child: _currentPlayer != null
                      ? _buildMyStats()
                      : _buildExploreGrid(),
                ),

                const SizedBox(height: 24),

                // ── My Team ────────────────────────────────────────────
                if (_currentPlayer?.teamId != null) ...[
                  FadeUpSection(
                    delayMs: 100,
                    child: _buildTeamCard(),
                  ),
                  const SizedBox(height: 24),
                ],

                // ── Live Tournaments ───────────────────────────────────
                if (_liveTournaments.isNotEmpty) ...[
                  FadeUpSection(
                    delayMs: 120,
                    child: Column(
                      children: [
                        SectionHeader(
                          title: 'Live Now',
                          linkLabel: 'See all',
                          onLinkTap: () =>
                              Navigator.pushNamed(context, '/tournaments'),
                        ),
                        const SizedBox(height: 12),
                        ..._liveTournaments.map((t) => Padding(
                          padding: const EdgeInsets.only(bottom: 8),
                          child: LiveTournamentCard(
                            tournament: t,
                            onTap: () => Navigator.pushNamed(
                                context, '/tournaments/${t.id}'),
                          ),
                        )),
                      ],
                    ),
                  ),
                  const SizedBox(height: 24),
                ],

                // ── Recent Matches ─────────────────────────────────────
                FadeUpSection(
                  delayMs: 150,
                  child: Column(
                    children: [
                      SectionHeader(
                        title: _currentPlayer != null
                            ? 'My Recent Matches'
                            : 'Recent Results',
                        linkLabel: 'See all',
                        onLinkTap: () =>
                            Navigator.pushNamed(context, '/matches'),
                      ),
                      const SizedBox(height: 12),
                      if (_loadingMatches)
                        Column(
                          children: [
                            const PulseSkeleton(height: 72),
                            const SizedBox(height: 8),
                            const PulseSkeleton(height: 72),
                          ],
                        )
                      else if (_recentMatches.isEmpty)
                        Container(
                          padding: const EdgeInsets.all(24),
                          decoration: BoxDecoration(
                            color: AppColors.muted,
                            borderRadius: BorderRadius.circular(16),
                          ),
                          child: Center(
                            child: Text(
                              _currentPlayer != null
                                  ? 'No matches logged for you yet.'
                                  : 'No results yet.',
                              style: const TextStyle(
                                  color: AppColors.mutedForeground,
                                  fontSize: 13),
                            ),
                          ),
                        )
                      else
                        Column(
                          children: _recentMatches
                              .map((m) => Padding(
                            padding: const EdgeInsets.only(bottom: 8),
                            child: MatchCard(
                              match: m,
                              currentPlayerId: _currentPlayer?.id,
                            ),
                          ))
                              .toList(),
                        ),
                    ],
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  // ── My Stats section ──────────────────────────────────────────────────────

  Widget _buildMyStats() {
    final p = _currentPlayer!;
    return Column(
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            const Text('My Stats',
                style: TextStyle(
                    color: AppColors.foreground,
                    fontSize: 18,
                    fontWeight: FontWeight.w800,
                    letterSpacing: -0.3)),
            GestureDetector(
              onTap: () =>
                  Navigator.pushNamed(context, '/players/${p.id}'),
              child: Row(
                children: const [
                  Text('Full profile',
                      style: TextStyle(
                          color: AppColors.primary,
                          fontSize: 13,
                          fontWeight: FontWeight.w600)),
                  SizedBox(width: 2),
                  Icon(Icons.chevron_right_rounded,
                      size: 16, color: AppColors.primary),
                ],
              ),
            ),
          ],
        ),
        const SizedBox(height: 12),
        Container(
          padding: const EdgeInsets.all(16),
          decoration: BoxDecoration(
            color: AppColors.card,
            borderRadius: BorderRadius.circular(16),
            border: Border.all(color: AppColors.border),
          ),
          child: Column(
            children: [
              WinRateBar(
                won: p.matchesWon,
                lost: p.matchesLost,
                drawn: p.matchesDrawn,
                total: p.matchesTotal,
              ),
              const SizedBox(height: 16),
              GridView.count(
                crossAxisCount: 2,
                shrinkWrap: true,
                physics: const NeverScrollableScrollPhysics(),
                crossAxisSpacing: 12,
                mainAxisSpacing: 12,
                childAspectRatio: 2.2,
                children: [
                  StatCard(
                    label: 'Match Win Rate',
                    value: p.matchWinRate != null
                        ? '${p.matchWinRate}%'
                        : '—',
                    accentColor: AppColors.green,
                  ),
                  StatCard(
                    label: 'Frame Win Rate',
                    value: p.frameWinRate != null
                        ? '${p.frameWinRate}%'
                        : '—',
                    accentColor: AppColors.blue,
                  ),
                  StatCard(
                    label: 'Matches Played',
                    value: '${p.matchesTotal}',
                  ),
                  StatCard(
                    label: 'Breaks',
                    value: '${p.breaks}',
                    accentColor: AppColors.amber,
                  ),
                ],
              ),
            ],
          ),
        ),
      ],
    );
  }

  // ── Explore grid (no player linked) ──────────────────────────────────────

  Widget _buildExploreGrid() {
    final items = [
      (
      Icons.groups_rounded,
      'Players',
      const Color(0xFF1A1A2E),
      AppColors.primary,
      '/players'
      ),
      (
      Icons.emoji_events_rounded,
      'Tournaments',
      const Color(0xFF2A1A00),
      AppColors.amber,
      '/tournaments'
      ),
      (
      Icons.bar_chart_rounded,
      'Leaderboard',
      const Color(0xFF1E1A2E),
      AppColors.purple,
      '/leaderboard'
      ),
      (
      Icons.person_rounded,
      'My Profile',
      const Color(0xFF001A2E),
      AppColors.blue,
      '/profile'
      ),
    ];

    return Column(
      children: [
        GridView.count(
          crossAxisCount: 2,
          shrinkWrap: true,
          physics: const NeverScrollableScrollPhysics(),
          crossAxisSpacing: 12,
          mainAxisSpacing: 12,
          childAspectRatio: 2.5,
          children: items
              .map((item) => QuickNavCard(
            icon: item.$1,
            label: item.$2,
            bgColor: item.$3,
            iconColor: item.$4,
            onTap: () => Navigator.pushNamed(context, item.$5),
          ))
              .toList(),
        ),
        const SizedBox(height: 10),
        const Text(
          'Ask an admin to link your account to your player profile.',
          textAlign: TextAlign.center,
          style: TextStyle(color: AppColors.mutedForeground, fontSize: 12),
        ),
      ],
    );
  }

  // ── My Team card ──────────────────────────────────────────────────────────

  Widget _buildTeamCard() {
    final p = _currentPlayer!;
    return GestureDetector(
      onTap: () => Navigator.pushNamed(context, '/profile'),
      child: Container(
        padding: const EdgeInsets.all(16),
        decoration: BoxDecoration(
          color: AppColors.card,
          borderRadius: BorderRadius.circular(16),
          border: Border.all(color: AppColors.border),
        ),
        child: Row(
          children: [
            Container(
              width: 40,
              height: 40,
              decoration: BoxDecoration(
                color: const Color(0xFF001A2E),
                borderRadius: BorderRadius.circular(12),
              ),
              child: const Icon(Icons.groups_rounded,
                  size: 20, color: AppColors.blue),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    p.teamName ?? 'My Team',
                    style: const TextStyle(
                      color: AppColors.foreground,
                      fontWeight: FontWeight.w700,
                      fontSize: 15,
                    ),
                  ),
                  Text(
                    p.isCaptain
                        ? 'View team & manage players'
                        : 'View team & teammates',
                    style: const TextStyle(
                        color: AppColors.mutedForeground, fontSize: 12),
                  ),
                ],
              ),
            ),
            const Icon(Icons.chevron_right_rounded,
                size: 18, color: AppColors.mutedForeground),
          ],
        ),
      ),
    );
  }
}
