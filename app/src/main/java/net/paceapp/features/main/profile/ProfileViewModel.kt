package net.paceapp.features.main.profile

import androidx.lifecycle.viewModelScope
import net.paceapp.core.auth.AuthManager
import net.paceapp.core.base.BaseViewModel
import net.paceapp.core.data.firestore.UserProfileRepository
import net.paceapp.core.domain.usecases.ObserveUserUiModelUseCase
import net.paceapp.core.garmin.EventSyncManager
import net.paceapp.features.main.profile.ProfileContract.Effect
import net.paceapp.features.main.profile.ProfileContract.Event
import net.paceapp.features.main.profile.ProfileContract.State
import net.paceapp.features.main.profile.enums.ProfileOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    val observeUserUiModelUseCase: ObserveUserUiModelUseCase,
    private val userProfileRepository: UserProfileRepository,
    private val eventSyncManager: EventSyncManager,
    private val authManager: AuthManager,
) : BaseViewModel<State, Event, Effect>() {
    override fun setInitialState() = State()

    override fun handleEvents(event: Event) {
        when (event) {
            is Event.Init -> initData()
            is Event.OnBackClick -> {
                setEffect { Effect.NavigateBack }
            }

            is Event.OnSettingClick -> handleOnSettingClick()
            is Event.OnEditProfileClick -> handleOnEditProfileClick()
            is Event.OnToggleSwitch -> handleOnToggleSwitch(event.option, event.isEnabled)
            is Event.OnProfileOptionClick -> handleOnProfileOptionClick(event.option)
        }
    }


    private fun initData() {
        if (currentState.isInitialized) return
        observeUserData()
        observeIntervalSettings()
        setState { copy(isInitialized = true) }
    }

    private fun observeUserData() {
        observeUserUiModelUseCase()
            .onEach {
                setState { copy(userUiModel = it) }
            }.launchIn(viewModelScope)
    }

    // Prefill the interval-alert toggles from the Firestore user doc so they reflect
    // whatever the watch or the other phone last set.
    private fun observeIntervalSettings() {
        val uid = authManager.currentUid ?: return
        userProfileRepository.observeUser(uid)
            .onEach { doc ->
                setState {
                    copy(
                        isIntervalVibrateEnabled = doc?.intervalVibrate ?: isIntervalVibrateEnabled,
                        isIntervalBeepEnabled = doc?.intervalBeep ?: isIntervalBeepEnabled,
                    )
                }
            }.launchIn(viewModelScope)
    }


    private fun handleOnSettingClick() {
        setEffect { Effect.NavigateToSettings }
    }

    private fun handleOnEditProfileClick() {
        setEffect { Effect.NavigateToEditProfile }
    }

    private fun handleOnProfileOptionClick(option: ProfileOptions) {
        when (option) {
            ProfileOptions.MANAGE_YOUR_WATCH -> setEffect { Effect.NavigateToManageWatch }
            ProfileOptions.INTERVAL_VIBRATE -> setIntervalVibrate(!currentState.isIntervalVibrateEnabled)
            ProfileOptions.INTERVAL_BEEP -> setIntervalBeep(!currentState.isIntervalBeepEnabled)
            ProfileOptions.SET_GAIT -> setEffect { Effect.NavigateToSetGait }
        }
    }

    private fun handleOnToggleSwitch(option: ProfileOptions, enabled: Boolean) {
        when (option) {
            ProfileOptions.INTERVAL_VIBRATE -> setIntervalVibrate(enabled)
            ProfileOptions.INTERVAL_BEEP -> setIntervalBeep(enabled)
            else -> Unit
        }
    }

    // Interval-alert toggles: update UI, persist to the Firestore user doc, and push
    // to the watch (sync_settings). Mirrors iOS "call sendSettings() after mutation".
    private fun setIntervalVibrate(enabled: Boolean) {
        setState { copy(isIntervalVibrateEnabled = enabled) }
        persistIntervalSetting("vibrate_alert", enabled) { uid ->
            userProfileRepository.updateIntervalVibrate(uid, enabled)
        }
    }

    private fun setIntervalBeep(enabled: Boolean) {
        setState { copy(isIntervalBeepEnabled = enabled) }
        persistIntervalSetting("beep_alert", enabled) { uid ->
            userProfileRepository.updateIntervalBeep(uid, enabled)
        }
    }

    private fun persistIntervalSetting(
        watchKey: String,
        enabled: Boolean,
        firestoreWrite: suspend (String) -> Unit,
    ) {
        val uid = authManager.currentUid
        if (uid != null) {
            viewModelScope.launch { runCatching { firestoreWrite(uid) } }
        }
        eventSyncManager.updateSetting(watchKey, enabled)
        eventSyncManager.sendSettings()
    }


}
