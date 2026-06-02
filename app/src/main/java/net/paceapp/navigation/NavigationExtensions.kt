package net.paceapp.navigation

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch




/**
 * Extension on SavedStateHandle to observe a navigation result exactly once.
 * Automatically consumes the result so it does not trigger again.
 *
 * @param key The Navigation Result Key
 * @param scope The CoroutineScope (usually viewModelScope) to launch the observer in
 * @param onResult The callback to execute when the data is received
 */
fun <T> SavedStateHandle.observeNavResult(
    key: String,
    scope: CoroutineScope,
    onResult: (T) -> Unit
) {
    scope.launch {
        getStateFlow<T?>(key, null).collect { result ->
            if (result != null) {
                // 1. Trigger the callback with the result
                onResult(result)

                // 2. Consume/clear the result immediately
                remove<T>(key)
            }
        }
    }
}