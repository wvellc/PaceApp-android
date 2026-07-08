package net.paceapp.features.authentication.login

// Importing interfaces from your untouchable core library!
import android.app.Activity
import androidx.compose.foundation.text.input.TextFieldState
import net.paceapp.core.domain.enums.LoginTypes
import com.wvelabs.core_ui.base.ViewEvent
import com.wvelabs.core_ui.base.ViewSideEffect
import com.wvelabs.core_ui.base.ViewState

class LoginContract {

    data class State(
        val isInitialized: Boolean = false,
        val isLoading: Boolean = false,
        val selectedLoginType: LoginTypes = LoginTypes.EMAIL,
        val countryCode: String = "+1",
        val emailState: TextFieldState = TextFieldState(),
        val phoneState: TextFieldState = TextFieldState(),
        val isSendOTPEnabled: Boolean = false
    ) : ViewState

    sealed class Event : ViewEvent {
        data object Init : Event()
        data object OnBackClicked : Event()

        data class OnLoginTypeSelected(val loginType: LoginTypes) : Event()
        // Carries the Activity — Firebase phone verification requires one.
        data class OnLoginClick(val activity: Activity?) : Event()
        data class ToWebview(val url: String) : Event()
        data class OnCountrySelected(val dialCode: String) : Event()

    }

    sealed class Effect : ViewSideEffect {
        data object NavigateBack : Effect()
        data class NavigateToVerifyOtp(
            val loginType: LoginTypes,
            val emailPhoneValue: String,
            val countryCode: String?
        ) : Effect()

        data class NavigateToWebview(val url: String, val title: String?) : Effect()
        class RequestFocus(val type: LoginTypes) : Effect() {

        }
    }
}
