package net.paceapp.features.main.settings

import android.app.Activity
import android.content.Context
import net.paceapp.core.enums.DistanceUnits
import net.paceapp.features.main.settings.enums.SettingOptions
import com.wvelabs.core_ui.base.ViewEvent
import com.wvelabs.core_ui.base.ViewSideEffect
import com.wvelabs.core_ui.base.ViewState

class SettingsContract {
    data class State(
        val isInitialized: Boolean = false,
        val isLoading: Boolean = false,
        val isDevOptionExpanded: Boolean = false,
        val selectedDistanceUnits: DistanceUnits = DistanceUnits.MILES,
        // Account-deletion re-authentication. `reauthPhase` drives the OTP/email
        // overlay; `isDeleting` shows the blocking processing overlay.
        val reauthPhase: ReauthPhase? = null,
        val isDeleting: Boolean = false,
        // Inline Strava card state (mirrored from StravaManager, sourced from Firestore).
        val isStravaConnected: Boolean = false,
        val stravaAthleteName: String? = null,
        val isStravaWorking: Boolean = false,
    ) : ViewState

    // Which re-auth surface to present before deleting. Phone → enter the OTP sent to
    // the signed-in number; Email → wait for the user to tap the link (deletion runs
    // from MainActivity when the link returns).
    sealed interface ReauthPhase {
        data class PhoneOtp(
            val phone: String,
            val verificationId: String,
            val otp: String = "",
            val isVerifying: Boolean = false,
        ) : ReauthPhase

        data class EmailWait(val email: String) : ReauthPhase
    }

    sealed class Event : ViewEvent {
        data object Init : Event()
        data object OnBackClick : Event()

        data class OnDistanceUnitSelected(val unit: DistanceUnits) : Event()
        data class OnSettingOptionClick(val option: SettingOptions) : Event()
        data object OnDeveloperWebsiteClick : Event()

        data object OnLogoutClick : Event()
        data object OnDeleteAccountClick : Event()
        data object OnLogoutConfirm : Event()
        // Carries the Activity — Firebase phone re-verification requires one.
        data class OnDeleteAccountConfirm(val activity: Activity?) : Event()

        // Re-auth OTP entry (phone flow).
        data class OnReauthOtpChanged(val otp: String) : Event()
        data object OnReauthOtpSubmit : Event()
        data object OnReauthCancel : Event()

        // Inline Strava card actions. Connect needs a Context (Custom Tab / Strava app).
        data class OnStravaConnect(val context: Context) : Event()
        data object OnStravaDisconnect : Event()
        data object OnStravaResync : Event()
    }

    sealed class Effect : ViewSideEffect {


        data object NavigateBack : Effect()
        data class NavigateToDeveloperWebsite(val url: String) : Effect()
        data class NavigateToWebview(val url: String, val title: String?) : Effect()
        data object NavigateToNotifications : Effect()
        data object NavigateToStrava : Effect()
        data object OpenFaq : Effect()
        data object ShowLogoutDialog : Effect()
        data object ShowDeleteAccountDialog : Effect()

    }
}
