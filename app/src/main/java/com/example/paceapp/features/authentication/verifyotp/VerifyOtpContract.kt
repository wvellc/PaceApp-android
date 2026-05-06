package com.example.paceapp.features.authentication.verifyotp

// Importing interfaces from your untouchable core library!
import com.example.paceapp.core.domain.enums.LoginTypes
import com.example.paceapp.features.authentication.otpsuccess.OtpSuccessContract.Effect
import com.wvelabs.core_ui.base.ViewEvent
import com.wvelabs.core_ui.base.ViewSideEffect
import com.wvelabs.core_ui.base.ViewState

class VerifyOtpContract {

    data class State(
        val isInitialized: Boolean = false,
        val loginType: LoginTypes = LoginTypes.EMAIL,
        val emailPhoneValue: String = "",
        val countryCode: String? = null,
        val isLoading: Boolean = false,
        val otp: String = "",
    ) : ViewState

    sealed class Event : ViewEvent {
        data object Init : Event()
        data object OnResendOtpClicked : Event()
        data object OnNextClick : Event()
        data object OnBackClick : Event()

        data class OnOtpChange(val otp: String) : Event()

    }

    sealed class Effect : ViewSideEffect {
         data object NavigateBack : Effect()
        data object NavigateToTabHost : Effect()
        data object NavigateToBuildProfile : Effect()
    }
}
