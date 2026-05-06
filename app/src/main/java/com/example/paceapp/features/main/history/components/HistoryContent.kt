package com.example.paceapp.features.main.history.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.paceapp.core.components.AppBaseScreen

import com.example.paceapp.features.main.history.HistoryContract.State
import com.example.paceapp.features.main.history.HistoryContract.Event

@Composable
internal fun HistoryContent(
    state: State,
    onEvent: (Event) -> Unit
) {
    AppBaseScreen(
        modifier = Modifier
            .fillMaxSize(),
        isLoading = state.isLoading,
        hasPattern = true,
    ) {innerPaddings->
        Text(text = "History Screen")
    }
}
