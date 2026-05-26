package net.paceapp.features.main.createevent.components

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyant.capsule.ContinuousRoundedRectangle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.paceapp.R
import net.paceapp.core.components.AppBaseScreen
import net.paceapp.core.components.AppButton
import net.paceapp.core.components.AppButtonStyle
import net.paceapp.core.components.AppDurationPicker
import net.paceapp.core.components.CommonAppBar
import net.paceapp.core.components.animation.horizontalStepTransition
import net.paceapp.core.extensions.clearFocusOnTap
import net.paceapp.core.extensions.verticalScrollOnIme
import net.paceapp.features.main.createevent.CreateEventContract.Event
import net.paceapp.features.main.createevent.CreateEventContract.State
import net.paceapp.features.main.createevent.enums.CreateRunStep
import net.paceapp.features.main.createevent.extensions.nextButtonRes
import net.paceapp.features.main.createevent.extensions.titleRes
import net.paceapp.features.main.createevent.steps.EventDetailsContent
import net.paceapp.theme.AppColors
import net.paceapp.theme.AppTheme

@Composable
internal fun CreateEventContent(
    state: State,
    onEvent: (Event) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
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
                    coroutineScope.launch {
                        focusManager.clearFocus(force = true)
                        delay(200)
                        onEvent(Event.OnBackClick)
                    }

                },
            )
        },
    ) { innerPaddings ->
        val onDurationChange: (Long) -> Unit = { newValue ->
            onEvent(Event.OnDurationUpdated(newValue))
        }
        val initialDurationSeconds = state.goalTimeInSeconds
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScrollOnIme()
                .padding(top = innerPaddings.calculateTopPadding())
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
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    when (step) {
                        CreateRunStep.EventDetails -> EventDetailsContent(
                            eventNameState = state.eventNameState,
                            locationState = state.locationState,
                            selectedDate = state.selectedDate
                        ) { millis ->
                            onEvent(Event.OnDateSelected(millis))
                        }

                        CreateRunStep.Distance -> DistanceContent(
                            titleRes = step.titleRes,
                            distance = state.selectedDistance,
                            onDistanceChanged = { onEvent(Event.OnDistanceUpdated(it)) }
                        )

                        CreateRunStep.GoalTime -> GoalTimeContent(
                            titleRes = step.titleRes,
                            duration = initialDurationSeconds,
                            onDurationChange = onDurationChange
                        )

                        CreateRunStep.SegmentChoice -> EventCardContainer()
                        CreateRunStep.SegmentCount -> EventCardContainer()
                        CreateRunStep.SegmentDetails -> EventCardContainer()
                        CreateRunStep.LookBackIntervals -> EventCardContainer()
                    }
                }
            }


            //Next button
            AppButton(
                title = stringResource(state.currentStep.nextButtonRes),
                onClick = {
                    coroutineScope.launch {
                        focusManager.clearFocus(force = true)
                        delay(200)
                        onEvent(Event.OnNextButtonClick)
                    }
                },
                style = AppButtonStyle.FILLED_GRADIENT,
                modifier = Modifier
                    .fillMaxWidth()
                    .safeContentPadding()

            )
        }
    }
}




