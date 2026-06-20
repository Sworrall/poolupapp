export const colors = {
  background: '#0F0F13',
  card: '#1A1A24',
  muted: '#1E1E2A',
  border: '#2A2A3A',
  foreground: '#F0F0F5',
  mutedForeground: '#6B6B80',
  primary: '#4F8EFF',
  green: '#4CAF50',
  red: '#EF5350',
  amber: '#FFC107',
} as const;

export const spacing = {
  xs: 4,
  sm: 8,
  md: 12,
  lg: 16,
  xl: 24,
} as const;

export const radius = {
  sm: 8,
  md: 12,
  lg: 16,
  pill: 999,
} as const;

export const typography = {
  title: { fontSize: 28, fontWeight: '800' as const, letterSpacing: -0.5 },
  heading: { fontSize: 18, fontWeight: '700' as const },
  body: { fontSize: 14, fontWeight: '400' as const },
  caption: { fontSize: 12, fontWeight: '400' as const },
  label: { fontSize: 13, fontWeight: '600' as const },
};
