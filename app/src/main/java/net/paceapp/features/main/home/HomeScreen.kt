package net.paceapp.features.main.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.paceapp.features.main.home.HomeContract.Effect
import net.paceapp.features.main.home.HomeContract.Event
import net.paceapp.features.main.home.components.HomeContent

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToCreateEvent: () -> Unit,
    onNavigateToManageWatch: () -> Unit,
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
                is Effect.NavigateToNotifications -> onNavigateToNotifications()
                is Effect.NavigateToCreateEvent -> onNavigateToCreateEvent()
                is Effect.NavigateToManageWatch -> onNavigateToManageWatch()
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
    HomeContent(
        state = state,
        onEvent = viewModel::setEvent
    )
}
