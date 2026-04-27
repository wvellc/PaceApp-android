package com.example.paceapp.features.authentication.otpsuccess

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.paceapp.core.base.BaseViewModel
import com.example.paceapp.core.data.usecases.AuthRouteManager
import com.example.paceapp.features.authentication.otpsuccess.OtpSuccessContract.Effect
import com.example.paceapp.features.authentication.otpsuccess.OtpSuccessContract.Event
import com.example.paceapp.features.authentication.otpsuccess.OtpSuccessContract.State
import com.example.paceapp.features.authentication.otpsuccess.navigation.OtpSuccessRoute
import com.example.paceapp.session.AppSessionManager
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
        fetchSessionData()
        setState { copy(isInitialized = true,) }
    }

    private fun fetchSessionData() {
        viewModelScope.launch {
            val user = sessionManager.getUserDetails()
            setState { copy() }
        }
    }

    private fun onContinueClicked() {
        viewModelScope.launch {
            val destination = authRouteManager.getNextDestination()

            // Same logic, but mapped to this specific screen's Contract
//            when (destination) {
//                AuthDestination.DASHBOARD -> setEffect { Effect.GoToDashboard }
//                AuthDestination.BUILD_PROFILE -> setEffect { Effect.GoToProfileSetup }
//                AuthDestination.LOGIN -> {
//                    AppLogger.e("Error")
//                }
//            }
        }
    }
}
