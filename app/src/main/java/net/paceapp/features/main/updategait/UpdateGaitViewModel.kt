package net.paceapp.features.main.updategait

import androidx.lifecycle.viewModelScope
import net.paceapp.core.auth.AuthManager
import net.paceapp.core.base.BaseViewModel
import net.paceapp.core.data.firestore.GaitDocument
import net.paceapp.core.data.firestore.UserProfileRepository
import net.paceapp.core.domain.models.GaitPace
import net.paceapp.core.domain.models.GaitUnit
import net.paceapp.core.extensions.getDefaultGaits
import net.paceapp.core.garmin.EventSyncManager
import net.paceapp.core.garmin.GaitStrideCalculator
import net.paceapp.features.main.updategait.UpdateGaitContract.Effect
import net.paceapp.features.main.updategait.UpdateGaitContract.Event
import net.paceapp.features.main.updategait.UpdateGaitContract.State
import net.paceapp.session.AppSessionManager
import com.wvelabs.core_network.di.ApplicationScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class UpdateGaitViewModel @Inject constructor(
    val sessionManager: AppSessionManager,
    private val userProfileRepository: UserProfileRepository,
    private val authManager: AuthManager,
    private val eventSyncManager: EventSyncManager,
    @param:ApplicationScope val appScope: CoroutineScope
) : BaseViewModel<State, Event, Effect>() {

    override fun setInitialState() = State()

    override fun handleEvents(event: Event) {
        when (event) {
            is Event.Init -> initData()
            is Event.OnWalkingGaitChanged -> handleWalkingGaitChanged(event.gaitPace)
            is Event.OnRunningGaitChanged -> handleRunningGaitChanged(event.gaitPace)
            is Event.OnBackClick -> handleBackClick()
        }
    }


    private fun initData() {
        if (currentState.isInitialized) return
        setGaitValues()
        setState { copy(isInitialized = true) }
    }

    private fun setGaitValues() {
        viewModelScope.launch {
            val (walkGait, runGait) = getWalkingRunningGaits()

            setState {
                copy(
                    walkingGait = walkGait,
                    runningGait = runGait,
                )
            }
        }
    }

    private suspend fun getWalkingRunningGaits(
    ): Pair<GaitPace, GaitPace> = withContext(Dispatchers.Default) {
        // Prefer the Firestore user doc (source of truth, parity with iOS).
        val uid = authManager.currentUid
        val remoteGait = uid?.let { userProfileRepository.observeUser(it).firstOrNull()?.gait }
        if (remoteGait != null) {
            return@withContext Pair(
                GaitPace(remoteGait.walkingStepLength.toFloat(), remoteGait.walkingUnit.toGaitUnit()),
                GaitPace(remoteGait.runningStepLength.toFloat(), remoteGait.runningUnit.toGaitUnit()),
            )
        }

        val userData = sessionManager.getUserDetails()
        when {
            userData?.walkingGait != null && userData.runningGait != null -> Pair(
                userData.walkingGait,
                userData.runningGait
            )

            userData != null -> userData.gender.getDefaultGaits()

            //Default values
            else -> Pair(GaitPace(1.0f), GaitPace(1.0f))
        }
    }

    private fun handleRunningGaitChanged(gaitPace: GaitPace) {
        setState { copy(runningGait = applyGaitChange(runningGait, gaitPace)) }
    }

    private fun handleWalkingGaitChanged(gaitPace: GaitPace) {
        setState { copy(walkingGait = applyGaitChange(walkingGait, gaitPace)) }
    }

    // On a unit toggle, convert the shown step-length instead of keeping the raw number (iOS SetGaitStepView onChange parity).
    private fun applyGaitChange(current: GaitPace, incoming: GaitPace): GaitPace {
        if (incoming.unit == current.unit) return incoming
        val converted = GaitStrideCalculator.convert(
            current.value.toDouble(),
            current.unit.firestoreName(),
            incoming.unit.firestoreName(),
        )
        return GaitPace(converted.toFloat(), incoming.unit)
    }

    private fun handleBackClick() {
        val walkingToSave = currentState.walkingGait
        val runningToSave = currentState.runningGait
        setEffect { Effect.NavigateBack }
        //Save gait data silently in background
        updateGait(walkingToSave, runningToSave)
    }

    private fun updateGait(
        walkingToSave: GaitPace,
        runningToSave: GaitPace
    ) {
        appScope.launch {
            // Keep local session in sync (existing behaviour).
            val currentUser = sessionManager.getUserDetails()
            if (currentUser != null) {
                val updatedUser = currentUser.copy(
                    walkingGait = walkingToSave,
                    runningGait = runningToSave
                )
                sessionManager.setUserDetails(updatedUser)
            }

            // Persist to the Firestore user doc (units as full words "Feet"/"Meters").
            val uid = authManager.currentUid
            if (uid != null) {
                val gait = GaitDocument(
                    walkingStepLength = walkingToSave.value.toDouble(),
                    walkingUnit = walkingToSave.unit.firestoreName(),
                    runningStepLength = runningToSave.value.toDouble(),
                    runningUnit = runningToSave.unit.firestoreName(),
                )
                runCatching { userProfileRepository.updateGait(uid, gait) }
            }

            // Push to the watch (unit boundary: full words → "ft"/"m").
            eventSyncManager.updateSetting("walking_gait", walkingToSave.value.toDouble())
            eventSyncManager.updateSetting("walking_gait_measure", walkingToSave.unit.watchName())
            eventSyncManager.updateSetting("running_gait", runningToSave.value.toDouble())
            eventSyncManager.updateSetting("running_gait_measure", runningToSave.unit.watchName())
            eventSyncManager.sendSettings()
        }
    }

    // GaitUnit → Firestore/app full word.
    private fun GaitUnit.firestoreName(): String =
        if (this == GaitUnit.METERS) "Meters" else "Feet"

    // GaitUnit → watch BLE short form.
    private fun GaitUnit.watchName(): String =
        if (this == GaitUnit.METERS) "m" else "ft"

    // Firestore full word → GaitUnit.
    private fun String.toGaitUnit(): GaitUnit =
        if (lowercase().startsWith("m")) GaitUnit.METERS else GaitUnit.FEET

}
