# Nifty 50 Widget React Native App

This is a React Native application that provides a home screen widget to display the Nifty 50 stock index data. The widget scrapes data from the NSE India website and updates every 5 seconds during market hours.

## Features

- **Real-time Data**: The widget scrapes data from the NSE India website to provide the latest Nifty 50 data.
- **Comprehensive Information**: The widget displays the current price, open, high, low, and close values.
- **Auto-refresh**: The widget automatically refreshes every 5 seconds when the market is open (9:15 AM to 3:30 PM IST, Monday-Friday).
- **Efficient**: The widget is designed to be battery-efficient by only updating when the market is open.

## Prerequisites

- **Node.js**: Make sure you have Node.js installed. You can download it from [nodejs.org](https://nodejs.org/).
- **React Native CLI**: You will need the React Native command-line interface. You can install it by running `npm install -g react-native-cli`.
- **Android Studio**: You will need Android Studio to run the application on an Android emulator or a physical device. You can download it from the [Android Developer website](https://developer.android.com/studio).
- **Java Development Kit (JDK)**: Make sure you have the JDK installed. You can download it from the [Oracle website](https://www.oracle.com/java/technologies/javase-downloads.html).

## Installation

1. **Clone the repository**:
   ```sh
   git clone https://github.com/your-username/NiftyWidgetApp.git
   cd NiftyWidgetApp
   ```

2. **Install dependencies**:
   ```sh
   npm install
   ```

## Running the Application

1. **Start the Metro server**:
   ```sh
   npm start
   ```

2. **Run the application on Android**:
   ```sh
   npm run android
   ```

   This will build the application and run it on an Android emulator or a connected physical device.

## Adding the Widget to Your Home Screen

1. **Long-press on an empty area of your home screen.**
2. **Select "Widgets".**
3. **Find "NiftyWidgetApp" in the list of widgets.**
4. **Drag and drop the widget onto your home screen.**

The widget will initially display "Loading..." and then start showing the Nifty 50 data. If there is an error while fetching the data, the widget will display "Error".

## Debugging

If you encounter issues with the widget (e.g., it's stuck on "Loading..." or "Error"), you can view the application's logs to diagnose the problem.

### Using Android Studio

1.  **Open the project in Android Studio**: Open the `android` folder of the project in Android Studio.
2.  **Open Logcat**: At the bottom of the screen, click on the "Logcat" tab.
3.  **Filter the logs**: In the search bar of the Logcat window, enter `NiftyWidget` to filter the logs and only see messages from the widget.

### Using the Command Line

1.  **Open a terminal or command prompt.**
2.  **Run the following command**:
    ```sh
    adb logcat -s "NiftyWidget"
    ```

This will show you the logs from the widget, which can help you identify any errors that are occurring.
