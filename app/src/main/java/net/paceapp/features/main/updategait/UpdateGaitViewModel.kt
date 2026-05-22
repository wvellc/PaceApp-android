package net.paceapp.features.main.updategait

import androidx.lifecycle.viewModelScope
import net.paceapp.core.base.BaseViewModel
import net.paceapp.core.domain.models.GaitPace
import net.paceapp.core.extensions.getDefaultGaits
import net.paceapp.features.main.updategait.UpdateGaitContract.Effect
import net.paceapp.features.main.updategait.UpdateGaitContract.Event
import net.paceapp.features.main.updategait.UpdateGaitContract.State
import net.paceapp.session.AppSessionManager
import com.wvelabs.core_network.di.ApplicationScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class UpdateGaitViewModel @Inject constructor(
    val sessionManager: AppSessionManager,
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
        setState { copy(runningGait = gaitPace) }
    }

    private fun handleWalkingGaitChanged(gaitPace: GaitPace) {
        setState { copy(walkingGait = gaitPace) }
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
            // TODO: Add Firebase Integration
            val currentUser = sessionManager.getUserDetails()
            if (currentUser != null) {
                val updatedUser = currentUser.copy(
                    walkingGait = walkingToSave,
                    runningGait = runningToSave
                )
                sessionManager.setUserDetails(updatedUser)
            }
        }
    }

}
