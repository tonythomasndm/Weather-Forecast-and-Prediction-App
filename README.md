# Weather Forecast and Prediction App

## Overview

The Weather Forecast Prediction App is an Android application designed to provide weather information for a specified date. It utilizes a free weather API to fetch historical weather data, including the maximum and minimum temperatures for the given date. The app also supports storing this information in a local database, allowing users to access weather data even without network connectivity. If the requested date is in the future, the app calculates the average temperatures based on the last 10 years of available data.

## Features

- Fetch weather data from a free weather API for a specified date.
- Display maximum and minimum temperatures for the selected date.
- Store weather data in a local database for offline access.
- Handle dates in the past, present, and future with appropriate calculations and data retrieval.

## Project Structure

### Main Files

- **MainActivity.kt**: The main activity that handles user input, data fetching, and displaying results.
- **WeatherViewModel.kt**: The ViewModel that manages data operations, including API calls and database interactions.
- **WeatherRepository.kt**: The repository that provides a clean API for data access to the rest of the application.
- **WeatherDatabase.kt**: Room database setup including the DAO and entities.
- **WeatherApiService.kt**: Retrofit interface for making API calls to the weather service.

### Key Components

- **WeatherData Composable**: Handles user input for the date, fetches weather data, and displays the results.
- **checkDateAndPrintMinAndMaxTemp**: Function to validate the date and fetch the corresponding weather data.
- **Room Database**: Used for storing and retrieving weather data locally.

## Installation

1. **Clone the Repository**: Clone this repository to your local machine.
2. **Open in Android Studio**: Open the project in Android Studio.
3. **Sync Gradle**: Allow Android Studio to sync Gradle and download necessary dependencies.
4. **API Key**: Obtain an API key from a free weather API provider (e.g., OpenWeatherMap) and add it to your project.
5. **Build and Run**: Build the project and run it on an Android device or emulator.

## Usage

1. **Launch the App**: Open the Weather Forecast Prediction app on your Android device.
2. **Enter Date**: Input the desired date in the format YYYY-MM-DD.
3. **Fetch Data**: Click on the "Get Min and Max Temperature" button to fetch and display the weather data for the specified date.
4. **View Results**: The app will display the minimum and maximum temperatures for the entered date. If the date is in the future, the average temperatures from the last 10 years will be shown.

OR

1. Locate the apk file in the repository in the following path: `app\build\outputs\apk\debug\app-debug.apk"
2. Install the apk file in your smartphone and run the application
