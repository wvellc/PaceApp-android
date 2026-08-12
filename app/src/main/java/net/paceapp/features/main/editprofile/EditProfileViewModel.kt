package net.paceapp.features.main.editprofile

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import net.paceapp.core.auth.AuthManager
import net.paceapp.core.base.BaseViewModel
import net.paceapp.core.data.firestore.UserProfileRepository
import net.paceapp.core.domain.usecases.ValidateUserNamesUseCase
import net.paceapp.features.main.editprofile.EditProfileContract.Effect
import net.paceapp.features.main.editprofile.EditProfileContract.Event
import net.paceapp.features.main.editprofile.EditProfileContract.State
import net.paceapp.session.AppSessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val validateUserNames: ValidateUserNamesUseCase,
    private val sessionManager: AppSessionManager,
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
        // Prefer the Firestore user doc (source of truth, parity with iOS),
        // falling back to the local session for the initial names.
        val uid = authManager.currentUid
        val remoteDoc = uid?.let { userProfileRepository.observeUser(it).firstOrNull() }
        val firstName = remoteDoc?.firstName?.ifBlank { null }
            ?: sessionManager.getUserDetails()?.firstName.orEmpty()
        val lastName = remoteDoc?.lastName?.ifBlank { null }
            ?: sessionManager.getUserDetails()?.lastName.orEmpty()

        currentState.firstNameState.edit {
            replace(0, length, firstName)
        }
        currentState.lastNameState.edit {
            replace(0, length, lastName)
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
            // Confirm the account still exists — a deleted/disabled account (elsewhere) is
            // signed out here instead of appearing to save (mirrors iOS 97bbfcf).
            if (!authManager.verifyAccountStillValid()) {
                setState { copy(isLoading = false) }
                return@launch
            }
            saveUserDetails()
            setState { copy(isLoading = false) }
            setEffect { Effect.NavigateBack }
        }
    }

    private suspend fun saveUserDetails() {
        val firstName = currentState.firstNameState.text.toString()
        val lastName = currentState.lastNameState.text.toString()

        // Persist to the Firestore user doc: fetch current, copy with new names, upsert.
        val uid = authManager.currentUid
        if (uid != null) {
            val currentDoc = userProfileRepository.getUser(uid)
            if (currentDoc != null) {
                runCatching {
                    userProfileRepository.upsertUser(
                        currentDoc.copy(firstName = firstName, lastName = lastName)
                    )
                }
            }
        }

        // Keep local session in sync (existing behaviour).
        val currentUser = sessionManager.getUserDetails() ?: return
        sessionManager.setUserDetails(
            currentUser.copy(
                firstName = firstName,
                lastName = lastName
            )
        )
    }
}
