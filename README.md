# GlideBrowser

An Android TV web browser application built with Jetpack Compose and modern Android development practices.

## Overview

GlideBrowser is a native Android TV browser that provides a seamless browsing experience optimized for TV screens and remote control navigation. Built with Kotlin and Jetpack Compose, it offers features like tab management, bookmarks, and browsing history.

## Features

- **WebView Integration**: Full-featured web browsing experience
- **Tab Management**: Support for multiple browsing tabs
- **Bookmarks**: Save and manage favorite websites
- **Browsing History**: Track visited pages
- **TV-Optimized UI**: Interface designed for Android TV with remote control support
- **Local Data Persistence**: Room database for offline data storage

## Tech Stack

### Core Technologies
- **Kotlin**: Primary programming language
- **Jetpack Compose**: Modern declarative UI framework
- **Android TV**: Optimized for TV platform (Material 3 for TV)

### Architecture & Dependencies
- **Hilt**: Dependency injection
- **Room**: Local database for bookmarks, history, and tabs
- **Navigation Component**: Compose navigation
- **Coroutines**: Asynchronous programming
- **DataStore**: Preferences storage
- **Accompanist WebView**: WebView integration for Compose

### Build Configuration
- **Min SDK**: 23 (Android 6.0)
- **Target SDK**: 36
- **Compile SDK**: 36
- **JVM Target**: Java 11

## Project Structure

```
app/
├── src/main/java/com/inflexionco/glidebrowser/
│   ├── data/
│   │   └── local/
│   │       ├── dao/           # Data Access Objects
│   │       ├── database/      # Room database configuration
│   │       └── entity/        # Database entities
│   ├── di/                    # Dependency injection modules
│   ├── domain/
│   │   └── model/            # Domain models
│   ├── presentation/
│   │   ├── browser/          # Browser screen and components
│   │   ├── home/             # Home screen
│   │   └── navigation/       # Navigation graph and routes
│   └── ui/
│       └── theme/            # App theming
```

## Getting Started

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 11 or higher
- Android SDK with API level 36

### Building the Project

1. Clone the repository:
```bash
git clone <repository-url>
cd GlideBrowser
```

2. Open the project in Android Studio

3. Sync Gradle dependencies

4. Build and run on an Android TV device or emulator:
```bash
./gradlew assembleDebug
```

### Running on Android TV

This app is designed specifically for Android TV. To test:
- Use an Android TV emulator (API level 23+)
- Deploy to a physical Android TV device
- Use a device with Google TV

## Development

### Build Variants
- **Debug**: Development build with debugging enabled
- **Release**: Production build with optimizations

### Key Components

#### Data Layer
- **TabDao**: Manages browser tabs
- **BookmarkDao**: Handles bookmark operations
- **HistoryDao**: Tracks browsing history

#### Presentation Layer
- **BrowserScreen**: Main browsing interface
- **HomeScreen**: Landing page with quick access
- **GlideWebView**: Custom WebView component

## License

[Add your license here]

## Contributing

[Add contribution guidelines here]

## Contact

[Add contact information here]
