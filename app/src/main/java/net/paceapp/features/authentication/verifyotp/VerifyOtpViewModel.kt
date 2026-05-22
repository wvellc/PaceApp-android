package net.paceapp.features.authentication.verifyotp

// App-specific base classes and managers

// Screen imports
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import net.paceapp.core.base.BaseViewModel
import net.paceapp.core.domain.enums.LoginTypes
import net.paceapp.core.domain.models.UserData
import net.paceapp.core.domain.usecases.AuthRouteManager
import net.paceapp.core.enums.AuthDestination
import net.paceapp.core.utils.AppConstants
import net.paceapp.features.authentication.verifyotp.VerifyOtpContract.Effect
import net.paceapp.features.authentication.verifyotp.VerifyOtpContract.Event
import net.paceapp.features.authentication.verifyotp.VerifyOtpContract.State
import net.paceapp.features.authentication.verifyotp.navigation.VerifyOtpRoute
import net.paceapp.session.AppSessionManager
import com.wvelabs.core_network.timer.TimerFactory
import com.wvelabs.core_network.utils.AppLogger
import com.wvelabs.core_ui.alerts.AppAlerts
import com.wvelabs.core_ui.alerts.MessageType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.minutes


@HiltViewModel
class VerifyOtpViewModel @Inject constructor(
    private val sessionManager: AppSessionManager,
    val savedStateHandle: SavedStateHandle,
    private val authRouteManager: AuthRouteManager,
    timerFactory: TimerFactory,
) : BaseViewModel<State, Event, Effect>() {
    private val otpTimer = timerFactory.create(viewModelScope)
    val countDown = otpTimer.time

    override fun setInitialState() = State()

    override fun handleEvents(event: Event) {
        when (event) {
            is Event.Init -> initData()
            is Event.OnBackClick -> {
                setEffect { Effect.NavigateBack }
            }

            is Event.OnResendOtpClicked -> handleResendOtpClick()
            is Event.OnOtpChange -> updateOtp(event.otp)
            is Event.OnVerifyOTP -> verifyOTP()
        }
    }


    private fun initData() {
        if (currentState.isInitialized) return
        val args = savedStateHandle.toRoute<VerifyOtpRoute>()
        //Set argument data
        setState {
            copy(
                loginType = args.loginType,
                emailPhoneValue = args.emailPhoneValue,
                countryCode = args.countryCode
            )
        }
        //Init otp timer
        otpTimer.start(duration = 1.minutes, isCountdown = true)
        showSuccessToast()
        setState { copy(isInitialized = true) }
    }

    private fun updateOtp(otp: String) {
        setState { copy(otp = otp) }

    }

    private fun handleResendOtpClick() {
        showSuccessToast()
        //TODO:Add Resend OTP API call
        otpTimer.restart()
    }

    private fun showSuccessToast() {
        viewModelScope.launch {
            //dummy delay
            delay(1000)
            AppAlerts.showToast(
                "OTP has been sent to ${currentState.emailPhoneValue}",
                type = MessageType.Info,
            )
        }

    }

    private fun verifyOTP() {
        //Safety check
        if (currentState.otp.length < AppConstants.OTP_LENGTH) return
        //TODO:Call verify OTP API
        val loginType = currentState.loginType
        //Replace with data from API
        val userData = UserData(
            loginType = loginType,
            email = when (loginType) {
                LoginTypes.EMAIL -> currentState.emailPhoneValue
                else -> null
            },
            countryCode = currentState.countryCode,
            phoneNumber = when (loginType) {
                LoginTypes.PHONE -> currentState.emailPhoneValue
                else -> null
            },
        )

        safeLaunch(
            block = {
                sessionManager.saveToken(AppConstants.DUMMY_TOKEN)
                sessionManager.setUserDetails(userData)
                authRouteManager.getNextDestination()
            },
            onLoading = { loading -> setState { copy(isLoading = loading) } },
            onSuccess = { destination -> navigateToNextScreen(destination) },
            onError = { AppLogger.e("VerifyOTPError: ${it.message}") },
        )
    }

    //Navigate to next destinations
    private fun navigateToNextScreen(destination: AuthDestination) {
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
