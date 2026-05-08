# TreeTask - Android Task Management App

A modern, offline-first Android Task Management application built using **Clean Architecture**, **MVI Pattern**, and heavily inspired by Google's official **Now in Android** (NiA) architecture.

## 🏗 Architecture & Tech Stack

This project follows a highly modularized architecture to ensure scalability, robust testing, and separation of concerns.

* **Architecture**: Clean Architecture, Multi-module, MVI (Model-View-Intent)
* **Language**: Kotlin 2.0+
* **UI**: Jetpack Compose (Material 3)
* **Dependency Injection**: Hilt
* **Asynchronous Programming**: Kotlin Coroutines & Flow
* **Networking**: Retrofit2, OkHttp, Kotlinx Serialization, Socket.IO (planned)
* **Local Storage**: Room Database (Offline-first data layer), Preferences DataStore
* **Testing**: JUnit4, MockK, Turbine, Truth, Coroutines-Test
* **Observability**: Firebase Analytics, Firebase Crashlytics, Firebase Performance, LeakCanary
* **CI/CD**: GitHub Actions (Lint, Detekt, Automated Unit Tests)
* **Localization**: Multi-language support (English & Vietnamese)
* **Security & Optimization**: R8/ProGuard obfuscation, EncryptedSharedPreferences (planned)

## 📦 Key Features (Ready)

* **Offline-first Sync**: Uses `RemoteMediator` for seamless data synchronization between Room and REST API.
* **Real-time Network Monitor**: Dynamic UI feedback (Offline Banner) when connectivity changes.
* **Robust Authentication**: 
    * Support for Login/Register with instant profile sync.
    * Automatic Token Refresh mechanism via OkHttp Interceptors.
    * **Global Session Management**: Automatic forced logout and backstack clearing when sessions expire.
* **Localization (i18n)**: Fully translated for EN and VI locales.
* **Advanced Analytics**: Custom event tracking (`task_created`, `sync_failed`, `login_method`) and non-fatal error reporting via Crashlytics.
* **Clean Code & Quality**: 
    * Strict Linting rules (Detekt, Spotless).
    * High Unit Test coverage for UseCases and Repositories.

## 🚀 Setup Instructions

To build and run this project on your local machine:

1. **Clone the repository**:
   ```bash
   git clone https://github.com/doannd3/TreeTask.git
   ```

2. **Open the project**:
   Open the cloned directory in **Android Studio Ladybug** (or a newer version).

3. **Configure Firebase**:
   Place your `google-services.json` file into the `app/` directory to enable Analytics, Performance, and Crashlytics.

4. **Build and Run**:
   Sync the Gradle project and click the Run button.

## 🔮 Future Roadmap

The foundational core is fully established. Upcoming updates will focus on:

* **UI/UX Polishing**: Implementing "Premium" design elements like glassmorphism and smooth animations in the Profile and Tasks screens.
* **Task Management Features**: Adding, editing, and deleting tasks with complex scheduling.
* **Settings Module**: Dark Mode toggle, language switcher, and notification preferences.
* **Real-time Chat**: Wiring up Socket.IO streams to the Compose UI layer.

---
*Note: This README is continuously updated as new features are deployed.*
