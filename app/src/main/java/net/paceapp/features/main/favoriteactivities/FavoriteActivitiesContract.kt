package net.paceapp.features.main.favoriteactivities

import com.wvelabs.core_ui.base.ViewEvent
import com.wvelabs.core_ui.base.ViewSideEffect
import com.wvelabs.core_ui.base.ViewState
import net.paceapp.core.models.ActivityUiModel

class FavoriteActivitiesContract {
    data class State(
        val isInitialized: Boolean = false,
        val isLoading: Boolean = true,
        val favorites: List<ActivityUiModel> = emptyList(),
    ) : ViewState

    sealed class Event : ViewEvent {

        data object Init : Event()
        data object OnBackClick : Event()
        data class OnActivityClick(val activity: ActivityUiModel) : Event()
        // Swipe-to-unfavorite (mirrors iOS .swipeActions un-favorite).
        data class OnUnfavoriteClick(val activity: ActivityUiModel) : Event()

    }

    sealed class Effect : ViewSideEffect {
        data object NavigateBack : Effect()
        data class NavigateToEventDetails(
            val id: String,
            val eventName: String,
            val location: String,
            val date: String,
        ) : Effect()
    }
}
