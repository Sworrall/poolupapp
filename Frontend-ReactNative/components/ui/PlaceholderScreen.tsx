import { StyleSheet, Text, View } from 'react-native';

import { ScreenShell } from '@/components/ui/ScreenShell';
import { colors, radius, spacing, typography } from '@/constants/theme';

type PlaceholderScreenProps = {
    title: string;
    description: string;
};

export function PlaceholderScreen({ title, description }: PlaceholderScreenProps) {
    return (
        <ScreenShell title={title}>
            <View style={styles.card}>
                <Text style={styles.cardTitle}>Ready to build</Text>
                <Text style={styles.cardBody}>{description}</Text>
            </View>
        </ScreenShell>
    );
}

const styles = StyleSheet.create({
    card: {
        backgroundColor: colors.card,
        borderRadius: radius.lg,
        borderWidth: 1,
        borderColor: colors.border,
        padding: spacing.lg,
        gap: spacing.sm,
    },
    cardTitle: {
        ...typography.heading,
        color: colors.foreground,
    },
    cardBody: {
        ...typography.body,
        color: colors.mutedForeground,
        lineHeight: 20,
    },
});
