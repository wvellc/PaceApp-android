package com.example.paceapp.features.main.editprofile

import androidx.compose.foundation.text.input.TextFieldState
import com.wvelabs.core_ui.base.ViewEvent
import com.wvelabs.core_ui.base.ViewSideEffect
import com.wvelabs.core_ui.base.ViewState

class EditProfileContract {
    data class State(
        val isInitialized: Boolean = false,
        val isLoading: Boolean = false,
        val firstNameState: TextFieldState = TextFieldState(),
        val lastNameState: TextFieldState = TextFieldState(),
        val isNextButtonEnabled: Boolean = false,
    ) : ViewState

    sealed class Event : ViewEvent {
        data object Init : Event()
        data object OnBackClick : Event()
        data object OnUpdateProfileClick : Event()
    }

    sealed class Effect : ViewSideEffect {
        data object NavigateBack : Effect()
    }
}
