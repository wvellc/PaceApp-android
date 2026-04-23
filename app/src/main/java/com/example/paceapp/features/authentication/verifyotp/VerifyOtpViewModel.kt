package com.example.paceapp.features.authentication.verifyotp

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

// App-specific base classes and managers
import com.example.paceapp.core.base.BaseViewModel
import com.example.paceapp.session.AppSessionManager

// Screen imports
import com.example.paceapp.features.authentication.verifyotp.VerifyOtpContract.Effect
import com.example.paceapp.features.authentication.verifyotp.VerifyOtpContract.Event
import com.example.paceapp.features.authentication.verifyotp.VerifyOtpContract.State

@HiltViewModel
class VerifyOtpViewModel @Inject constructor(
    private val sessionManager: AppSessionManager
) : BaseViewModel<State, Event, Effect>() {

    override fun setInitialState() = State()

    override fun handleEvents(event: Event) {
        when (event) {
            is Event.Init -> initData()
        }
    }

    private fun initData() {
        if (state.value.isInitialized) return

        // TODO: Initialization logic here

        setState { copy(isInitialized = true) }
    }
}
