package com.example.paceapp.features.main.home

import com.example.paceapp.core.garmin.models.WatchModel
import com.example.paceapp.core.garmin.state.GarminSdkState
import com.example.paceapp.core.models.UserUiModel
import com.example.paceapp.features.main.home.models.ActivityUiModel
import com.example.paceapp.features.main.home.models.WatchMetric
import com.wvelabs.core_ui.base.ViewEvent
import com.wvelabs.core_ui.base.ViewSideEffect
import com.wvelabs.core_ui.base.ViewState

class HomeContract {
    data class State(
        val isInitialized: Boolean = false,
        val isLoading: Boolean = false,
        val userUiModel: UserUiModel? = null,
        val watchModel: WatchModel? = null,
        val metrics: List<WatchMetric> = emptyList(),
        val upcomingActivities: List<ActivityUiModel> = emptyList(),
        val lastSyncDate: String = "Wed, 1 May 2026",
        val garminSdkStatus: GarminSdkState = GarminSdkState.Uninitialized,
    ) : ViewState

    sealed class Event : ViewEvent {
        data object Init : Event()
        data object OnBackClick : Event()
        data class OnActivityClick(val activity: ActivityUiModel) : Event()

        data object OnNotificationClick : Event()
        data object OnNewEventClick : Event()

    }

    sealed class Effect : ViewSideEffect {
        data object NavigateBack : Effect()
        data object NavigateToNotifications : Effect()
        data object NavigateToCreateEvent : Effect()
    }
}