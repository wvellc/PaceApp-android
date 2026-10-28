package net.paceapp.features.main.settings

import android.app.Activity
import androidx.lifecycle.viewModelScope
import net.paceapp.R
import net.paceapp.config.AppWebUrls
import net.paceapp.core.auth.AuthManager
import net.paceapp.core.auth.AuthErrorMapper
import net.paceapp.core.auth.AuthProviderKind
import net.paceapp.core.base.BaseViewModel
import net.paceapp.core.data.firestore.UserProfileRepository
import net.paceapp.core.enums.DistanceUnits
import net.paceapp.core.providers.AppResourceProvider
import net.paceapp.core.strava.StravaManager
import net.paceapp.features.main.settings.SettingsContract.Effect
import net.paceapp.features.main.settings.SettingsContract.Event
import net.paceapp.features.main.settings.SettingsContract.ReauthPhase
import net.paceapp.features.main.settings.SettingsContract.State
import net.paceapp.features.main.settings.enums.SettingOptions
import net.paceapp.session.AppSessionManager
import com.wvelabs.core_ui.alerts.AppAlerts
import com.wvelabs.core_ui.alerts.MessageType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    val resourceProvider: AppResourceProvider,
    val sessionManager: AppSessionManager,
    private val userProfileRepository: UserProfileRepository,
    private val authManager: AuthManager,
    private val stravaManager: StravaManager,
) : BaseViewModel<State, Event, Effect>() {

    override fun setInitialState() = State()

    override fun handleEvents(event: Event) {
        when (event) {
            is Event.Init -> initData()
            is Event.OnBackClick -> {
                setEffect { Effect.NavigateBack }
            }

            is Event.OnSettingOptionClick -> handleSettingOptionClick(event.option)
            is Event.OnDistanceUnitSelected -> handleDistanceUnitSelected(event.unit)
            is Event.OnDeveloperWebsiteClick -> handleDeveloperWebsiteClick()
            is Event.OnLogoutClick -> handleOnLogoutClick()
            is Event.OnDeleteAccountClick -> handleOnDeleteAccountClick()
            is Event.OnLogoutConfirm -> handleLogout()
            is Event.OnDeleteAccountConfirm -> beginReauthentication(event.activity)
            is Event.OnReauthOtpChanged -> handleReauthOtpChanged(event.otp)
            is Event.OnReauthOtpSubmit -> handleReauthOtpSubmit()
            is Event.OnReauthCancel -> handleReauthCancel()
            is Event.OnStravaConnect -> stravaManager.connect(event.context)
            is Event.OnStravaDisconnect -> stravaManager.disconnect()
            is Event.OnStravaResync -> stravaManager.syncRecent()
        }
    }


    private fun initData() {
        if (currentState.isInitialized) return
        getDistanceUnits()
        observeStravaState()
        setState { copy(isInitialized = true) }

    }

    // Mirror StravaManager's connection state (users/{uid}.strava) into the inline
    // Settings card. startObserving() is idempotent. Mirrors iOS SettingScreen.onAppear.
    private fun observeStravaState() {
        stravaManager.startObserving()
        stravaManager.state
            .onEach { s ->
                setState {
                    copy(
                        isStravaConnected = s.isConnected,
                        stravaAthleteName = s.athleteName,
                        isStravaWorking = s.isWorking,
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun getDistanceUnits() {
        viewModelScope.launch {
            // Prefer the Firestore user doc (source of truth, parity with iOS).
            val uid = authManager.currentUid
            val remoteUnit = uid
                ?.let { userProfileRepository.observeUser(it).firstOrNull()?.distanceUnit }
                ?.toDistanceUnits()
            val units = remoteUnit
                ?: sessionManager.getUserDetails()?.distanceUnits
                ?: DistanceUnits.MILES
            setState { copy(selectedDistanceUnits = units) }
        }
    }

    private fun handleSettingOptionClick(option: SettingOptions) {
        when (option) {
            SettingOptions.STRAVA -> setEffect {
                Effect.NavigateToStrava
            }

            SettingOptions.NOTIFICATIONS -> setEffect {
                Effect.NavigateToNotifications
            }

            SettingOptions.PRIVACY_POLICY -> setEffect {
                Effect.NavigateToWebview(
                    url = AppWebUrls.PRIVACY_POLICY,
                    title = resourceProvider.getString(R.string.privacy_policy)
                )
            }

            SettingOptions.TERMS_SERVICE -> setEffect {
                Effect.NavigateToWebview(
                    url = AppWebUrls.TERM_OF_SERVICE,
                    title = resourceProvider.getString(R.string.terms_of_service)
                )
            }

//            SettingOptions.LICENSES -> setEffect {
//                Effect.NavigateToWebview(
//                    url = AppWebUrls.LICENSES,
//                    title = resourceProvider.getString(R.string.licenses)
//                )
//            }

            SettingOptions.FAQ -> setEffect { Effect.OpenFaq }

            SettingOptions.DEVELOPED_BY -> setState {
                copy(isDevOptionExpanded = isDevOptionExpanded.not())
            }
        }
    }

    private fun handleDistanceUnitSelected(distanceUnits: DistanceUnits) {
        viewModelScope.launch {
            // Confirm the account still exists — a deleted/disabled account is signed out
            // instead of writing a setting to a dead session.
            if (!authManager.verifyAccountStillValid()) return@launch
            //Save distance units — keep local session in sync (existing behaviour).
            val userDetails = sessionManager.getUserDetails()
            sessionManager.setUserDetails(
                userDetails?.copy(
                    distanceUnits = distanceUnits
                )
            )
            setState { copy(selectedDistanceUnits = distanceUnits) }

            // Persist to the Firestore user doc as the full word ("Miles"/"Kilometers").
            val uid = authManager.currentUid
            if (uid != null) {
                runCatching {
                    userProfileRepository.updateDistanceUnit(uid, distanceUnits.firestoreName())
                }
            }
        }

    }

    // DistanceUnits → Firestore/iOS full word.
    private fun DistanceUnits.firestoreName(): String =
        if (this == DistanceUnits.MILES) "Miles" else "Kilometers"

    // Firestore full word → DistanceUnits.
    private fun String.toDistanceUnits(): DistanceUnits =
        if (equals("Miles", ignoreCase = true)) DistanceUnits.MILES else DistanceUnits.KMS

    private fun handleDeveloperWebsiteClick() {
        setEffect { Effect.NavigateToDeveloperWebsite(AppWebUrls.DEVELOPER_WEBSITE) }
    }

    private fun handleOnLogoutClick() {
        setEffect { Effect.ShowLogoutDialog }
    }

    private fun handleOnDeleteAccountClick() {
        setEffect { Effect.ShowDeleteAccountDialog }
    }

    private fun handleLogout() {
        runTask(
            block = {
                // Sign out of Firebase too — clearing only the local session leaves
                // auth.currentUser set, so a later phone-OTP login would latch onto the
                // stale account on the Verify screen (skipping the OTP). Then emit the
                // session-expired event so AppNavHost routes to Login.
                authManager.signOut()
                sessionManager.onSessionExpired()
            },
            onLoading = { loading ->
                setState { copy(isLoading = loading) }
            },
        )
    }

    // Confirming the delete dialog verifies it's really the user before deleting — OTP
    // for phone, email link for email — no sign-out. Unknown provider deletes directly.
    // Mirrors iOS SettingScreen.beginReauthentication.
    private fun beginReauthentication(activity: Activity?) {
        when (val kind = authManager.authProviderKind) {
            is AuthProviderKind.Phone -> {
                if (activity == null) {
                    AppAlerts.showToast("Couldn't start verification. Please try again.", type = MessageType.Error)
                    return
                }
                setState { copy(isDeleting = true) }
                authManager.sendReauthOtp(
                    activity = activity,
                    onCodeSent = { verificationId ->
                        setState {
                            copy(
                                isDeleting = false,
                                reauthPhase = ReauthPhase.PhoneOtp(phone = kind.number, verificationId = verificationId),
                            )
                        }
                    },
                    onError = { message ->
                        setState { copy(isDeleting = false) }
                        AppAlerts.showToast(message, type = MessageType.Error)
                    },
                    // Auto-retrieval already reauthenticated — go straight to deletion.
                    onDone = { deleteAccountAndRouteToLogin() },
                )
            }

            is AuthProviderKind.Email -> {
                setState { copy(isDeleting = true) }
                viewModelScope.launch {
                    authManager.sendReauthEmailLink()
                        .onSuccess {
                            setState {
                                copy(isDeleting = false, reauthPhase = ReauthPhase.EmailWait(email = kind.email))
                            }
                        }
                        .onFailure {
                            setState { copy(isDeleting = false) }
                            AppAlerts.showToast(AuthErrorMapper.message(it), type = MessageType.Error)
                        }
                }
            }

            AuthProviderKind.Unknown -> deleteAccountAndRouteToLogin()
        }
    }

    private fun handleReauthOtpChanged(otp: String) {
        val phase = currentState.reauthPhase as? ReauthPhase.PhoneOtp ?: return
        setState { copy(reauthPhase = phase.copy(otp = otp)) }
    }

    // Verify & Delete: reauthenticate with the entered OTP, then delete the account.
    private fun handleReauthOtpSubmit() {
        val phase = currentState.reauthPhase as? ReauthPhase.PhoneOtp ?: return
        if (phase.otp.length < 6 || phase.isVerifying) return
        setState { copy(reauthPhase = phase.copy(isVerifying = true)) }
        viewModelScope.launch {
            authManager.reauthenticateWithPhone(phase.verificationId, phase.otp)
                .onSuccess {
                    // Drop the OTP sheet, show the blocking overlay, then delete.
                    setState { copy(reauthPhase = null) }
                    deleteAccountAndRouteToLogin()
                }
                .onFailure {
                    setState { copy(reauthPhase = phase.copy(otp = "", isVerifying = false)) }
                    AppAlerts.showToast(AuthErrorMapper.message(it), type = MessageType.Error)
                }
        }
    }

    private fun handleReauthCancel() {
        // Abandon an in-flight email reauth so a later link isn't treated as deletion.
        authManager.isReauthenticatingForDeletion = false
        setState { copy(reauthPhase = null, isDeleting = false) }
    }

    // Runs the actual deletion behind a blocking overlay, then routes to login by
    // reusing the global session-expired path (AppNavHost → toLogin).
    private fun deleteAccountAndRouteToLogin() {
        setState { copy(isDeleting = true) }
        viewModelScope.launch {
            runCatching { authManager.deleteAccount() }
                .onSuccess { sessionManager.onSessionExpired() }
                .onFailure {
                    setState { copy(isDeleting = false) }
                    AppAlerts.showToast(AuthErrorMapper.message(it), type = MessageType.Error)
                }
        }
    }
}
