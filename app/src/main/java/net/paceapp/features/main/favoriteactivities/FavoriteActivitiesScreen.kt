package net.paceapp.features.main.favoriteactivities

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

import net.paceapp.features.main.favoriteactivities.FavoriteActivitiesContract.Effect
import net.paceapp.features.main.favoriteactivities.FavoriteActivitiesContract.Event
import net.paceapp.features.main.favoriteactivities.components.FavoriteActivitiesContent
import net.paceapp.features.main.history.HistoryContract

@Composable
fun FavoriteActivitiesScreen(
    viewModel: FavoriteActivitiesViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onNavigateToEventDetails: (String, String, String, String) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Init view model
    LaunchedEffect(key1 = Unit) {
          viewModel.setEvent(Event.Init)
    }

    // Handle one-time effects
    LaunchedEffect(key1 = Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is Effect.NavigateBack -> onBack()
                is Effect.NavigateToEventDetails -> onNavigateToEventDetails(
                    effect.id,
                    effect.eventName,
                    effect.location,
                    effect.date
                )
            }
        }
    }

    // Render content
    FavoriteActivitiesContent(
        state = state,
        onEvent = viewModel::setEvent
    )
}
