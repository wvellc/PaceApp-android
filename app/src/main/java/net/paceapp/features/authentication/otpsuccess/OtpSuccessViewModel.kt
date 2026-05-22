package net.paceapp.features.authentication.otpsuccess

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import net.paceapp.core.base.BaseViewModel
import net.paceapp.core.enums.AuthDestination
import net.paceapp.core.domain.usecases.AuthRouteManager
import net.paceapp.features.authentication.otpsuccess.OtpSuccessContract.Effect
import net.paceapp.features.authentication.otpsuccess.OtpSuccessContract.Event
import net.paceapp.features.authentication.otpsuccess.OtpSuccessContract.State
import net.paceapp.features.authentication.otpsuccess.navigation.OtpSuccessRoute
import net.paceapp.session.AppSessionManager
import com.wvelabs.core_network.utils.AppLogger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtpSuccessViewModel @Inject constructor(
    private val sessionManager: AppSessionManager,
    private val authRouteManager: AuthRouteManager,
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel<State, Event, Effect>() {

    override fun setInitialState() = State()

    override fun handleEvents(event: Event) {
        when (event) {
            is Event.Init -> initData()
            is Event.OnBackClicked -> {
                setEffect { Effect.NavigateBack }
            }

            is Event.OnContinueClicked -> onContinueClicked()
        }
    }

    private fun initData() {
        if (currentState.isInitialized) return
        val args = savedStateHandle.toRoute<OtpSuccessRoute>()
        //Set argument data
        setState {
            copy(
                loginType = args.loginType,
            )
        }
        setState { copy(isInitialized = true) }
    }


    private fun onContinueClicked() {
        viewModelScope.launch {
            val destination = authRouteManager.getNextDestination()

            // Same logic, but mapped to this specific screen's Contract
            when (destination) {
                AuthDestination.TAB_HOST -> setEffect { Effect.NavigateToTabHost }
                AuthDestination.BUILD_PROFILE -> setEffect { Effect.NavigateToBuildProfile }
                AuthDestination.LOGIN -> {
                    AppLogger.e("Error: Auth token expired")
                    sessionManager.onSessionExpired()
                }
            }
        }
    }
}
