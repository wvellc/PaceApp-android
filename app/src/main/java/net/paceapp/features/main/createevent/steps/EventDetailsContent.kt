package net.paceapp.features.main.createevent.steps

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.wvelabs.core_ui.utils.AppDateFormat
import com.wvelabs.core_ui.utils.DateTimeHelper
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import net.paceapp.R
import net.paceapp.core.components.AppDatePickerDialog
import net.paceapp.core.components.AppTextField
import net.paceapp.core.components.DatePickerField
import net.paceapp.core.components.IconAlignment
import net.paceapp.core.components.ValidatorType
import net.paceapp.core.components.rememberFutureOnlySelectableDates
import net.paceapp.core.extensions.verticalScrollOnIme
import net.paceapp.theme.AppColors

@Composable
fun EventDetailsContent(
    eventNameState: TextFieldState,
    locationState: TextFieldState,
    selectedDate: LocalDateTime?,
    onDateSelected: (Long?) -> Unit,
) {
    val eventNameFocus = remember { FocusRequester() }
    val locationFocus = remember { FocusRequester() }
    var showDatePicker by remember { mutableStateOf(false) }

    val formattedDate = remember(selectedDate) {
        selectedDate?.let { dateTime ->
            DateTimeHelper.formatDateTime(
                date = dateTime,
                toFormat = AppDateFormat.DATE_DMY,
                isUtc = false
            )
        } ?: ""
    }

    val selectedDateMillis = remember(selectedDate) {
        selectedDate
            ?.toInstant(TimeZone.UTC)
            ?.toEpochMilliseconds()
    }
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScrollOnIme(scrollState, 200),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        //Event name
        AppTextField(
            state = eventNameState,
            hint = stringResource(R.string.event_name),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            leadingIcon = {
                Image(
                    painter = painterResource(R.drawable.ic_event_name),
                    contentDescription = null,
                )
            },
            validatorType = ValidatorType.Name,
            hintTextColor = AppColors.FashionGray,
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
            hint = stringResource(R.string.location),
            errorMessageRes = R.string.location_field_error,
            leadingIcon = {
                Image(
                    painter = painterResource(R.drawable.ic_location),
                    contentDescription = null,
                )
            },
            modifier = Modifier.fillMaxWidth(),
            validatorType = ValidatorType.Name,
            imeAction = ImeAction.Done,
            hintTextColor = AppColors.FashionGray,
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
            iconColor = AppColors.White,
            textColor = AppColors.White,
            iconAlignment = IconAlignment.Start,
            borderColor = AppColors.HintGray,
            onClick = { showDatePicker = true }
        )

        // --- Date Picker Dialog ---
        if (showDatePicker) {
            AppDatePickerDialog(
                initialSelectedDateMillis = selectedDateMillis,
                onDismissRequest = { showDatePicker = false },
                selectableDates = rememberFutureOnlySelectableDates(),
                onDateSelected = onDateSelected
            )
        }
    }
}
