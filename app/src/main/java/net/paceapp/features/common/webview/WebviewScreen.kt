package net.paceapp.features.common.webview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest
import net.paceapp.features.common.webview.components.WebviewContent

@Composable
fun WebviewScreen(
    viewModel: WebviewViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Handle one-time effects
    LaunchedEffect(key1 = Unit) {
        viewModel.effect.collect { effect ->
            // when (effect) { ... }
        }
    }

    // Render content
    WebviewContent(
        state = state,
        onEvent = viewModel::setEvent
    )
}
