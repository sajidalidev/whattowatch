# What to Watch - Project Guidelines

## Project Overview
"What to Watch" is a Kotlin Multiplatform project that helps users discover movies to watch. The application uses The Movie Database (TMDb) API to fetch information about trending movies, top-rated movies, and allows users to search for specific movies and view their details.

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

## Development Guidelines
1. **API Key**: The application requires a TMDb API key to function. Make sure to initialize the application with a valid API key.
2. **Dependencies**: The project uses Koin for dependency injection and Ktor for API requests.
3. **UI**: The UI is built using Jetpack Compose for all platforms.

## Testing Guidelines
When implementing changes, consider testing the following:
- API integration with TMDb
- UI rendering across different platforms
- Dependency injection setup

## Build Instructions
Before submitting changes, ensure the project builds successfully for all target platforms:
- Android
- iOS
- Desktop

## Code Style
- Follow Kotlin coding conventions
- Use meaningful variable and function names
- Add appropriate documentation for public APIs
- Organize imports properly
