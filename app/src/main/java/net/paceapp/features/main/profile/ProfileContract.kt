package net.paceapp.features.main.profile

import net.paceapp.core.models.UserUiModel
import net.paceapp.features.main.profile.enums.ProfileOptions
import com.wvelabs.core_ui.base.ViewEvent
import com.wvelabs.core_ui.base.ViewSideEffect
import com.wvelabs.core_ui.base.ViewState

class ProfileContract {
    data class State(
        val isInitialized: Boolean = false,
        val isLoading: Boolean = false,
        val userUiModel: UserUiModel? = null,
        val isIntervalVibrateEnabled: Boolean = false,
        val isIntervalBeepEnabled: Boolean = false
    ) : ViewState

    sealed class Event : ViewEvent {
        data object Init : Event()
        data object OnBackClick : Event()
        data object OnSettingClick : Event()
        data object OnEditProfileClick : Event()
        data class OnToggleSwitch(val option: ProfileOptions, val isEnabled: Boolean) : Event()
        data class OnProfileOptionClick(val option: ProfileOptions) : Event()
    }

    sealed class Effect : ViewSideEffect {
        data object NavigateBack : Effect()
        data object NavigateToSetGait : Effect()

        data object NavigateToManageWatch : Effect()
        data object NavigateToSettings : Effect()
        data object NavigateToEditProfile : Effect()
        data object NavigateToStrava : Effect()
    }
}
