package com.example.paceapp.features.main.analytics.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.paceapp.core.components.AppBaseScreen
import com.example.paceapp.features.main.analytics.AnalyticsContract.Event
import com.example.paceapp.features.main.analytics.AnalyticsContract.State

@Composable
internal fun AnalyticsContent(
    state: State,
    onEvent: (Event) -> Unit
) {
    AppBaseScreen(
        modifier = Modifier
            .fillMaxSize(),
        isLoading = state.isLoading,
        hasPattern = true,
    ) { innerPaddings ->
        Text(text = "Analytics Screen")
    }
}
