package com.example.paceapp.core.base

import androidx.lifecycle.viewModelScope
import com.wvelabs.core_network.model.ErrorType
import com.wvelabs.core_network.model.NetworkError
import com.wvelabs.core_network.model.NetworkResult
import com.wvelabs.core_ui.base.CoreViewModel
import com.wvelabs.core_ui.base.ViewEvent
import com.wvelabs.core_ui.base.ViewSideEffect
import com.wvelabs.core_ui.base.ViewState
import com.wvelabs.core_ui.navigation.NavManager
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * The parent ViewModel for EVERY feature in the app.
 * It provides built-in Navigation and Auto-Retry logic for API calls.
 */
abstract class BaseViewModel<S : ViewState, E : ViewEvent, Ef : ViewSideEffect> :
    CoreViewModel<S, E, Ef>() {

    @Inject
    lateinit var navManager: NavManager

    /**
     * Executes an API call safely. 
     * Automatically routes to the "No Internet" screen if the connection fails,
     * while passing standard HTTP errors back to the specific feature ViewModel.
     */
    protected fun <T> safeAppApiCall(
        apiCall: suspend () -> NetworkResult<T>,
        onSuccess: (T) -> Unit,
        onError: ((NetworkError) -> Unit)? = null
    ) {
        viewModelScope.launch {
            when (val result = apiCall()) {
                is NetworkResult.Success -> {
                    onSuccess(result.data)
                }
                is NetworkResult.Failure -> {
                    if (result.error.errorType == ErrorType.NO_INTERNET) {
                        // Global interception: Route to offline screen
                        navManager.navigate("no_internet_route") 
                    } else {
                        // Pass API errors (401, 404, 500) back to the caller
                        onError?.invoke(result.error)
                    }
                }
            }
        }
    }
}