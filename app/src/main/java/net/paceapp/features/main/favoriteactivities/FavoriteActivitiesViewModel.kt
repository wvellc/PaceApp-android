package net.paceapp.features.main.favoriteactivities

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.paceapp.core.auth.AuthManager
import net.paceapp.core.base.BaseViewModel
import net.paceapp.core.data.firestore.FavoriteRepository
import net.paceapp.core.mappers.ActivityToUiModelMapper
import net.paceapp.core.mappers.EventDocumentUiMapper
import net.paceapp.core.models.ActivityUiModel
import net.paceapp.features.main.favoriteactivities.FavoriteActivitiesContract.Effect
import net.paceapp.features.main.favoriteactivities.FavoriteActivitiesContract.Event
import net.paceapp.features.main.favoriteactivities.FavoriteActivitiesContract.State
import javax.inject.Inject

@HiltViewModel
class FavoriteActivitiesViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
    private val authManager: AuthManager,
    private val activityToUiModelMapper: ActivityToUiModelMapper,
) : BaseViewModel<State, Event, Effect>() {

    override fun setInitialState() = State()

    override fun handleEvents(event: Event) {
        when (event) {
            is Event.Init -> initData()
            is Event.OnBackClick -> {
                setEffect { Effect.NavigateBack }
            }

            is Event.OnActivityClick -> handleActivityClick(event.activity)
            is Event.OnUnfavoriteClick -> handleUnfavorite(event.activity)
        }
    }

    // Un-favorite: optimistically drop the row, then toggle it off in Firestore.
    // Mirrors iOS FavoritesViewModel.unFavorite.
    private fun handleUnfavorite(activity: ActivityUiModel) {
        val uid = authManager.currentUid ?: return
        setState { copy(favorites = favorites.filterNot { it.id == activity.id }) }
        viewModelScope.launch {
            runCatching { favoriteRepository.toggleFavorite(uid, activity.id) }
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

    // Hydrate favorited events from Firestore for the signed-in user, then format
    // them into the list UI model. Empty when signed out.
    private suspend fun fetchFavoriteActivities(): List<ActivityUiModel> {
        val uid = authManager.currentUid ?: return emptyList()
        return favoriteRepository.fetchFavoriteEvents(uid)
            .map { EventDocumentUiMapper.toActivityModel(it) }
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
