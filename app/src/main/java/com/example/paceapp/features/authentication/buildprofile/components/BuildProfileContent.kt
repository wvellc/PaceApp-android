package com.example.paceapp.features.authentication.buildprofile.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.paceapp.core.components.AppBaseScreen
import com.example.paceapp.core.components.AppButton
import com.example.paceapp.core.components.AppTextButton
import com.example.paceapp.core.components.CommonAppBar
import com.example.paceapp.core.extensions.clearFocusOnTap
import com.example.paceapp.core.extensions.verticalScrollOnIme
import com.example.paceapp.features.authentication.buildprofile.BuildProfileContract.Event
import com.example.paceapp.features.authentication.buildprofile.BuildProfileContract.State
import com.example.paceapp.features.authentication.buildprofile.domain.ProfileStep
import com.example.paceapp.features.authentication.buildprofile.steps.AccountSetContent
import com.example.paceapp.theme.AppColors
import com.example.paceapp.theme.AppTheme

@Composable
internal fun BuildProfileContent(
    state: State,
    onEvent: (Event) -> Unit
) {
    val focusManager = LocalFocusManager.current
    //Screen
    AppBaseScreen(
        modifier = Modifier
            .fillMaxSize()
            .clearFocusOnTap(focusManager),
        isLoading = state.isLoading,
        hasPattern = true,
        appBar = {
            //App bar
            CommonAppBar(
                title = stringResource(state.currentStep.titleRes),
                onBackClick = {
                    focusManager.clearFocus(force = true)
                    onEvent(Event.OnBackClick)
                },

                actions = {
                    //Skip action
                    if (state.currentStep.isSkippable) {
                        AppTextButton(
                            text = "Skip",
                            style = AppTheme.typography.size16.copy(
                                fontWeight = FontWeight.Medium,
                                color = AppColors.White,
                                fontSize = 17.sp
                            ),
                            onClick = { onEvent(Event.OnSkipClick) },
                        )
                    }
                }
            )
        },
    ) { innerPaddings ->

        val outerScroll = rememberScrollState()
        //Scroll inner content on Ime
        val innerScroll = rememberScrollState()
        //Column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScrollOnIme(outerScroll)
                .padding(innerPaddings)
                .padding(horizontal = 16.dp)

        ) {
            //Scrollable Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScrollOnIme(
                        innerScroll, delayMs = 200 //delay to sync with outer scroll
                    )
                    .padding(top = 16.dp)
            ) {

                //Step content with animation
                AnimatedContent(
                    modifier = Modifier,
                    targetState = state.currentStep,
//                    transitionSpec = {
//                        fadeIn(animationSpec = tween(500)) togetherWith
//                                fadeOut(animationSpec = snap()) using SizeTransform(clip = false)
//
//                        val isForward = targetState.ordinal > initialState.ordinal
//
//                        if (isForward) {
//                            slideInHorizontally { it } + fadeIn() togetherWith
//                                    slideOutHorizontally { -it } + fadeOut()
//                        } else {
//                            slideInHorizontally { -it } + fadeIn() togetherWith
//                                    slideOutHorizontally { it } + fadeOut()
//                        }.using(SizeTransform(clip = false))
//                    },
                    label = "StepTransition"
                ) { step ->
                    Box(modifier = Modifier) {
                        when (step) {
                            ProfileStep.AccountSetup -> AccountSetContent(
                                profileImage = state.profileImage,
                                firstNameState = state.firstNameState,
                                lastNameState = state.lastNameState,
                                onEvent = onEvent,
                            )

                            ProfileStep.PairWatchInit -> DefaultContent(step)
                            ProfileStep.SelectModel -> DefaultContent(step)
                            ProfileStep.PairSelectedWatch -> DefaultContent(step)
                            ProfileStep.SetGait -> DefaultContent(step)
                            ProfileStep.ConnectStrava -> DefaultContent(step)
                        }
                    }
                }
            }
            //Next button
            AppButton(
                enabled = state.isNextButtonEnabled,
                title = stringResource(state.currentStep.buttonLabelRes),
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = {
                    onEvent(Event.OnNextClick)
                }
            )
        }
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
