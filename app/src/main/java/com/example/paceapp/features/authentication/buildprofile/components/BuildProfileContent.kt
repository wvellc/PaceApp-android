package com.example.paceapp.features.authentication.buildprofile.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.paceapp.core.components.AppBaseScreen
import com.example.paceapp.core.components.AppButton
import com.example.paceapp.core.components.CommonAppBar
import com.example.paceapp.core.components.animation.FadeInUpWrapper
import com.example.paceapp.core.extensions.clearFocusOnTap
import com.example.paceapp.core.extensions.verticalScrollOnIme
import com.example.paceapp.features.authentication.buildprofile.BuildProfileContract.Event
import com.example.paceapp.features.authentication.buildprofile.BuildProfileContract.State
import com.example.paceapp.features.authentication.buildprofile.domain.ProfileStep
import com.example.paceapp.features.authentication.buildprofile.steps.AccountSetContent
import com.example.paceapp.theme.AppColors
import com.example.paceapp.theme.AppTheme
import com.wvelabs.core_ui.extensions.fadeInUpTransition

@Composable
internal fun BuildProfileContent(
    state: State,
    onEvent: (Event) -> Unit
) {
    val focusManager = LocalFocusManager.current

    AppBaseScreen(
        modifier = Modifier
            .fillMaxSize()
            .clearFocusOnTap(focusManager),
        isLoading = state.isLoading,
        hasPattern = true,
        appBar = {
            CommonAppBar(
                title = stringResource(state.currentStep.titleRes),
                onBackClick = {
                    focusManager.clearFocus(force = true)
                    onEvent(Event.OnBackClick)
                },
            )
        },
        animationWrapper = { content -> FadeInUpWrapper(enterTransition = fadeInUpTransition(from = 0.02f)) { content() } }
    ) { innerPaddings ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScrollOnIme()
                .padding(innerPaddings)
                .padding(16.dp),
        ) {
            AnimatedContent(
                modifier = Modifier.weight(1f),
                targetState = state.currentStep,
                transitionSpec = {
                    slideInHorizontally { it } + fadeIn() togetherWith
                            slideOutHorizontally { -it } + fadeOut()
                },
                label = "StepTransition"
            ) { step ->
                Box(modifier = Modifier) {
                    StepContent(
                        step = step,
                    )
                }
            }
            AppButton(
                enabled = state.isNextButtonEnabled,
                title = stringResource(state.currentStep.buttonLabelRes),
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onEvent(Event.OnNextClick)
                }
            )
        }
    }
}

@Composable
fun StepContent(step: ProfileStep) {

    when (step) {
        ProfileStep.AccountSetup -> AccountSetContent()
        ProfileStep.PairWatchInit -> DefaultContent(step)
        ProfileStep.SelectModel -> DefaultContent(step)
        ProfileStep.PairSelectedWatch -> DefaultContent(step)
        ProfileStep.SetGait -> DefaultContent(step)
        ProfileStep.ConnectStrava -> DefaultContent(step)
    }
}


@Composable
private fun DefaultContent(step: ProfileStep) {
    Text(
        text = stringResource(step.titleRes),
        style = AppTheme.typography.size26.copy(
            color = AppColors.White,
            fontWeight = FontWeight.SemiBold
        ),
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp)
            .background(AppColors.NeonAquaBlue)
    )
}
