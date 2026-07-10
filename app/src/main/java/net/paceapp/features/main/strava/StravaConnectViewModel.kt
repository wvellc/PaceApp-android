package net.paceapp.features.main.strava

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import net.paceapp.core.base.BaseViewModel
import net.paceapp.core.strava.StravaManager
import net.paceapp.features.main.strava.StravaConnectContract.Effect
import net.paceapp.features.main.strava.StravaConnectContract.Event
import net.paceapp.features.main.strava.StravaConnectContract.State
import javax.inject.Inject

@HiltViewModel
class StravaConnectViewModel @Inject constructor(
    private val stravaManager: StravaManager,
) : BaseViewModel<State, Event, Effect>() {

    override fun setInitialState() = State()

    override fun handleEvents(event: Event) {
        when (event) {
            is Event.Init -> initData()
            is Event.OnBackClick -> setEffect { Effect.NavigateBack }
            is Event.OnConnectClick -> stravaManager.connect(event.context)
            is Event.OnDisconnectClick -> stravaManager.disconnect()
            is Event.OnSyncRecentClick -> stravaManager.syncRecent()
        }
    }

    // Mirror StravaManager's connection state (sourced from users/{uid}.strava) into
    // this screen's state; startObserving() is idempotent. Mirrors iOS .onAppear.
    private fun initData() {
        stravaManager.startObserving()
        stravaManager.state
            .onEach { s ->
                setState {
                    copy(
                        isConnected = s.isConnected,
                        athleteName = s.athleteName,
                        isWorking = s.isWorking,
                    )
                }
            }
            .launchIn(viewModelScope)
    }
}
