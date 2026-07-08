package net.paceapp.features.main.settings

import androidx.lifecycle.viewModelScope
import net.paceapp.R
import net.paceapp.config.AppWebUrls
import net.paceapp.core.auth.AuthManager
import net.paceapp.core.base.BaseViewModel
import net.paceapp.core.data.firestore.UserProfileRepository
import net.paceapp.core.enums.DistanceUnits
import net.paceapp.core.providers.AppResourceProvider
import net.paceapp.features.main.settings.SettingsContract.Effect
import net.paceapp.features.main.settings.SettingsContract.Event
import net.paceapp.features.main.settings.SettingsContract.State
import net.paceapp.features.main.settings.enums.SettingOptions
import net.paceapp.session.AppSessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    val resourceProvider: AppResourceProvider,
    val sessionManager: AppSessionManager,
    private val userProfileRepository: UserProfileRepository,
    private val authManager: AuthManager,
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
            is Event.OnDeleteAccountConfirm -> handleDeleteAccount()
        }
    }


    private fun initData() {
        if (currentState.isInitialized) return
        getDistanceUnits()
        setState { copy(isInitialized = true) }

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

            SettingOptions.LICENSES -> setEffect {
                Effect.NavigateToWebview(
                    url = AppWebUrls.LICENSES,
                    title = resourceProvider.getString(R.string.licenses)
                )
            }

            SettingOptions.DEVELOPED_BY -> setState {
                copy(isDevOptionExpanded = isDevOptionExpanded.not())
            }
        }
    }

    private fun handleDistanceUnitSelected(distanceUnits: DistanceUnits) {
        viewModelScope.launch {
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
                //TODO : CallAPI
                clearSessionData()
            },
            onLoading = { loading ->
                setState { copy(isLoading = loading) }
            },
        )
    }

    private fun handleDeleteAccount() {
        runTask(
            block = {
                //TODO : CallAPI
                clearSessionData()
            },
            onLoading = { loading ->
                setState { copy(isLoading = loading) }
            }
        )
    }

    private suspend fun clearSessionData() = sessionManager.onSessionExpired()
}
