package com.example.paceapp.features.authentication.buildprofile.components

import android.content.Context
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.paceapp.R
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
import com.example.paceapp.features.authentication.buildprofile.steps.PairWatchContent
import com.example.paceapp.features.authentication.buildprofile.steps.PairWatchSuccessContent
import com.example.paceapp.features.authentication.buildprofile.steps.SelectModelContent
import com.example.paceapp.theme.AppColors
import com.example.paceapp.theme.AppTheme
import com.wvelabs.core_ui.alerts.AlertType
import com.wvelabs.core_ui.alerts.DefaultDialog
import com.wvelabs.core_ui.alerts.MessageType

@Composable
internal fun BuildProfileContent(
    state: State,
    onEvent: (Event) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val context: Context = LocalContext.current

    //Display garmin sdk alert
    if (state.showGarminSetupDialog) {
        DefaultDialog(
            alert = AlertType.Dialog(
                title = stringResource(R.string.garmin_sdk_title),
                text = stringResource(R.string.garmin_sdk_message),
                confirmText = stringResource(R.string.try_again),
                dismissText = stringResource(R.string.skip),
                cancelable = false,
                type = MessageType.Warning, // Or whatever type matches your design!
                // Safe lambdas! No memory leaks here because we are in the Compose scope
                onConfirm = { onEvent(Event.OnGarminDialogRetry(context)) },
                onDismiss = { onEvent(Event.OnGarminDialogSkip) }
            ),
        )
    }

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
                            text = stringResource(R.string.skip),
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


        //Column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScrollOnIme()
                .padding(innerPaddings)
                .padding(horizontal = 16.dp)

        ) {
            //Scrollable Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)

                    .padding(top = 16.dp)
            ) {

                //Step content with animation
                AnimatedContent(
                    modifier = Modifier.fillMaxSize(),
                    targetState = state.currentStep,
                    transitionSpec = {
                        val isMovingForward = targetState.stepOrder > initialState.stepOrder

                        if (isMovingForward) {
                            (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                                slideOutHorizontally { width -> -width } + fadeOut()
                            )
                        } else {
                            (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                                slideOutHorizontally { width -> width } + fadeOut()
                            )
                        }.using(SizeTransform(clip = false))
                    },
                    label = "StepTransition"
                ) { step ->
                    Box(modifier = Modifier.fillMaxSize()) {
                        when (step) {
                            ProfileStep.AccountSetup -> AccountSetContent(
                                profileImage = state.profileImage,
                                firstNameState = state.firstNameState,
                                lastNameState = state.lastNameState,
                                onEvent = onEvent,
                            )

                            ProfileStep.PairWatchInit -> PairWatchContent()
                            ProfileStep.SelectModel -> SelectModelContent(
                                watchList = state.watchList,
                                selectedWatch = state.selectedWatch,
                                onModelTap = { onEvent(Event.SelectWatchModel(device = it)) }
                            )

                            ProfileStep.PairWatchSuccess -> PairWatchSuccessContent(state.selectedWatch)
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
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                onClick = {
                    onEvent(Event.OnNextClick(context = context))
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
