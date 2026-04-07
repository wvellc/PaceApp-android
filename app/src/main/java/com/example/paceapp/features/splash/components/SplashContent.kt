package com.example.paceapp.features.splash.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.paceapp.core.components.AppBaseScreen
import com.example.paceapp.features.splash.SplashContract
import com.example.paceapp.features.splash.SplashContract.Event
import com.example.paceapp.features.splash.SplashContract.State

@Composable
internal fun SplashContent(
    state: State,
    onEvent: (Event) -> Unit
) {
    AppBaseScreen(
        modifier = Modifier.fillMaxSize(),
        isLoading = state.isLoading,
    ) {
        Text(text = "Splash Screen")
    }
}

@Preview
@Composable
fun SplashPreview() = SplashContent(state = SplashContract.State(), onEvent = {})