# Piggy

**Piggy** is a modern cross-platform finance app built with [Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform), supporting **Android** and **iOS**.

This project showcases the power of **Kotlin Multiplatform** and cutting-edge technologies such as **Firebase**, delivering a smooth and unified user experience across platforms.

## Features

- Single codebase for Android and iOS.
- Firebase integration (Auth, Firestore, Analytics).
- Modular architecture with separation of platform-specific code (`androidMain`, `iosMain`).
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
│ ├── build.gradle.kts
│ ├── google-services.json # Firebase config
├── iosApp/ # Xcode project for iOS
```


## Getting Started

### Prerequisites

- Kotlin Multiplatform support in IDE (Android Studio or IntelliJ)
- Xcode for iOS builds
- Java 17+

### Run on Android

```bash
./gradlew :composeApp:assembleDebug
```

### Run on iOS
Open the `iosApp` folder in Xcode and run the project.

### Tech Stack
- Kotlin Multiplatform
- Compose Multiplatform
- Firebase
- Gradle Kotlin DSL
- MVVM + Clean Architecture
