package net.paceapp.core.base

import androidx.lifecycle.viewModelScope
import com.wvelabs.core_network.utils.AppLogger
import com.wvelabs.core_ui.alerts.AppAlerts
import com.wvelabs.core_ui.alerts.MessageType
import com.wvelabs.core_ui.base.CoreViewModel
import com.wvelabs.core_ui.base.ViewEvent
import com.wvelabs.core_ui.base.ViewSideEffect
import com.wvelabs.core_ui.base.ViewState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import net.paceapp.BuildConfig
import net.paceapp.core.utils.ErrorMapper

/**
 * The parent ViewModel for EVERY feature in the app.
 * It provides built-in Navigation and Auto-Retry logic for API calls.
 */
abstract class BaseViewModel<S : ViewState, E : ViewEvent, Ef : ViewSideEffect> :
    CoreViewModel<S, E, Ef>() {
    protected val currentState: S get() = state.value
    protected val isDebugMode: Boolean = BuildConfig.DEBUG


    /**
     * Executes a coroutine task with automatic loading state management,
     * success Toasts, and global Error mapping using AppAlerts.
     */
    protected fun <T> runTask(
        onLoading: (Boolean) -> Unit = {},
        block: suspend () -> T,
        onSuccess: (T) -> Unit = {},
        showSuccessMessage: String? = null, // Optional auto-toast
        onError: ((Exception) -> Unit)? = null // Overridable if needed
    ) {
        viewModelScope.launch {
            onLoading(true)
            try {
                val result = block()

                // Show automatic success toast if a message was provided
                if (showSuccessMessage != null) {
                    AppAlerts.showToast(
                        text = showSuccessMessage,
                        type = MessageType.Success
                    )
                }

                onSuccess(result)
            } catch (e: Exception) {
                AppLogger.e("Task execution failed", e)

                // If the child ViewModel wants to handle the error custom, let it.
                // Otherwise, push it to the Global AppAlerts automatically!
                if (onError != null) {
                    onError(e)
                } else {
                    val message = ErrorMapper.getMessage(e)
                    AppAlerts.showToast(
                        text = message,
                        type = MessageType.Error
                    )
                }
            } finally {
                onLoading(false)
            }
        }
    }

    /**
     * A generic helper to observe any Flow and update the state automatically.
     * Uses distinctUntilChanged() to prevent infinite recomposition loops.
     */
    protected fun <T> observeState(
        flow: Flow<T>,
        updateState: S.(T) -> S
    ) {
        viewModelScope.launch {
            flow.distinctUntilChanged().collectLatest { value ->
                setState { updateState(value) }
            }
        }
    }

    // Quick helper for manual alerts from any ViewModel
    protected fun showToast(text: String, type: MessageType = MessageType.Info) {
        AppAlerts.showToast(text = text, type = type)
    }
}