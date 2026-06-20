import Constants from 'expo-constants';
import { Platform } from 'react-native';

function resolveDevHost(): string {
  if (Platform.OS === 'android') {
    return '10.0.2.2';
  }

  const hostUri = Constants.expoConfig?.hostUri;
  if (hostUri) {
    return hostUri.split(':')[0] ?? 'localhost';
  }

  return 'localhost';
}

const DEV_URL = `http://${resolveDevHost()}:8080`;

/** Set EXPO_PUBLIC_API_URL for staging/production builds. */
export const API_BASE_URL =
  process.env.EXPO_PUBLIC_API_URL?.replace(/\/$/, '') ?? DEV_URL;
