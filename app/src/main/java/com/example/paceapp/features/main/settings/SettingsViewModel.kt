package com.example.paceapp.features.main.settings

import com.example.paceapp.R
import com.example.paceapp.config.AppWebUrls
import com.example.paceapp.core.base.BaseViewModel
import com.example.paceapp.core.domain.enums.DistanceUnits
import com.example.paceapp.core.providers.AppResourceProvider
import com.example.paceapp.features.main.settings.SettingsContract.Effect
import com.example.paceapp.features.main.settings.SettingsContract.Event
import com.example.paceapp.features.main.settings.SettingsContract.State
import com.example.paceapp.features.main.settings.domain.SettingOptions
import com.example.paceapp.session.AppSessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
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
        setState { copy(isInitialized = true) }
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
        setState { copy(selectedDistanceUnits = distanceUnits) }
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
        safeLaunch(
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
        safeLaunch(
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
