package net.paceapp.core.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDatePickerDialog(
    initialSelectedDateMillis: Long? = null,
    selectableDates: SelectableDates = object : SelectableDates {},
    confirmButtonText: String = "OK",
    dismissButtonText: String = "Cancel",
    onDismissRequest: () -> Unit,
    onDateSelected: (Long?) -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialSelectedDateMillis,
        selectableDates = selectableDates
    )
    val dialogProperties = DialogProperties(
        dismissOnBackPress = true,
        dismissOnClickOutside = true,
        decorFitsSystemWindows = true,
        usePlatformDefaultWidth = false
    )

    DatePickerDialog(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 16.dp),
        onDismissRequest = onDismissRequest,
        properties = dialogProperties,
        confirmButton = {
            AppTextButton(
                text = confirmButtonText,
                onClick = {
                    onDateSelected(datePickerState.selectedDateMillis)
                    onDismissRequest()
                }
            )
        },
        dismissButton = {
            AppTextButton(
                text = dismissButtonText,
                onClick = onDismissRequest
            )
        }
    ) {
        DatePicker(state = datePickerState)
    }
}