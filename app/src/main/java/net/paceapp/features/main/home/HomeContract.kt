package net.paceapp.features.main.home

import android.content.Context
import net.paceapp.core.garmin.models.WatchModel
import net.paceapp.core.garmin.state.GarminSdkState
import net.paceapp.core.models.UserUiModel
import net.paceapp.features.main.home.models.ActivityUiModel
import net.paceapp.features.main.home.models.WatchMetric
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
        data class OnStartPairing(val context: Context) : Event()

        data object OnNotificationClick : Event()
        data object OnNewEventClick : Event()
        data object OnFavoriteClick : Event()

    }

    sealed class Effect : ViewSideEffect {
        data object NavigateBack : Effect()
        data object NavigateToNotifications : Effect()
        data object NavigateToCreateEvent : Effect()
        data object NavigateToManageWatch : Effect()
        data object NavigateToEventDetails : Effect()
    }
}