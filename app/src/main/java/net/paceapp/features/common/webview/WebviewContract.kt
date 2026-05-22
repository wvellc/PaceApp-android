package net.paceapp.features.common.webview

// Importing interfaces from your untouchable core library!
import com.wvelabs.core_ui.base.ViewEvent
import com.wvelabs.core_ui.base.ViewSideEffect
import com.wvelabs.core_ui.base.ViewState

class WebviewContract {

    data class State(
        val url: String = "",
        val title: String? = null,
        val isZoomEnabled: Boolean = false,
        val isLoading: Boolean = false
    ) : ViewState

    sealed class Event : ViewEvent {
        data object OnPageStarted : Event()
        data object OnPageFinished : Event()
    }

    sealed class Effect : ViewSideEffect {
        // data object NavigateBack : Effect()
    }
}
