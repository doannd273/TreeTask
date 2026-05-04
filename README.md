# TreeTask - Android Task Management App

A modern, offline-first Android Task Management application built using **Clean Architecture**, **MVI Pattern**, and heavily inspired by Google's official **Now in Android** (NiA) architecture.

## 🏗 Architecture & Tech Stack

This project follows a highly modularized architecture to ensure scalability, robust testing, and separation of concerns.

* **Architecture**: Clean Architecture, Multi-module, MVI (Model-View-Intent)
* **Language**: Kotlin 2.0+
* **UI**: Jetpack Compose (Adaptive layouts planned for future updates)
* **Dependency Injection**: Hilt
* **Asynchronous Programming**: Kotlin Coroutines & Flow
* **Networking**: Retrofit2, OkHttp, Chucker, Socket.IO (for Real-time Chat)
* **Local Storage**: Room Database (Offline-first data layer), Preferences DataStore
* **Testing**: JUnit4, MockK, Turbine, Truth, Coroutines-Test
* **Observability**: Firebase Analytics, Firebase Crashlytics, Firebase Performance, LeakCanary
* **CI/CD**: GitHub Actions (Lint, Detekt, Automated Unit Tests)

## 📦 Project Structure

The codebase is split into `app`, `feature`, and `core` modules:

* **`app`**: The main entry point, navigation graph, and global DI bindings.
* **`feature:*`**: Encapsulated feature modules (Auth, Tasks, Profile, Chat, Stats). *(Note: UI/UX implementation is actively in progress).*
* **`core:common`**: Shared utilities, DI qualifiers, and injected Coroutine Dispatchers.
* **`core:data`**: Repository implementations handling business logic, data merging, and offline-first fallback mechanisms.
* **`core:database`**: Room DB setup, DAOs, and local entity definitions.
* **`core:datastore`**: Local preferences (e.g., Auth Tokens) managed securely by DataStore.
* **`core:domain`**: Core business rules and repository interfaces.
* **`core:model`**: Immutable business models used across the application.
* **`core:network`**: API services, Kotlinx Serialization, and robust Auth Interceptors for automatic token refresh.
* **`core:testing`**: Test dispatchers, mock rules, and shared testing utilities.
* **`core:analytics`**: Firebase Integration for event tracking and crash reporting.

## 🚀 Setup Instructions

To build and run this project on your local machine:

1. **Clone the repository**:
   ```bash
   git clone https://github.com/doannd3/TreeTask.git
   ```

2. **Open the project**:
   Open the cloned directory in **Android Studio Ladybug** (or a newer version).

3. **Configure Firebase (Optional but recommended)**:
   Place your `google-services.json` file into the `app/` directory to enable Firebase Analytics, Performance, and Crashlytics.

4. **Build and Run**:
   Sync the Gradle project and click the Run button to install it on an emulator or physical device.

## 🔮 Future Roadmap

The foundational core (Networking, Database, DI, CI/CD, and Unit Testing) is fully established. Upcoming updates will focus on:

* **UI/UX Implementation**: Building beautiful, adaptive UI layouts using Jetpack Compose (supporting mobile phones, foldables, and tablets).
* **Background Syncing**: Integrating `WorkManager` for reliable, background offline-to-online data synchronization.
* **Real-time Chat**: Wiring up Socket.IO streams to the Compose UI layer.

---
*Note: This README serves as the foundational documentation and will be continuously updated as new visual features are deployed.*
