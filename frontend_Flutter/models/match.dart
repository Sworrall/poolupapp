import 'package:flutter/material.dart';
import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:intl/intl.dart';

// ─── Import from your shared files in your project ───────────────────────────
// import 'theme.dart';         → AppColors
// import 'home_screen.dart';   → MatchData, MatchCard, PulseSkeleton, FadeUpSection
// import 'tournaments_screen.dart' → PageHeader, FilterTabs, EmptyState
//
// Everything is inlined here for standalone use — refactor once you have a
// proper project structure.

// ─── AppColors (move to theme.dart) ──────────────────────────────────────────

class AppColors {
  static const background = Color(0xFF0F0F13);
  static const card = Color(0xFF1A1A24);
  static const muted = Color(0xFF1E1E2A);
  static const border = Color(0xFF2A2A3A);
  static const foreground = Color(0xFFF0F0F5);
  static const mutedForeground = Color(0xFF6B6B80);
  static const primary = Color(0xFF4F8EFF);
  static const green = Color(0xFF4CAF50);
  static const red = Color(0xFFEF5350);
  static const amber = Color(0xFFFFC107);
}

// ─── Model ────────────────────────────────────────────────────────────────────

class MatchData {
  final String id;
  final String homeId;
  final String awayId;
  final String homeName;
  final String awayName;
  final String result; // 'home_win' | 'away_win' | 'draw' | 'pending'
  final String? score;
  final DateTime? matchDate;
  final String? tournamentName;

  MatchData({
    required this.id,
    required this.homeId,
    required this.awayId,
    required this.homeName,
    required this.awayName,
    required this.result,
    this.score,
    this.matchDate,
    this.tournamentName,
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
      tournamentName: d['tournament_name'],
    );
  }

  bool get isPending => result == 'pending';
}

// ─── Match Card ───────────────────────────────────────────────────────────────

class MatchCard extends StatelessWidget {
  final MatchData match;
  final String? currentPlayerId;

  const MatchCard({super.key, required this.match, this.currentPlayerId});

  @override
  Widget build(BuildContext context) {
    final isPending = match.isPending;

    // Result pill
    Color pillColor;
    Color pillBg;
    String pillLabel;

    if (isPending) {
      pillColor = AppColors.amber;
      pillBg = const Color(0xFF2A2000);
      pillLabel = 'Pending';
    } else if (match.result == 'home_win') {
      pillColor = AppColors.green;
      pillBg = const Color(0xFF0A2A0A);
      pillLabel = 'H';
    } else if (match.result == 'away_win') {
      pillColor = AppColors.red;
      pillBg = const Color(0xFF2A0A0A);
      pillLabel = 'A';
    } else {
      pillColor = AppColors.mutedForeground;
      pillBg = const Color(0xFF1E1E2A);
      pillLabel = 'D';
    }

    // If we know the current player, show personalised W/L/D
    if (currentPlayerId != null && !isPending) {
      final isHome = match.homeId == currentPlayerId;
      final won = (isHome && match.result == 'home_win') ||
          (!isHome && match.result == 'away_win');
      final lost = (isHome && match.result == 'away_win') ||
          (!isHome && match.result == 'home_win');

      if (won) {
        pillColor = AppColors.green;
        pillBg = const Color(0xFF0A2A0A);
        pillLabel = 'W';
      } else if (lost) {
        pillColor = AppColors.red;
        pillBg = const Color(0xFF2A0A0A);
        pillLabel = 'L';
      } else {
        pillColor = AppColors.mutedForeground;
        pillBg = const Color(0xFF1E1E2A);
        pillLabel = 'D';
      }
    }

    return Container(
      padding: const EdgeInsets.all(14),
      decoration: BoxDecoration(
        color: AppColors.card,
        borderRadius: BorderRadius.circular(16),
        border: Border.all(color: AppColors.border),
      ),
      child: Row(
        children: [
          // Result pill
          Container(
            width: 36,
            height: 36,
            decoration: BoxDecoration(
              color: pillBg,
              borderRadius: BorderRadius.circular(10),
            ),
            child: Center(
              child: Text(
                pillLabel,
                style: TextStyle(
                  color: pillColor,
                  fontWeight: FontWeight.w800,
                  fontSize: isPending ? 10 : 15,
                ),
              ),
            ),
          ),
          const SizedBox(width: 12),

          // Teams + meta
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
                    height: 1.2,
                  ),
                ),
                const SizedBox(height: 3),
                Row(
                  children: [
                    if (match.matchDate != null)
                      Text(
                        DateFormat('d MMM yyyy').format(match.matchDate!),
                        style: const TextStyle(
                            color: AppColors.mutedForeground, fontSize: 11),
                      ),
                    if (match.matchDate != null &&
                        match.tournamentName != null) ...[
                      const Text(' · ',
                          style: TextStyle(
                              color: AppColors.mutedForeground, fontSize: 11)),
                    ],
                    if (match.tournamentName != null)
                      Flexible(
                        child: Text(
                          match.tournamentName!,
                          overflow: TextOverflow.ellipsis,
                          style: const TextStyle(
                              color: AppColors.mutedForeground, fontSize: 11),
                        ),
                      ),
                  ],
                ),
              ],
            ),
          ),

          // Score
          if (match.score != null && !isPending) ...[
            const SizedBox(width: 8),
            Text(
              match.score!,
              style: const TextStyle(
                color: AppColors.foreground,
                fontWeight: FontWeight.w700,
                fontSize: 16,
                letterSpacing: 0.5,
              ),
            ),
          ],
        ],
      ),
    );
  }
}

// ─── Skeleton ─────────────────────────────────────────────────────────────────

class _PulseSkeleton extends StatefulWidget {
  const _PulseSkeleton();

  @override
  State<_PulseSkeleton> createState() => _PulseSkeletonState();
}

class _PulseSkeletonState extends State<_PulseSkeleton>
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
  Widget build(BuildContext context) => AnimatedBuilder(
        animation: _ctrl,
        builder: (_, __) => Container(
          height: 72,
          decoration: BoxDecoration(
            color: AppColors.card.withOpacity(0.4 + _ctrl.value * 0.4),
            borderRadius: BorderRadius.circular(16),
            border: Border.all(color: AppColors.border),
          ),
        ),
      );
}

// ─── Animated card wrapper ────────────────────────────────────────────────────

class _FadeUp extends StatefulWidget {
  final Widget child;
  final int delayMs;

  const _FadeUp({required this.child, required this.delayMs});

  @override
  State<_FadeUp> createState() => _FadeUpState();
}

class _FadeUpState extends State<_FadeUp> with SingleTickerProviderStateMixin {
  late final AnimationController _ctrl;
  late final Animation<double> _opacity;
  late final Animation<Offset> _slide;

  @override
  void initState() {
    super.initState();
    _ctrl = AnimationController(
        vsync: this, duration: const Duration(milliseconds: 300));
    _opacity = Tween<double>(begin: 0, end: 1)
        .animate(CurvedAnimation(parent: _ctrl, curve: Curves.easeOut));
    _slide =
        Tween<Offset>(begin: const Offset(0, 0.06), end: Offset.zero).animate(
            CurvedAnimation(parent: _ctrl, curve: Curves.easeOut));
    Future.delayed(Duration(milliseconds: widget.delayMs),
        () { if (mounted) _ctrl.forward(); });
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

// ─── Filter Tabs ──────────────────────────────────────────────────────────────

class _FilterTabs extends StatelessWidget {
  final String selected;
  final ValueChanged<String> onChanged;

  static const _tabs = [
    ('all', 'All'),
    ('results', 'Results'),
    ('pending', 'Pending'),
  ];

  const _FilterTabs({required this.selected, required this.onChanged});

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(4),
      decoration: BoxDecoration(
        color: AppColors.muted,
        borderRadius: BorderRadius.circular(12),
      ),
      child: Row(
        children: _tabs.map((tab) {
          final isSelected = selected == tab.$1;
          return Expanded(
            child: GestureDetector(
              onTap: () => onChanged(tab.$1),
              child: AnimatedContainer(
                duration: const Duration(milliseconds: 200),
                padding: const EdgeInsets.symmetric(vertical: 9),
                decoration: BoxDecoration(
                  color:
                      isSelected ? AppColors.card : Colors.transparent,
                  borderRadius: BorderRadius.circular(8),
                  boxShadow: isSelected
                      ? [
                          BoxShadow(
                            color: Colors.black.withOpacity(0.15),
                            blurRadius: 4,
                            offset: const Offset(0, 1),
                          )
                        ]
                      : null,
                ),
                child: Text(
                  tab.$2,
                  textAlign: TextAlign.center,
                  style: TextStyle(
                    color: isSelected
                        ? AppColors.foreground
                        : AppColors.mutedForeground,
                    fontSize: 13,
                    fontWeight: FontWeight.w600,
                  ),
                ),
              ),
            ),
          );
        }).toList(),
      ),
    );
  }
}

// ─── Empty State ──────────────────────────────────────────────────────────────

class _EmptyState extends StatelessWidget {
  const _EmptyState();

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Padding(
        padding: const EdgeInsets.symmetric(vertical: 60),
        child: Column(
          children: [
            Container(
              padding: const EdgeInsets.all(20),
              decoration: const BoxDecoration(
                color: AppColors.muted,
                shape: BoxShape.circle,
              ),
              child: const Icon(Icons.track_changes_rounded,
                  color: AppColors.mutedForeground, size: 32),
            ),
            const SizedBox(height: 16),
            const Text('No matches found',
                style: TextStyle(
                    color: AppColors.foreground,
                    fontSize: 16,
                    fontWeight: FontWeight.w600)),
            const SizedBox(height: 6),
            const Text(
              'Match results logged by admins will appear here.',
              textAlign: TextAlign.center,
              style:
                  TextStyle(color: AppColors.mutedForeground, fontSize: 13),
            ),
          ],
        ),
      ),
    );
  }
}

// ─── Matches Screen ───────────────────────────────────────────────────────────

class MatchesScreen extends StatefulWidget {
  const MatchesScreen({super.key});

  @override
  State<MatchesScreen> createState() => _MatchesScreenState();
}

class _MatchesScreenState extends State<MatchesScreen> {
  List<MatchData> _matches = [];
  bool _loading = true;
  String _filter = 'all';

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load() async {
    setState(() => _loading = true);
    try {
      final snap = await FirebaseFirestore.instance
          .collection('matches')
          .orderBy('match_date', descending: true)
          .limit(100)
          .get();

      setState(() {
        _matches = snap.docs.map(MatchData.fromFirestore).toList();
        _loading = false;
      });
    } catch (_) {
      setState(() => _loading = false);
    }
  }

  List<MatchData> get _filtered {
    switch (_filter) {
      case 'pending':
        return _matches.where((m) => m.isPending).toList();
      case 'results':
        return _matches.where((m) => !m.isPending).toList();
      default:
        return _matches;
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.background,
      body: SafeArea(
        child: RefreshIndicator(
          onRefresh: _load,
          color: AppColors.primary,
          backgroundColor: AppColors.card,
          child: CustomScrollView(
            slivers: [
              // ── Header ──────────────────────────────────────────────
              SliverToBoxAdapter(
                child: Padding(
                  padding: const EdgeInsets.fromLTRB(16, 16, 16, 12),
                  child: Row(
                    crossAxisAlignment: CrossAxisAlignment.end,
                    children: [
                      const Text(
                        'Matches',
                        style: TextStyle(
                          color: AppColors.foreground,
                          fontSize: 28,
                          fontWeight: FontWeight.w800,
                          letterSpacing: -0.5,
                        ),
                      ),
                      const SizedBox(width: 8),
                      Padding(
                        padding: const EdgeInsets.only(bottom: 3),
                        child: Text(
                          '${_matches.length} total',
                          style: const TextStyle(
                              color: AppColors.mutedForeground, fontSize: 13),
                        ),
                      ),
                    ],
                  ),
                ),
              ),

              // ── Filter tabs ─────────────────────────────────────────
              SliverToBoxAdapter(
                child: Padding(
                  padding:
                      const EdgeInsets.symmetric(horizontal: 16).copyWith(bottom: 16),
                  child: _FilterTabs(
                    selected: _filter,
                    onChanged: (v) => setState(() => _filter = v),
                  ),
                ),
              ),

              // ── Content ─────────────────────────────────────────────
              if (_loading)
                SliverPadding(
                  padding: const EdgeInsets.symmetric(horizontal: 16),
                  sliver: SliverList(
                    delegate: SliverChildBuilderDelegate(
                      (_, __) => const Padding(
                        padding: EdgeInsets.only(bottom: 12),
                        child: _PulseSkeleton(),
                      ),
                      childCount: 4,
                    ),
                  ),
                )
              else if (_filtered.isEmpty)
                const SliverToBoxAdapter(child: _EmptyState())
              else
                SliverPadding(
                  padding: const EdgeInsets.symmetric(horizontal: 16),
                  sliver: SliverList(
                    delegate: SliverChildBuilderDelegate(
                      (context, i) {
                        final m = _filtered[i];
                        return Padding(
                          padding: const EdgeInsets.only(bottom: 12),
                          child: _FadeUp(
                            delayMs: (i * 30).clamp(0, 400),
                            child: MatchCard(match: m),
                          ),
                        );
                      },
                      childCount: _filtered.length,
                    ),
                  ),
                ),
            ],
          ),
        ),
      ),
    );
  }
}
