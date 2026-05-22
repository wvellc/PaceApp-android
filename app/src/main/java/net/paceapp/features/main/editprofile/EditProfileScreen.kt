package net.paceapp.features.main.editprofile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

import net.paceapp.features.main.editprofile.EditProfileContract.Effect
import net.paceapp.features.main.editprofile.EditProfileContract.Event
import net.paceapp.features.main.editprofile.components.EditProfileContent

@Composable
fun EditProfileScreen(
    viewModel: EditProfileViewModel = hiltViewModel(),
    onBack: () -> Unit
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
            }
        }
    }

    // Render content
    EditProfileContent(
        state = state,
        onEvent = viewModel::setEvent
    )
}
