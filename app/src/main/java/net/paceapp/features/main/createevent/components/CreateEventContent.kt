package net.paceapp.features.main.createevent.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import net.paceapp.R
import net.paceapp.core.components.AppBaseScreen
import net.paceapp.core.components.AppButton
import net.paceapp.core.components.AppButtonStyle
import net.paceapp.core.components.AppDatePickerDialog
import net.paceapp.core.components.AppTextField
import net.paceapp.core.components.CommonAppBar
import net.paceapp.core.components.DatePickerField
import net.paceapp.core.components.ValidatorType
import net.paceapp.core.components.animation.horizontalStepTransition
import net.paceapp.core.components.rememberPastOnlySelectableDates
import net.paceapp.core.extensions.clearFocusOnTap
import net.paceapp.features.main.createevent.CreateEventContract.Event
import net.paceapp.features.main.createevent.CreateEventContract.State
import net.paceapp.features.main.createevent.enums.CreateRunStep
import net.paceapp.features.main.createevent.extensions.nextButtonRes
import net.paceapp.theme.AppColors
import net.paceapp.theme.AppTheme
import com.wvelabs.core_ui.alerts.IconPosition
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
                },
                style = AppButtonStyle.FILLED_GRADIENT,
                modifier = Modifier.fillMaxWidth()
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 42.dp),
            leadingIcon = {
                Image(
                    painter = painterResource(R.drawable.ic_calender),
                    contentDescription = "Calendar",
                )
            },
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
            leadingIcon = {
                Image(
                    painter = painterResource(R.drawable.ic_calender),
                    contentDescription = "Calendar",
                )
            },
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
            borderColor = AppColors.HintGray,
            iconColor = AppColors.White,
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
