package com.example.paceapp.features.main.editprofile

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.example.paceapp.core.base.BaseViewModel
import com.example.paceapp.core.domain.usecases.ValidateUserNamesUseCase
import com.example.paceapp.features.main.editprofile.EditProfileContract.Effect
import com.example.paceapp.features.main.editprofile.EditProfileContract.Event
import com.example.paceapp.features.main.editprofile.EditProfileContract.State
import com.example.paceapp.session.AppSessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val validateUserNames: ValidateUserNamesUseCase,
    private val sessionManager: AppSessionManager,
) : BaseViewModel<State, Event, Effect>() {

    override fun setInitialState() = State()

    override fun handleEvents(event: Event) {
        when (event) {
            is Event.Init -> initData()
            is Event.OnBackClick -> {
                setEffect { Effect.NavigateBack }
            }

            is Event.OnUpdateProfileClick -> handleUpdateProfileClick()
        }
    }


    private fun initData() {
        if (currentState.isInitialized) return
        viewModelScope.launch {
            getUserData()
            observeFields()
            setState { copy(isInitialized = true) }
        }
    }

    private suspend fun getUserData() {
        val currentUser = sessionManager.getUserDetails() ?: return
        currentState.firstNameState.edit {
            replace(0, length, currentUser.firstName.toString())
        }
        currentState.lastNameState.edit {
            replace(0, length, currentUser.lastName.toString())
        }
    }

    private fun observeFields() {
        val textValidationFlow = snapshotFlow {
            Pair(
                currentState.firstNameState.text.toString(),
                currentState.lastNameState.text.toString(),
            )
        }

        observeState(textValidationFlow) { (firstName, lastName) ->
            val isValid = validateUserNames(firstName = firstName, lastName = lastName)
            copy(isNextButtonEnabled = isValid)
        }
    }

    private fun handleUpdateProfileClick() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            saveUserDetails()
            setState { copy(isLoading = false) }
            setEffect { Effect.NavigateBack }
        }
    }

    private suspend fun saveUserDetails() {
        //TODO:Update data in firebase db
        val currentUser = sessionManager.getUserDetails() ?: return
        delay(500)//dummy delay
        sessionManager.setUserDetails(
            currentUser.copy(
                firstName = currentState.firstNameState.text.toString(),
                lastName = currentState.lastNameState.text.toString()
            )
        )
    }
}
