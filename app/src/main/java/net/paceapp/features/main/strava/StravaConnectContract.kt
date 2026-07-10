package net.paceapp.features.main.strava

import android.content.Context
import com.wvelabs.core_ui.base.ViewEvent
import com.wvelabs.core_ui.base.ViewSideEffect
import com.wvelabs.core_ui.base.ViewState

class StravaConnectContract {
    data class State(
        val isConnected: Boolean = false,
        val athleteName: String? = null,
        // True while an OAuth exchange / disconnect / sync call is in flight.
        val isWorking: Boolean = false,
    ) : ViewState

    sealed class Event : ViewEvent {
        data object Init : Event()
        data object OnBackClick : Event()
        // OAuth authorize needs a Context to launch the Strava app / Custom Tab.
        data class OnConnectClick(val context: Context) : Event()
        data object OnDisconnectClick : Event()
        data object OnSyncRecentClick : Event()
    }

    sealed class Effect : ViewSideEffect {
        data object NavigateBack : Effect()
    }
}
