package com.example.paceapp.features.main.createevent.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.example.paceapp.R
import com.example.paceapp.core.components.AppBaseScreen
import com.example.paceapp.core.components.AppButton
import com.example.paceapp.core.components.AppButtonStyle
import com.example.paceapp.core.components.AppDatePickerDialog
import com.example.paceapp.core.components.AppTextField
import com.example.paceapp.core.components.CommonAppBar
import com.example.paceapp.core.components.DatePickerField
import com.example.paceapp.core.components.ValidatorType
import com.example.paceapp.core.components.animation.horizontalStepTransition
import com.example.paceapp.core.components.rememberPastOnlySelectableDates
import com.example.paceapp.core.extensions.clearFocusOnTap
import com.example.paceapp.features.main.createevent.CreateEventContract.Event
import com.example.paceapp.features.main.createevent.CreateEventContract.State
import com.example.paceapp.features.main.createevent.enums.CreateRunStep
import com.example.paceapp.features.main.createevent.extensions.nextButtonRes
import com.example.paceapp.theme.AppColors
import com.example.paceapp.theme.AppTheme
import com.wvelabs.core_ui.utils.AppDateFormat
import com.wvelabs.core_ui.utils.DateTimeHelper
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

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
                .padding(horizontal = AppTheme.screenPadding)
        ) {
            AnimatedContent(
                modifier = Modifier.fillMaxSize(),
                targetState = state.currentStep,
                transitionSpec = horizontalStepTransition { initial, target ->
                    initial.ordinal < target.ordinal
                },
                label = "StepTransition"
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    DateTimeHelper
                    when (it) {
                        CreateRunStep.EventDetails -> EventDetailsContent(
                            eventNameState = state.eventNameState,
                            locationState = state.locationState,
                            formattedDate = state.selectedDate?.let {
                                DateTimeHelper.formatDateTime(it, toFormat = AppDateFormat.DATE_DMY)
                            } ?: "",

                            // Convert back to UTC Milliseconds for the DatePicker
                            selectedDateMillis = state.selectedDate
                                ?.toInstant(TimeZone.UTC)
                                ?.toEpochMilliseconds()
                        ) { millis ->
                            onEvent(Event.OnDateSelected(millis))
                        }

                        CreateRunStep.Distance -> TODO()
                        CreateRunStep.GoalTime -> TODO()
                        CreateRunStep.SegmentChoice -> TODO()
                        CreateRunStep.SegmentCount -> TODO()
                        CreateRunStep.SegmentDetails -> TODO()
                        CreateRunStep.LookBackIntervals -> TODO()
                    }
                }
            }

            //Next button
            AppButton(
                title = stringResource(state.currentStep.nextButtonRes),
                onClick = {
                },
                style = AppButtonStyle.FILLED_GRADIENT,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun EventDetailsContent(
    eventNameState: TextFieldState,
    locationState: TextFieldState,
    formattedDate: String?,
    selectedDateMillis: Long?,
    onDateSelected: (Long?) -> Unit,
) {
    val eventNameFocus = remember { FocusRequester() }
    val locationFocus = remember { FocusRequester() }
    var showDatePicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        //Event name
        AppTextField(
            state = eventNameState,
            hint = stringResource(R.string.first_name),
            borderColor = AppColors.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 42.dp),
            leadingIcon = { },
            validatorType = ValidatorType.Name,
            imeAction = ImeAction.Next,
            showErrorMessage = true,
            unfocusedBorderWidth = 1.2.dp,
            focusedBorderWidth = 1.2.dp,
            capitalization = KeyboardCapitalization.Words,
            focusRequester = eventNameFocus,
        )


        //Location
        AppTextField(
            state = locationState,
            hint = stringResource(R.string.last_name),
            leadingIcon = { },
            borderColor = AppColors.White,
            modifier = Modifier.fillMaxWidth(),
            validatorType = ValidatorType.Name,
            imeAction = ImeAction.Done,
            showErrorMessage = true,
            unfocusedBorderWidth = 1.2.dp,
            focusedBorderWidth = 1.2.dp,
            capitalization = KeyboardCapitalization.Words,
            focusRequester = locationFocus,
        )


        //Date picker field
        DatePickerField(
            modifier = Modifier.fillMaxWidth(),
            value = formattedDate,
            onClick = { showDatePicker = true }
        )

        // --- Date Picker Dialog ---
        if (showDatePicker) {
            AppDatePickerDialog(
                initialSelectedDateMillis = selectedDateMillis,
                onDismissRequest = { showDatePicker = false },
                selectableDates = rememberPastOnlySelectableDates(),
                onDateSelected = onDateSelected
            )
        }
    }
}
