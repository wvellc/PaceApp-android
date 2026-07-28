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
import com.wvelabs.core_network.di.ApplicationScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
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
    @param:ApplicationScope private val appScope: CoroutineScope,
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
        // Ask the watch to push its settings once per lifetime; gait/height/weight then stay live via the Firestore profile listener.
        eventSyncManager.requestWatchSettings()
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
            // Interval alerts are driven solely by the switch (OnToggleSwitch). The row
            // is not independently clickable for switch options, so a row-tap must never
            // toggle here — doing so would double-fire and cancel the switch's change.
            ProfileOptions.INTERVAL_VIBRATE, ProfileOptions.INTERVAL_BEEP -> Unit
            ProfileOptions.SET_GAIT -> setEffect { Effect.NavigateToSetGait }
            ProfileOptions.STRAVA -> setEffect { Effect.NavigateToStrava }
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
        // Capture the OTHER alert's current UI value before mutating state, so the watch
        // payload carries both alerts consistently (see persistIntervalSettings).
        val beep = currentState.isIntervalBeepEnabled
        setState { copy(isIntervalVibrateEnabled = enabled) }
        persistIntervalSettings(vibrate = enabled, beep = beep) { uid ->
            userProfileRepository.updateIntervalVibrate(uid, enabled)
        }
    }

    private fun setIntervalBeep(enabled: Boolean) {
        val vibrate = currentState.isIntervalVibrateEnabled
        setState { copy(isIntervalBeepEnabled = enabled) }
        persistIntervalSettings(vibrate = vibrate, beep = enabled) { uid ->
            userProfileRepository.updateIntervalBeep(uid, enabled)
        }
    }

    // sendSettings() always transmits BOTH vibrate_alert and beep_alert. The watch payload
    // is built from the synced_settings prefs blob, which is a separate store from the
    // Firestore doc that drives these toggles — so writing only the toggled key would let
    // a stale value for the other alert ride along and flip it on the watch. Write both
    // keys from the current UI truth, then push once.
    private fun persistIntervalSettings(
        vibrate: Boolean,
        beep: Boolean,
        firestoreWrite: suspend (String) -> Unit,
    ) {
        val uid = authManager.currentUid
        if (uid != null) {
            // Application scope, not viewModelScope: a Firestore write awaits the server
            // ack, so toggling then immediately leaving Profile would cancel it and the
            // change would be lost. appScope outlives the screen.
            appScope.launch { runCatching { firestoreWrite(uid) } }
        }
        eventSyncManager.updateSetting("vibrate_alert", vibrate)
        eventSyncManager.updateSetting("beep_alert", beep)
        eventSyncManager.sendSettings()
    }


}
