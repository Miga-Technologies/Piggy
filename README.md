# Piggy

**Piggy** is a modern cross-platform finance app built with [Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform), supporting **Android**, **iOS**, and **Desktop**.

This project showcases the power of **Kotlin Multiplatform** and cutting-edge technologies such as **Firebase**, delivering a smooth and unified user experience across platforms.

## Features

- Single codebase for Android, iOS, and Desktop.
- Firebase integration (Auth, Firestore, Analytics).
- Modular architecture with separation of platform-specific code (`androidMain`, `iosMain`, `desktopMain`).
- Built with Compose Multiplatform and Jetpack Compose principles.
- Scalable structure for adding more features and platforms (like web).

## Project Structure

```
Piggy/
├── composeApp/
│ ├── src/
│ │ ├── commonMain # Shared logic/UI
│ │ ├── androidMain # Android-specific code
│ │ ├── iosMain # iOS-specific code
│ │ ├── desktopMain # Desktop-specific code
│ ├── build.gradle.kts
│ ├── google-services.json # Firebase config
├── iosApp/ # Xcode project for iOS
```


## Getting Started

### Prerequisites

- Kotlin Multiplatform support in IDE (Android Studio or IntelliJ)
- Xcode for iOS builds
- Java 17+
- Node.js and Python (for backend integration)

### Run on Android

```bash
./gradlew :composeApp:assembleDebug
```

### Run on iOS
Open the `iosApp` folder in Xcode and run the project.

### Run on Desktop
```bash
./gradlew :composeApp:run
```

### Backend Integration
Piggy relies on a custom backend (Piggy Integration) to bridge Firebase services with desktop platforms. Learn more: [Piggy Integration GitHub](https://github.com/Miga-Technologies/PiggyIntegration)

### Tech Stack
- Kotlin Multiplatform
- Compose Multiplatform
- Firebase
- Gradle Kotlin DSL
- MVVM + Clean Architecture
