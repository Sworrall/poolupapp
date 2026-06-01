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

    final theme = Theme.of(context);

    if (total == 0) {

      return Container(

        height: 8,

        decoration: BoxDecoration(

          color: theme.colorScheme.surfaceContainerHighest,

          borderRadius: BorderRadius.circular(999),

        ),

      );

    }

    final wonFlex = won;

    final lostFlex = lost;

    final drawnFlex = drawn;

    return ClipRRect(

      borderRadius: BorderRadius.circular(999),

      child: SizedBox(

        height: 8,

        child: Row(

          children: [

            if (wonFlex > 0)

              Expanded(

                flex: wonFlex,

                child: Container(

                  color: Colors.green,

                ),

              ),

            if (drawnFlex > 0)

              Expanded(

                flex: drawnFlex,

                child: Container(

                  color: Colors.orange,

                ),

              ),

            if (lostFlex > 0)

              Expanded(

                flex: lostFlex,

                child: Container(

                  color: Colors.red,

                ),

              ),

          ],

        ),

      ),

    );

  }

}