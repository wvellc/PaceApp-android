package com.example.paceapp.features.authentication.verifyotp

// App-specific base classes and managers

// Screen imports
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.example.paceapp.core.base.BaseViewModel
import com.example.paceapp.features.authentication.verifyotp.VerifyOtpContract.Effect
import com.example.paceapp.features.authentication.verifyotp.VerifyOtpContract.Event
import com.example.paceapp.features.authentication.verifyotp.VerifyOtpContract.State
import com.example.paceapp.features.authentication.verifyotp.navigation.VerifyOtpRoute
import com.example.paceapp.session.AppSessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VerifyOtpViewModel @Inject constructor(
    private val sessionManager: AppSessionManager,
    val savedStateHandle: SavedStateHandle,
) : BaseViewModel<State, Event, Effect>() {

    override fun setInitialState() = State(

    )

    override fun handleEvents(event: Event) {

        when (event) {
            is Event.Init -> initData()
        }
    }

    private fun initData() {
        if (currentState.isInitialized) return
        val args = savedStateHandle.toRoute<VerifyOtpRoute>()

        setState {
            copy(
                isInitialized = true, loginType = args.loginType,
                emailPhoneValue = args.emailPhoneValue,
                countryCode = args.countryCode
            )
        }
    }
}
