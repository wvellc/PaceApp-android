package net.paceapp.features.common.webview

// App-specific base classes and managers

// Screen imports
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import net.paceapp.core.base.BaseViewModel
import net.paceapp.features.common.webview.WebviewContract.Effect
import net.paceapp.features.common.webview.WebviewContract.Event
import net.paceapp.features.common.webview.WebviewContract.State
import net.paceapp.features.common.webview.navigation.WebviewRoute
import com.wvelabs.core_network.utils.AppLogger
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WebviewViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : BaseViewModel<State, Event, Effect>() {
    private val args = savedStateHandle.toRoute<WebviewRoute>()

    override fun setInitialState() = State()

    init {
        setState {
            copy(
                url = args.url,
                title = args.title,
                isZoomEnabled = args.isZoomEnabled,
                isLoading = true,
            )
        }
    }

    override fun handleEvents(event: Event) {
        when (event) {
            is Event.OnPageStarted -> setState { copy(isLoading = true) }
            is Event.OnPageFinished -> setState { copy(isLoading = false) }
        }
    }
}
