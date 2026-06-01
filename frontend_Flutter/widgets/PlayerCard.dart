import 'package:flutter/material.dart';

class PlayerCard extends StatelessWidget {

  final Player player;

  const PlayerCard({

    super.key,

    required this.player,

  });

  int get winRate {

    if (player.matchesTotal > 0) {

      return ((player.matchesWon / player.matchesTotal) * 100).round();

    }

    return 0;

  }

  @override

  Widget build(BuildContext context) {

    final theme = Theme.of(context);

    return InkWell(

      borderRadius: BorderRadius.circular(24),

      onTap: () {

        Navigator.pushNamed(

          context,

          '/players/${player.id}',

        );

      },

      child: AnimatedContainer(

        duration: const Duration(milliseconds: 200),

        curve: Curves.easeInOut,

        padding: const EdgeInsets.all(16),

        decoration: BoxDecoration(

          color: theme.cardColor,

          borderRadius: BorderRadius.circular(24),

          border: Border.all(

            color: theme.dividerColor,

          ),

        ),

        child: Column(

          crossAxisAlignment: CrossAxisAlignment.start,

          children: [

            // Top Row

            Row(

              crossAxisAlignment: CrossAxisAlignment.center,

              children: [

                // Avatar

                Container(

                  width: 48,

                  height: 48,

                  decoration: BoxDecoration(

                    color: theme.colorScheme.surfaceContainerHighest,

                    borderRadius: BorderRadius.circular(16),

                  ),

                  clipBehavior: Clip.antiAlias,

                  child: player.avatarUrl != null &&

                      player.avatarUrl!.isNotEmpty

                      ? Image.network(

                    player.avatarUrl!,

                    fit: BoxFit.cover,

                  )

                      : Icon(

                    Icons.person,

                    size: 22,

                    color: theme.colorScheme.onSurfaceVariant,

                  ),

                ),

                const SizedBox(width: 12),

                // Name + Nickname

                Expanded(

                  child: Column(

                    crossAxisAlignment: CrossAxisAlignment.start,

                    children: [

                      Text(

                        player.name,

                        maxLines: 1,

                        overflow: TextOverflow.ellipsis,

                        style: theme.textTheme.titleMedium?.copyWith(

                          fontWeight: FontWeight.bold,

                        ),

                      ),

                      if (player.nickname != null &&

                          player.nickname!.isNotEmpty)

                        Text(

                          '"${player.nickname}"',

                          style: theme.textTheme.bodySmall?.copyWith(

                            color: theme.colorScheme.onSurfaceVariant,

                            fontSize: 12,

                          ),

                        ),

                    ],

                  ),

                ),

                // Win Rate

                Column(

                  crossAxisAlignment: CrossAxisAlignment.end,

                  children: [

                    Text(

                      '$winRate%',

                      style: theme.textTheme.titleLarge?.copyWith(

                        color: theme.colorScheme.primary,

                        fontWeight: FontWeight.bold,

                      ),

                    ),

                    Text(

                      'win rate',

                      style: theme.textTheme.bodySmall?.copyWith(

                        fontSize: 10,

                        color: theme.colorScheme.onSurfaceVariant,

                      ),

                    ),

                  ],

                ),

              ],

            ),

            const SizedBox(height: 12),

            // Win Rate Bar

            WinRateBar(

              won: player.matchesWon,

              lost: player.matchesLost,

              drawn: player.matchesDrawn,

              total: player.matchesTotal,

            ),

            const SizedBox(height: 12),

            // Bottom Stats

            Row(

              mainAxisAlignment: MainAxisAlignment.spaceBetween,

              children: [

                Text(

                  '${player.matchesTotal} matches',

                  style: theme.textTheme.bodySmall?.copyWith(

                    color: theme.colorScheme.onSurfaceVariant,

                    fontSize: 12,

                  ),

                ),

                Text(

                  '${player.framesWon}F / ${player.breaks} breaks',

                  style: theme.textTheme.bodySmall?.copyWith(

                    color: theme.colorScheme.onSurfaceVariant,

                    fontSize: 12,

                  ),

                ),

              ],

            ),

          ],

        ),

      ),

    );

  }

}