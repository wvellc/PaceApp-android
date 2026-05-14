package com.example.paceapp.features.main.home

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.paceapp.core.base.BaseViewModel
import com.example.paceapp.core.domain.usecases.ObserveUserUiModelUseCase
import com.example.paceapp.core.garmin.GarminDeviceManager
import com.example.paceapp.core.garmin.enums.WatchConnectionState
import com.example.paceapp.core.garmin.models.WatchModel
import com.example.paceapp.features.main.home.HomeContract.Effect
import com.example.paceapp.features.main.home.HomeContract.Event
import com.example.paceapp.features.main.home.HomeContract.State
import com.example.paceapp.features.main.home.domain.models.ActivityDomainModel
import com.example.paceapp.features.main.home.mappers.ActivityUiMapper
import com.example.paceapp.features.main.home.models.ActivityUiModel
import com.example.paceapp.features.main.home.models.WatchMetric
import com.wvelabs.core_network.utils.AppLogger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import java.util.UUID
import javax.inject.Inject
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val observeUserUiModelUseCase: ObserveUserUiModelUseCase,
    private val garminDeviceManager: GarminDeviceManager,
    private val activityUiMapper: ActivityUiMapper
) : BaseViewModel<State, Event, Effect>() {

    override fun setInitialState() = State()

    override fun handleEvents(event: Event) {
        when (event) {
            is Event.Init -> initData()
            is Event.OnBackClick -> {
                setEffect { Effect.NavigateBack }
            }

            is Event.OnNotificationClick -> handleOnNotificationClick()
            is Event.OnActivityClick -> handleOnActivityClick(event.activity)
        }
    }


    val dummyActivities = listOf(
        ActivityDomainModel(
            id = "act_001",
            name = "Zilker Park Run",
            date = LocalDateTime(
                year = 2026,
                month = 1,
                day = 31,
                hour = 7,
                minute = 30,
                second = 0,
                nanosecond = 0
            ),
            distanceMiles = 5.00,
            location = "Austin, TX",
            goalTime = 1.hours + 45.minutes // 01:45:00
        ),
        ActivityDomainModel(
            id = "act_002",
            name = "Sculpture Falls Hike",
            date = LocalDateTime(
                year = 2026,
                month = 2,
                day = 2,
                hour = 9,
                minute = 0,
                second = 0,
                nanosecond = 0
            ),
            distanceMiles = 4.00,
            location = "Twin Falls",
            goalTime = 35.minutes // 00:35:00
        ),
        ActivityDomainModel(
            id = "act_003",
            name = "Downtown Tempo Push",
            date = LocalDateTime(
                year = 2026,
                month = 2,
                day = 5,
                hour = 18,
                minute = 15,
                second = 0,
                nanosecond = 0
            ),
            distanceMiles = 3.10, // 5K
            location = "New York City",
            goalTime = 25.minutes // 00:25:00
        ),
        ActivityDomainModel(
            id = "act_004",
            name = "Weekend Long Ride",
            date = LocalDateTime(
                year = 2026,
                month = 2,
                day = 8,
                hour = 6,
                minute = 0,
                second = 0,
                nanosecond = 0
            ),
            distanceMiles = 25.50,
            location = "Pacific Coast Highway",
            goalTime = 2.hours + 10.minutes // 02:10:00
        ),
        ActivityDomainModel(
            id = "act_005",
            name = "Recovery Jog",
            date = LocalDateTime(
                year = 2026,
                month = 2,
                day = 10,
                hour = 17,
                minute = 30,
                second = 0,
                nanosecond = 0
            ),
            distanceMiles = 2.00,
            location = "Local Track",
            goalTime = 20.minutes // 00:20:00
        )
    )

    private fun initData() {
        if (currentState.isInitialized) return
        observeUserData()
        observeActiveWatchDevice()
        fetchUpcomingActivities()
        setState { copy(isInitialized = true) }
    }


    private fun observeUserData() {
        observeUserUiModelUseCase()
            .onEach {
                setState { copy(userUiModel = it) }
            }.launchIn(viewModelScope)
    }


    private fun observeActiveWatchDevice() {
        garminDeviceManager.activeDevice.onEach { watch ->
            setState {
                // TODO-FIX:replace dummy watch model 'watch'
                copy(
                    watchModel = WatchModel(
                        id = UUID.randomUUID().toString(),
                        name = "Forerunner 245",
                        model = "Jack’s Watch",
                        status = WatchConnectionState.CONNECTED
                    ),
                    metrics = getWatchMetrics()
                )
            }
        }.launchIn(viewModelScope)

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

    private fun fetchUpcomingActivities() {
        viewModelScope.launch {
            //TODO:Call API
            val activities = dummyActivities.take(2).map { activityUiMapper.map(it) }

            setState {
                copy(upcomingActivities = activities)
            }
        }
    }


    private fun handleOnNotificationClick() {
    }


    private fun handleOnStartPairing(context: Context) {
        // Safety check: Don't start if already connected
        if (currentState.watchModel?.status == WatchConnectionState.CONNECTED) return

        viewModelScope.launch {
            setState { copy(isLoading = true) }

            val devices = garminDeviceManager.getKnownDevicesAfterInit(context)

            setState { copy(isLoading = false) }

            if (devices != null) {
//                setEffect { Effect.NavigateToSelectDevice(devices) }
                //TODO:handle navigation
            } else {
                // Handle error (e.g. Garmin Connect app not found)
                AppLogger.e("Initialization failed. Is Garmin Connect installed?")

            }
        }
    }

    private fun handleOnActivityClick(activity: ActivityUiModel) {
    }


}
