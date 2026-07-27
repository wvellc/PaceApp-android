package net.paceapp.features.main.home

import android.content.Context
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import net.paceapp.core.auth.AuthManager
import net.paceapp.core.base.BaseViewModel
import net.paceapp.core.data.firestore.EventDocument
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
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val observeUserUiModelUseCase: ObserveUserUiModelUseCase,
    private val garminDeviceManager: GarminDeviceManager,
    private val activityToUiModelMapper: ActivityToUiModelMapper,
    private val eventRepository: EventRepository,
    private val authManager: AuthManager,
) : BaseViewModel<State, Event, Effect>() {

    // Latest completed event backing the header metrics + the distance⇄finish flash.
    private var latestCompletedDoc: EventDocument? = null
    private var flashShowsDistance = true
    private var flashJob: Job? = null

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
            is Event.OnFaqClick -> setEffect { Effect.OpenFaq }
        }
    }


    private fun initData() {
        if (currentState.isInitialized) return
        observeUserData()
        observeActiveWatchDevice()
        observeGarminSdkStatus()
        fetchUpcomingActivities()
        observeLatestCompletedMetrics()
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
            setState { copy(watchModel = watch) }
        }.launchIn(viewModelScope)
    }

    // Header metrics = the latest completed event's real, watch-sourced values (mirrors
    // iOS refreshLatestCompletedMetrics). observeCompletedEvents is updatedAt-DESC, so
    // first() is the most-recently-active completed run; the row hides when there is
    // none. Re-emits automatically whenever an event completes or is edited.
    private fun observeLatestCompletedMetrics() {
        val uid = authManager.currentUid ?: return
        eventRepository.observeCompletedEvents(uid)
            .onEach { docs ->
                val latest = docs.firstOrNull()
                latestCompletedDoc = latest
                if (latest == null) {
                    stopFlash()
                    setState { copy(metrics = emptyList()) }
                } else {
                    flashShowsDistance = true
                    setState {
                        copy(metrics = HomeMetricsMapper.metrics(latest, showDistanceFace = true))
                    }
                    startFlash()
                }
            }
            .launchIn(viewModelScope)
    }

    // Alternates the capsule-2 face (distance ⇄ finish time) every 2.5s, swapping only
    // that slot in place so the other four capsules stay put. Mirrors iOS startFlashLoop.
    private fun startFlash() {
        flashJob?.cancel()
        flashJob = viewModelScope.launch {
            while (isActive) {
                delay(FLASH_INTERVAL_MS)
                val doc = latestCompletedDoc ?: break
                flashShowsDistance = !flashShowsDistance
                val face = HomeMetricsMapper.face(doc, flashShowsDistance)
                setState {
                    val updated = metrics.toMutableList()
                    if (updated.size > HomeMetricsMapper.FLASH_SLOT_INDEX) {
                        updated[HomeMetricsMapper.FLASH_SLOT_INDEX] = face
                        copy(metrics = updated)
                    } else {
                        this
                    }
                }
            }
        }
    }

    private fun stopFlash() {
        flashJob?.cancel()
        flashJob = null
    }

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

    companion object {
        private const val FLASH_INTERVAL_MS = 2_500L
    }
}

