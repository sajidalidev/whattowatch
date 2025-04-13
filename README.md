# What to Watch

A cross-platform movie and TV show discovery application built with Kotlin Multiplatform. This application helps users discover trending, top-rated, and upcoming movies and TV shows, search for specific titles, and view detailed information about them.

## Demo

### Android App
![Android Demo](Android.webm)

### Desktop App
![Desktop Demo](Desktop.mov)

## Features

- **Movie Discovery**: Browse trending, top-rated, and upcoming movies
- **TV Show Discovery**: Explore popular, top-rated, and currently airing TV shows
- **Search Functionality**: Search for specific movies and TV shows
- **Detailed Information**: View comprehensive details about movies and TV shows
- **Cross-Platform**: Works on Android, iOS, and Desktop platforms with a shared codebase

## Technologies Used

- **Kotlin Multiplatform**: For sharing code across Android, iOS, and Desktop
- **Jetpack Compose**: For building the UI on all platforms
- **Koin**: For dependency injection
- **Ktor**: For making API requests to TMDb
- **The Movie Database (TMDb) API**: For fetching movie and TV show data

## Project Structure

- `/composeApp`: Contains the shared code for all platforms
  - `/commonMain`: Code shared across all platforms
    - `/kotlin/dev/sajidali/vod/discovery`: Main application code
      - `/remote/api`: TMDb API client implementation
      - `/remote/model`: Data models for API responses
      - `/di`: Dependency injection setup using Koin
  - `/androidMain`: Android-specific code
  - `/iosMain`: iOS-specific code
  - `/desktopMain`: Desktop-specific code
- `/iosApp`: iOS application entry point

## Getting Started

### Prerequisites

- JDK 11 or higher
- Android Studio Arctic Fox or higher (for Android development)
- Xcode 13 or higher (for iOS development)
- IntelliJ IDEA or Android Studio (for Desktop development)
- TMDb API Key (get it from [The Movie Database](https://www.themoviedb.org/))

### Setup

1. Clone the repository
2. Open the project in Android Studio, IntelliJ IDEA, or your preferred IDE
3. Replace "Your api key here" with your TMDb API key in:
   - `composeApp/src/androidMain/kotlin/dev/sajidali/vod/discovery/MainActivity.kt`
   - `composeApp/src/desktopMain/kotlin/dev/sajidali/vod/discovery/main.kt`
   - iOS equivalent file
4. Build and run the application on your desired platform

## Building and Running

### Android
- Open the project in Android Studio
- Select the Android configuration
- Click Run

### Desktop
- Open the project in IntelliJ IDEA or Android Studio
- Select the Desktop configuration
- Click Run

### iOS
- Open the project in Android Studio
- Select the iOS configuration
- Click Run (requires a Mac with Xcode installed)

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- [The Movie Database (TMDb)](https://www.themoviedb.org/) for providing the API
- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html) for enabling cross-platform development
- [Jetpack Compose](https://developer.android.com/jetpack/compose) for the modern UI toolkit
- [Jetbrains Junie](https://www.jetbrains.com/junie) for giving access to EAP
