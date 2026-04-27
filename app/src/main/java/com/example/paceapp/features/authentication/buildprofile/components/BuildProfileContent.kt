package com.example.paceapp.features.authentication.buildprofile.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.paceapp.core.components.AppBaseScreen
import com.example.paceapp.core.components.AppSuccessView
import com.example.paceapp.core.components.CommonAppBar
import com.example.paceapp.core.components.animation.FadeInUpWrapper
import com.example.paceapp.core.extensions.clearFocusOnTap

import com.example.paceapp.features.authentication.buildprofile.BuildProfileContract.State
import com.example.paceapp.features.authentication.buildprofile.BuildProfileContract.Event
import com.wvelabs.core_ui.extensions.fadeInUpTransition

@Composable
internal fun BuildProfileContent(
    state: State,
    onEvent: (Event) -> Unit
) {
    AppBaseScreen(
        modifier = Modifier
            .fillMaxSize(),
        isLoading = state.isLoading,
        hasPattern = true,
        appBar = { CommonAppBar(title = "Create Account") },
        animationWrapper = { content -> FadeInUpWrapper(enterTransition = fadeInUpTransition(from = 0.02f)) { content() } }
    ) {
        Text(text = "BuildProfile Screen")
    }
}
