package net.paceapp.features.main.createevent.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import net.paceapp.R
import net.paceapp.core.components.AppBaseScreen
import net.paceapp.core.components.AppButton
import net.paceapp.core.components.AppButtonStyle
import net.paceapp.core.components.CommonAppBar
import net.paceapp.core.components.animation.horizontalStepTransition
import net.paceapp.core.extensions.clearFocusOnTap
import net.paceapp.features.main.createevent.CreateEventContract.Event
import net.paceapp.features.main.createevent.CreateEventContract.State
import net.paceapp.features.main.createevent.enums.CreateRunStep
import net.paceapp.features.main.createevent.extensions.nextButtonRes
import net.paceapp.features.main.createevent.steps.EventDetailsContent
import net.paceapp.theme.AppColors
import net.paceapp.theme.AppTheme

@Composable
internal fun CreateEventContent(
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
                title = stringResource(R.string.new_event),
                onBackClick = {
                    onEvent(Event.OnBackClick)
                },
            )
        },
    ) { innerPaddings ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPaddings)
                .padding(AppTheme.screenPadding)
        ) {
            AnimatedContent(
                modifier = Modifier
                    .weight(1f),
                targetState = state.currentStep,
                transitionSpec = horizontalStepTransition { initial, target ->
                    initial.ordinal < target.ordinal
                },
                label = "StepTransition"
            ) { step ->
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    when (step) {
                        CreateRunStep.EventDetails -> EventDetailsContent(
                            eventNameState = state.eventNameState,
                            locationState = state.locationState,
                            selectedDate = state.selectedDate
                        ) { millis ->
                            onEvent(Event.OnDateSelected(millis))
                        }

                        CreateRunStep.Distance -> DefaultView()
                        CreateRunStep.GoalTime -> DefaultView()
                        CreateRunStep.SegmentChoice -> DefaultView()
                        CreateRunStep.SegmentCount -> DefaultView()
                        CreateRunStep.SegmentDetails -> DefaultView()
                        CreateRunStep.LookBackIntervals -> DefaultView()
                    }
                }
            }

            //Next button
            AppButton(
                title = stringResource(state.currentStep.nextButtonRes),
                onClick = {
                    onEvent(Event.OnNextButtonClick)
                },
                style = AppButtonStyle.FILLED_GRADIENT,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
fun DefaultView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.NeonAquaBlue)
    ) { }
}
