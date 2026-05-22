package net.paceapp.features.main.managewatch.components

import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import net.paceapp.core.components.AppBaseScreen
import net.paceapp.core.components.AppButton
import net.paceapp.core.components.CommonAppBar
import net.paceapp.core.components.profilesteps.PairWatchInitContent
import net.paceapp.core.components.profilesteps.PairWatchSuccessContent
import net.paceapp.core.components.profilesteps.SelectModelContent
import net.paceapp.core.extensions.clearFocusOnTap
import net.paceapp.features.main.managewatch.ManageWatchContract.Event
import net.paceapp.features.main.managewatch.ManageWatchContract.State
import net.paceapp.features.main.managewatch.models.ManageWatchStep
import net.paceapp.theme.AppTheme

@Composable
internal fun ManageWatchContent(
    state: State,
    onEvent: (Event) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val context: Context = LocalContext.current
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
                    onEvent(Event.OnBackClick)
                },
            )
        },
    ) { innerPaddings ->

        //Column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPaddings)
                .padding(horizontal = AppTheme.screenPadding)

        ) {
            // Step content animation
            AnimatedContent(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                targetState = state.currentStep,
                transitionSpec = {
                    // Initially no animation
                    if (initialState is ManageWatchStep.PairWatchInit && targetState is ManageWatchStep.PairWatchSuccess) {
                        EnterTransition.None togetherWith ExitTransition.None
                    } else if (state.isMovingForward) {
                        (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> -width } + fadeOut()
                        ).using(SizeTransform(clip = false))
                    }
                    // User pressed back
                    else {
                        (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> width } + fadeOut()
                        ).using(SizeTransform(clip = false))
                    }
                },
                label = "StepTransition"
            ) { step ->
                // Your step content goes here...
                Box(modifier = Modifier.fillMaxSize()) {
                    when (step) {
                        ManageWatchStep.PairWatchInit -> PairWatchInitContent(
                            modifier = Modifier
                                .wrapContentSize()
                                .padding(AppTheme.screenPadding),
                        )

                        ManageWatchStep.PairWatchSuccess -> PairWatchSuccessContent(
                            device = state.selectedWatch
                        )

                        ManageWatchStep.SelectModel -> SelectModelContent(
                            watchList = state.watchList,
                            selectedWatch = state.selectedWatch,
                            onModelTap = {
                                onEvent(Event.OnWatchSelected(it))
                            },
                        )
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
                    onEvent(Event.OnNextButtonClick(context = context))
                }
            )
        }
    }
}
