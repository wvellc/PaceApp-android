package net.paceapp.features.main.tabhost

import android.content.Context
import androidx.lifecycle.viewModelScope
import net.paceapp.core.base.BaseViewModel
import net.paceapp.core.domain.repositories.UserRepository
import net.paceapp.core.garmin.GarminDeviceManager
import net.paceapp.features.main.home.models.WatchMetric
import net.paceapp.features.main.tabhost.TabHostContract.Effect
import net.paceapp.features.main.tabhost.TabHostContract.Event
import net.paceapp.features.main.tabhost.TabHostContract.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TabHostViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val garminDeviceManager: GarminDeviceManager
) : BaseViewModel<State, Event, Effect>() {
    private var hasAttemptedRestore = false

    override fun setInitialState() = State()

    override fun handleEvents(event: Event) {
        when (event) {
            is Event.Init -> initData()
        }
    }

    private fun initData() {
        if (currentState.isInitialized) return


        setState { copy(isInitialized = true, metrics = getWatchMetrics()) }
    }


    fun restoreGarminConnection(context: Context) {
        // Only run this once per app lifecycle to avoid spamming the SDK
        if (hasAttemptedRestore) return
        hasAttemptedRestore = true

        viewModelScope.launch {
            val watchId = userRepository.getPairedWatchId()
            garminDeviceManager.onAppLaunchRestore(context, watchId)
        }
    }

    private fun getWatchMetrics(
        bpm: String = "60",
        hrs: String = "12",
        goal: String = "-01:10",
        left: String = "07:20",
        pace: String = "9:09"
    ): List<WatchMetric> = listOf(
        WatchMetric.HeartRate(bpm),
        WatchMetric.OverallTime(hrs),
        WatchMetric.GoalTime(goal),
        WatchMetric.RemainingTime(left),
        WatchMetric.Pace(pace)
    )
}
