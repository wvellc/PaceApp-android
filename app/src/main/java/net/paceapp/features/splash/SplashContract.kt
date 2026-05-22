package net.paceapp.features.splash

// Importing interfaces from your untouchable core library!
import com.wvelabs.core_ui.base.ViewEvent
import com.wvelabs.core_ui.base.ViewSideEffect
import com.wvelabs.core_ui.base.ViewState

class SplashContract {

    data class State(
        val isInitialized: Boolean = false,
        val isLoading: Boolean = false,
        val showGetStarted: Boolean = false,
    ) : ViewState

    sealed class Event : ViewEvent {
        data object Init : Event()
        data object OnGetStarted : Event()
    }

    sealed class Effect : ViewSideEffect {
        data object NavigateToLogin : Effect()
        data object NavigateToTabHost : Effect()
        data object NavigateToBuildProfile : Effect()
    }
}
