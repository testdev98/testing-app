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
    const interval = setInterval(fetchData, 5000);

    return () => clearInterval(interval);
  }, []);

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Nifty 50 Live Data</Text>
      {niftyData ? (
        <View>
          <Text style={styles.text}>Price: {niftyData.last}</Text>
          <Text style={styles.text}>Open: {niftyData.open}</Text>
          <Text style={styles.text}>High: {niftyData.high}</Text>
          <Text style={styles.text}>Low: {niftyData.low}</Text>
          <Text style={styles.text}>Close: {niftyData.previousClose}</Text>
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
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 20,
  },
  text: {
    fontSize: 18,
    marginBottom: 10,
  },
});

export default App;
