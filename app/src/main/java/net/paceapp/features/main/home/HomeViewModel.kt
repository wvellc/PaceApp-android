package net.paceapp.features.main.home

import android.content.Context
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import net.paceapp.core.auth.AuthManager
import net.paceapp.core.base.BaseViewModel
import net.paceapp.core.data.firestore.EventRepository
import net.paceapp.core.domain.usecases.ObserveUserUiModelUseCase
import net.paceapp.core.garmin.GarminDeviceManager
import net.paceapp.core.garmin.enums.WatchConnectionState
import net.paceapp.core.mappers.ActivityToUiModelMapper
import net.paceapp.core.mappers.EventDocumentUiMapper
import net.paceapp.core.models.ActivityUiModel
import net.paceapp.features.main.home.HomeContract.Effect
import net.paceapp.features.main.home.HomeContract.Event
import net.paceapp.features.main.home.HomeContract.State
import net.paceapp.features.main.home.models.WatchMetric
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val observeUserUiModelUseCase: ObserveUserUiModelUseCase,
    private val garminDeviceManager: GarminDeviceManager,
    private val activityToUiModelMapper: ActivityToUiModelMapper,
    private val eventRepository: EventRepository,
    private val authManager: AuthManager,
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
            is Event.OnStartPairing -> handleOnStartPairing(event.context)
            is Event.OnNewEventClick -> handleOnNewEventClick()
            is Event.OnFavoriteClick -> handleOnFavoriteClick()
        }
    }


    private fun initData() {
        if (currentState.isInitialized) return
        observeUserData()
        observeActiveWatchDevice()
        observeGarminSdkStatus()
        fetchUpcomingActivities()
        setState { copy(isInitialized = true) }
    }

    private fun observeGarminSdkStatus() {
        garminDeviceManager.sdkStateFlow
            .onEach { status ->
                setState {
                    copy(garminSdkStatus = status)
                }
            }.launchIn(viewModelScope)
    }

    private fun observeUserData() {
        observeUserUiModelUseCase()
            .onEach {
                setState { copy(userUiModel = it) }
            }.launchIn(viewModelScope)
    }


    private fun observeActiveWatchDevice() {
        garminDeviceManager.activeDevice.onEach { watch ->
            if (watch != null && watch.status == WatchConnectionState.CONNECTED) {

            }
            setState {
                copy(
                    watchModel = watch,
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

    // Upcoming = active events from Firestore (shared thepaceapp backend). No date
    // filter — overdue-but-active events stay visible (matches iOS Home).
    private fun fetchUpcomingActivities() {
        val uid = authManager.currentUid ?: return
        eventRepository.observeActiveEvents(uid)
            .onEach { docs ->
                val activities = docs
                    .map { activityToUiModelMapper.map(EventDocumentUiMapper.toActivityModel(it)) }
                setState { copy(upcomingActivities = activities) }
            }
            .launchIn(viewModelScope)
    }


    private fun handleOnNotificationClick() {
        setEffect { Effect.NavigateToNotifications }
    }


    private fun handleOnStartPairing(context: Context) {
        // Safety check: Don't start if already connected
        if (currentState.watchModel?.status == WatchConnectionState.CONNECTED) return

        setEffect { Effect.NavigateToManageWatch }
    }

    private fun handleOnActivityClick(activity: ActivityUiModel) {
        setEffect {
            Effect.NavigateToEventDetails(
                id = activity.id,
                eventName = activity.title,
                location = activity.location,
                date = activity.date
            )
        }
    }


    private fun handleOnNewEventClick() {
        setEffect { Effect.NavigateToCreateEvent }
    }

    private fun handleOnFavoriteClick() {
        setEffect { Effect.NavigateToFavorites }
    }

}

