package net.paceapp.features.main.favoriteactivities

import dagger.hilt.android.lifecycle.HiltViewModel
import net.paceapp.core.base.BaseViewModel
import net.paceapp.core.mappers.ActivityToUiModelMapper
import net.paceapp.core.models.ActivityDummyData
import net.paceapp.core.models.ActivityUiModel
import net.paceapp.features.main.favoriteactivities.FavoriteActivitiesContract.Effect
import net.paceapp.features.main.favoriteactivities.FavoriteActivitiesContract.Event
import net.paceapp.features.main.favoriteactivities.FavoriteActivitiesContract.State
import javax.inject.Inject

@HiltViewModel
class FavoriteActivitiesViewModel @Inject constructor(
    private val activityToUiModelMapper: ActivityToUiModelMapper
) : BaseViewModel<State, Event, Effect>() {

    override fun setInitialState() = State()

    override fun handleEvents(event: Event) {
        when (event) {
            is Event.Init -> initData()
            is Event.OnBackClick -> {
                setEffect { Effect.NavigateBack }
            }

            is Event.OnActivityClick -> handleActivityClick(event.activity)
        }
    }


    private fun initData() {
        if (currentState.isInitialized) return

        fetchFavorites()
        setState { copy(isInitialized = true) }
    }

    private fun fetchFavorites() {
        runTask(
            block = {
                fetchFavoriteActivities()
            },
            onLoading = { loadingState ->
                setState { copy(isLoading = loadingState) }
            },
            onSuccess = { activities ->
                setState { copy(favorites = activities) }
            },
        )
    }

    private fun fetchFavoriteActivities(): List<ActivityUiModel> {
        return ActivityDummyData.getDummyActivities().take(4)
            .map { activityToUiModelMapper.map(it) }
    }

    private fun handleActivityClick(activity: ActivityUiModel) {
        setEffect {
            Effect.NavigateToEventDetails(
                id = activity.id,
                eventName = activity.title,
                location = activity.location,
                date = activity.date
            )
        }
    }
}
