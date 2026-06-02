package net.paceapp.features.main.createevent.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.paceapp.R
import net.paceapp.core.components.AppBaseScreen
import net.paceapp.core.components.AppButton
import net.paceapp.core.components.CommonAppBar
import net.paceapp.core.components.animation.horizontalStepTransition
import net.paceapp.core.extensions.clearFocusOnTap
import net.paceapp.core.extensions.verticalScrollOnIme
import net.paceapp.features.main.createevent.CreateEventContract.Event
import net.paceapp.features.main.createevent.CreateEventContract.State
import net.paceapp.features.main.createevent.enums.CreateRunStep
import net.paceapp.features.main.createevent.extensions.nextButtonRes
import net.paceapp.features.main.createevent.extensions.titleRes
import net.paceapp.features.main.createevent.steps.DistanceStep
import net.paceapp.core.components.eventsteps.EventDetailsStep
import net.paceapp.features.main.createevent.steps.GoalTimeStep
import net.paceapp.features.main.createevent.steps.LookBackIntervalStep
import net.paceapp.features.main.createevent.steps.SegmentChoiceStep
import net.paceapp.features.main.createevent.steps.SegmentCountStep
import net.paceapp.features.main.createevent.steps.SegmentDetailsStep
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
        //Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScrollOnIme()
                .padding(top = innerPaddings.calculateTopPadding())
                .padding(AppTheme.screenPadding),
            verticalArrangement = Arrangement.Top
        ) {
            //Animated steps
            AnimatedContent(
                modifier = Modifier.weight(1f),
                targetState = state.currentStep,
                transitionSpec = horizontalStepTransition { initial, target ->
                    initial.ordinal < target.ordinal
                },
                label = "StepTransition",
            ) { step ->
                //Step container
                Box(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    //Step content
                    when (step) {
                        CreateRunStep.EventDetails -> EventDetailsStep(
                            eventNameState = state.eventNameState,
                            locationState = state.locationState,
                            selectedDate = state.selectedDate
                        ) { millis ->
                            onEvent(Event.OnDateSelected(millis))
                        }

                        CreateRunStep.Distance -> DistanceStep(
                            titleRes = step.titleRes,
                            distance = state.selectedDistance,
                            onDistanceChanged = {
                                onEvent(Event.OnDistanceUpdated(it))
                            })

                        CreateRunStep.GoalTime -> GoalTimeStep(
                            titleRes = step.titleRes,
                            duration = state.goalTimeInSeconds,
                            onDurationChange = { newValue ->
                                onEvent(Event.OnDurationUpdated(newValue))
                            })

                        CreateRunStep.SegmentChoice -> SegmentChoiceStep(
                            titleRes = step.titleRes,
                            hasSegments = state.hasSegments,
                            onInfoClick = {
                                onEvent(Event.OnStepInfoClick)
                            },
                            onSegmentChoiceUpdated = {
                                onEvent(Event.OnSegmentChoiceUpdated(it))
                            })

                        CreateRunStep.SegmentCount -> SegmentCountStep(
                            titleRes = step.titleRes, count = state.segmentCount, onCountChange = {
                                onEvent(Event.OnSegmentCountChange(it.toInt()))
                            })

                        CreateRunStep.SegmentDetails -> AnimatedContent(
                            targetState = state.currentSegmentIndex,
                            transitionSpec = horizontalStepTransition { initial, target ->
                                initial < target
                            },
                            label = "SegmentIndexTransition"
                        ) { animatedIndex ->
                            val activeSegment = state.segmentList.getOrNull(animatedIndex)

                            if (activeSegment != null) {
                                SegmentDetailsStep(
                                    titleRes = step.titleRes,
                                    segmentError = state.segmentError,
                                    distanceUnitRes = state.selectedDistance.unit.titleRes,
                                    segment = activeSegment,
                                    onSegmentUpdated = {
                                        onEvent(Event.OnSegmentUpdated(it))
                                    },
                                )
                            }
                        }

                        CreateRunStep.LookBackIntervals -> LookBackIntervalStep(
                            titleRes = step.titleRes,
                            lookBackInterval = state.lookBackInterval,
                            maxInterval = state.selectedDistance.value.toInt(),
                            eventType = state.eventType,
                            onEventTypeUpdated = { onEvent(Event.OnEventTypeUpdated(it)) },
                            onIntervalUpdated = { onEvent(Event.OnLookBackIntervalUpdated(it)) },
                        )
                    }
                }
            }

            //Next button
            AppButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .systemBarsPadding(),
                title = stringResource(state.currentStep.nextButtonRes),
                onClick = {
                    coroutineScope.launch {
                        focusManager.clearFocus(force = true)
                        delay(200)
                        onEvent(Event.OnNextButtonClick)
                    }
                }

            )
        }
    }
}


