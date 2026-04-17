package com.example.paceapp.features.authentication.login.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.paceapp.core.components.AppBaseScreen
import com.example.paceapp.features.authentication.login.LoginContract.State
import com.example.paceapp.features.authentication.login.LoginContract.Event

@Composable
internal fun LoginContent(
    state: State,
    onEvent: (Event) -> Unit
) {
    AppBaseScreen(
        modifier = Modifier.fillMaxSize(),
        isLoading = state.isLoading,
        hasPattern = true,
    ) {

    }
}
