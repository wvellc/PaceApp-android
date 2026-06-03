package net.paceapp.features.main.settings

import androidx.lifecycle.viewModelScope
import net.paceapp.R
import net.paceapp.config.AppWebUrls
import net.paceapp.core.base.BaseViewModel
import net.paceapp.core.enums.DistanceUnits
import net.paceapp.core.providers.AppResourceProvider
import net.paceapp.features.main.settings.SettingsContract.Effect
import net.paceapp.features.main.settings.SettingsContract.Event
import net.paceapp.features.main.settings.SettingsContract.State
import net.paceapp.features.main.settings.enums.SettingOptions
import net.paceapp.session.AppSessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    val resourceProvider: AppResourceProvider,
    val sessionManager: AppSessionManager,
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
            val userDetails = sessionManager.getUserDetails()
            val units = userDetails?.distanceUnits ?: DistanceUnits.MILES
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
            //Save distance units
            val userDetails = sessionManager.getUserDetails()
            sessionManager.setUserDetails(
                userDetails?.copy(
                    distanceUnits = distanceUnits
                )
            )
            setState { copy(selectedDistanceUnits = distanceUnits) }

        }

    }

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
