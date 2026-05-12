package com.example.paceapp.features.main.settings

import com.example.paceapp.core.domain.enums.DistanceUnits
import com.example.paceapp.features.main.settings.enums.SettingOptions
import com.wvelabs.core_ui.base.ViewEvent
import com.wvelabs.core_ui.base.ViewSideEffect
import com.wvelabs.core_ui.base.ViewState

class SettingsContract {
    data class State(
        val isInitialized: Boolean = false,
        val isLoading: Boolean = false,
        val isDevOptionExpanded: Boolean = false,
        val selectedDistanceUnits: DistanceUnits = DistanceUnits.MILE,
    ) : ViewState

    sealed class Event : ViewEvent {
        data object Init : Event()
        data object OnBackClick : Event()

        data class OnDistanceUnitSelected(val unit: DistanceUnits) : Event()
        data class OnSettingOptionClick(val option: SettingOptions) : Event()
        data object OnDeveloperWebsiteClick : Event()

        data object OnLogoutClick : Event()
        data object OnDeleteAccountClick : Event()
        data object OnLogoutConfirm : Event()
        data object OnDeleteAccountConfirm : Event()


    }

    sealed class Effect : ViewSideEffect {


        data object NavigateBack : Effect()
        data class NavigateToDeveloperWebsite(val url: String) : Effect()
        data class NavigateToWebview(val url: String, val title: String?) : Effect()
        data object NavigateToNotifications : Effect()
        data object ShowLogoutDialog : Effect()
        data object ShowDeleteAccountDialog : Effect()

    }
}
