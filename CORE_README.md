# 🚀 PaceApp Universal Core Architecture

This document serves as the "source of truth" for the architectural patterns used in the `core_ui` and `core_network` modules. Use this to maintain consistency across projects.

---

## 🏗️ 1. The Global Alert System
Our app utilizes a decoupled, "Fire and Forget" global alerting system. The `core_ui` module handles queuing, state management, and animations, while the app module provides the specific UI implementation.

### The UI Hierarchy Rule
To ensure alerts (Toasts, Snackbars, Dialogs) float correctly over all screens, follow this exact wrapping order in `MainActivity.kt`:

1.  **App Theme**: Base colors and typography.
2.  **Alert Container**: Catches global events and draws overlays.
3.  **Nav Host**: Manages screen routing.

#### Implementation (MainActivity.kt)
```kotlin
PaceAppTheme {
    val navController = rememberNavController()
    
    // MIDDLE LEVEL: The Core UI Alert Container
    AppAlertContainer {
        // BOTTOM LEVEL: The Navigation Flow
        AppNavHost(
            navController = navController,
            sessionManager = sessionManager 
        )
    }
}
```

### 🎯 How to Trigger an Alert
Call the `AppAlerts` singleton from any feature module (ViewModel, Repository, etc.):

```kotlin
import com.wvelabs.core_ui.alerts.AppAlerts
import com.wvelabs.core_ui.alerts.MessageType

// Triggers instantly over the current screen
AppAlerts.showToast(
    text = "Invalid credentials.",
    type = MessageType.Error
)
```

---

## 🧩 2. MVI Architecture (`core_ui`)
Standardizes screen logic using Unidirectional Data Flow.

- **`ViewState`**: The single source of truth for the UI data.
- **`ViewEvent`**: User actions sent to the ViewModel.
- **`ViewSideEffect`**: One-time events (Navigation, Toasts).
- **`CoreViewModel`**: The base class that manages state updates via `setState { ... }` and provides `safeLaunch` for API calls.

---

## 🌐 3. Networking & Session (`core_network`)
Standardizes communication and error handling.

- **`NetworkResult<T>`**: Generic wrapper for `Success` or `Failure`.
- **`ErrorHandler`**: Maps HTTP/Socket errors to a unified `NetworkError` model.
- **`AuthInterceptor`**: Automatically attaches Bearer tokens and handles `401 Unauthorized` globally via `SessionListener`.
- **`SessionContracts`**: Decoupled interfaces (`SessionCache`, `SessionListener`) implemented by the app module.

---

## ⚡ 4. Automation: `generate_screen.sh`
To reduce development time, use the generation script. It creates:
1.  **Contract**: MVI interface definitions.
2.  **ViewModel**: Inherits from `BaseViewModel`.
3.  **Screen**: Compose entry point.
4.  **Content**: Stateless UI component.
5.  **Navigation**: Type-safe routes.

---

## 🛠️ 5. AI Optimization Guide
When providing this to a Gemini Notebook, ask for improvements in these areas:
1.  **State Preservation**: Inject `SavedStateHandle` into `CoreViewModel`.
2.  **Performance**: Add `@Immutable` or `@Stable` to `ViewState` objects.
3.  **Refined Networking**: Migrate from `safeLaunch` callbacks to `Flow<NetworkResult<T>>` streams for better composability.
