/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 */

import React, { useEffect, useState } from 'react';
import { NativeModules, StatusBar, StyleSheet, Text, useColorScheme, View } from 'react-native';
import {
  SafeAreaProvider,
  useSafeAreaInsets,
} from 'react-native-safe-area-context';

const { NiftyDataModule } = NativeModules;

function App() {
  const isDarkMode = useColorScheme() === 'dark';

  return (
    <SafeAreaProvider>
      <StatusBar barStyle={isDarkMode ? 'light-content' : 'dark-content'} />
      <AppContent />
    </SafeAreaProvider>
  );
}

function AppContent() {
  const [niftyData, setNiftyData] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const data = await NiftyDataModule.getNiftyData();
        const jsonData = JSON.parse(data);
        const nifty50 = jsonData.data.find(index => index.indexSymbol === "NIFTY 50");
        setNiftyData(nifty50);
      } catch (e) {
        console.error(e);
      }
    };

    fetchData();
    const interval = setInterval(fetchData, 3000);

    return () => clearInterval(interval);
  }, []);

  const getPriceColor = () => {
    if (!niftyData) return '#FFFFFF';
    return niftyData.percentChange > 0 ? '#4CAF50' : '#F44336';
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Nifty 50 Live</Text>
      {niftyData ? (
        <View style={styles.card}>
          <Text style={[styles.price, { color: getPriceColor() }]}>{niftyData.last}</Text>
          <Text style={[styles.change, { color: getPriceColor() }]}>
            {niftyData.variation} ({niftyData.percentChange}%)
          </Text>
          <View style={styles.row}>
            <View style={styles.col}>
              <Text style={styles.label}>Open</Text>
              <Text style={styles.value}>{niftyData.open}</Text>
            </View>
            <View style={styles.col}>
              <Text style={styles.label}>High</Text>
              <Text style={styles.value}>{niftyData.high}</Text>
            </View>
          </View>
          <View style={styles.row}>
            <View style={styles.col}>
              <Text style={styles.label}>Low</Text>
              <Text style={styles.value}>{niftyData.low}</Text>
            </View>
            <View style={styles.col}>
              <Text style={styles.label}>Close</Text>
              <Text style={styles.value}>{niftyData.previousClose}</Text>
            </View>
          </View>
        </View>
      ) : (
        <Text style={styles.text}>Loading...</Text>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#1E2A3E',
  },
  title: {
    fontSize: 32,
    fontWeight: 'bold',
    color: '#FFFFFF',
    marginBottom: 20,
  },
  card: {
    backgroundColor: '#2A3B52',
    borderRadius: 10,
    padding: 20,
    width: '90%',
  },
  price: {
    fontSize: 48,
    fontWeight: 'bold',
    textAlign: 'center',
  },
  change: {
    fontSize: 18,
    textAlign: 'center',
    marginBottom: 20,
  },
  row: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 10,
  },
  col: {
    flex: 1,
    alignItems: 'center',
  },
  label: {
    fontSize: 16,
    color: '#87CEEB',
  },
  value: {
    fontSize: 20,
    color: '#FFFFFF',
    fontWeight: 'bold',
  },
  text: {
    fontSize: 18,
    color: '#FFFFFF',
  },
});

export default App;
