package com.example.paceapp.features.main.history.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.paceapp.R
import com.example.paceapp.core.components.AppButton
import com.example.paceapp.core.components.AppButtonStyle
import com.example.paceapp.core.components.AppDatePickerDialog
import com.example.paceapp.core.components.AppRangeSlider
import com.example.paceapp.core.components.AppTextField
import com.example.paceapp.core.components.DatePickerField
import com.example.paceapp.core.components.ValidatorType
import com.example.paceapp.core.extensions.clearFocusOnTap
import com.example.paceapp.core.extensions.defaultClickable
import com.example.paceapp.core.extensions.verticalScrollOnIme
import com.example.paceapp.features.main.history.models.HistoryFilterModel
import com.example.paceapp.theme.AppColors
import com.example.paceapp.theme.AppTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

val defaultDistanceRange = 0f..150f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    isVisible: Boolean,
    initialFilter: HistoryFilterModel? = null,
    backgroundColor: Color = AppColors.White,
    sheetShape: Shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
    overlayColor: Color = AppColors.NeonAquaBlue.copy(alpha = 0.4f),
    onDismiss: () -> Unit,
    onApplyFilter: (HistoryFilterModel?) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var isAnimating by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    // --- Initialize States ---
    val pastOnlyFilter = remember {
        object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis <= System.currentTimeMillis()
            }

            override fun isSelectableYear(year: Int): Boolean {
                return year <= Calendar.getInstance().get(Calendar.YEAR)
            }
        }
    }
    val locationTextState = remember { TextFieldState() }
    var sliderValue by remember { mutableStateOf(defaultDistanceRange) }
    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    // Sync states with selected filter data from ViewState
    LaunchedEffect(isVisible, initialFilter) {
        if (isVisible) {
            sliderValue = initialFilter?.distanceRange ?: defaultDistanceRange
            selectedDateMillis = initialFilter?.dateMillis
            locationTextState.setTextAndPlaceCursorAtEnd(initialFilter?.location ?: "")
        }
    }

    //Dismiss sheet with animation
    fun dismissSheet() {
        if (!isAnimating) {
            isAnimating = true
            scope.launch {
                focusManager.clearFocus(true)
                sheetState.hide()
                onDismiss()
                isAnimating = false
            }
        }
    }

    if (isVisible) {
        ModalBottomSheet(
            sheetState = sheetState,
            shape = sheetShape,
            containerColor = backgroundColor,
            scrimColor = overlayColor,
            onDismissRequest = { dismissSheet() },
            dragHandle = null,
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .clearFocusOnTap(focusManager)
        ) {
            FilterSheetContent(
                selectedDistanceRange = sliderValue,
                onDistanceChange = { sliderValue = it },
                locationTextState = locationTextState,
                selectedDateMillis = selectedDateMillis,
                onDateClick = { showDatePicker = true },
                onCloseClick = { dismissSheet() },
                onClearAllClick = {
                    onApplyFilter(null) // Send null to clear
                    dismissSheet()
                },
                onShowResultClick = {
                    val newFilter = HistoryFilterModel(
                        distanceRange = sliderValue,
                        dateMillis = selectedDateMillis,
                        location = locationTextState.text.toString()
                    )
                    onApplyFilter(newFilter)
                    dismissSheet()
                }
            )
        }

        // --- Date Picker Dialog ---
        if (showDatePicker) {
            AppDatePickerDialog(
                initialSelectedDateMillis = selectedDateMillis,
                onDismissRequest = { showDatePicker = false },
                selectableDates = pastOnlyFilter,
                onDateSelected = { dateMillis ->
                    selectedDateMillis = dateMillis
                }
            )
        }
    }
}

@Composable
private fun FilterSheetContent(
    selectedDistanceRange: ClosedFloatingPointRange<Float>,
    onDistanceChange: (ClosedFloatingPointRange<Float>) -> Unit,
    locationTextState: TextFieldState,
    selectedDateMillis: Long?,
    onDateClick: () -> Unit,
    onCloseClick: () -> Unit,
    onClearAllClick: () -> Unit,
    onShowResultClick: () -> Unit,
) {
    val locationFocus = remember { FocusRequester() }

    // Formatting the date for display
    val formattedDate = remember(selectedDateMillis) {
        selectedDateMillis?.let {
            SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(it))
        }
    }

    Column(
        modifier = Modifier
            .padding(AppTheme.screenPadding)
            .verticalScrollOnIme()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            //Title
            Text(
                modifier = Modifier.weight(1f),
                text = "Filter",
                style = AppTheme.typography.semiBold.copy(
                    color = AppColors.DarkCharcoal,
                    fontSize = 24.sp,
                )
            )
            //Close
            Image(
                modifier = Modifier
                    .clip(CircleShape)
                    .defaultClickable(onClick = onCloseClick),
                painter = painterResource(R.drawable.ic_close_gray),
                contentDescription = "Close"
            )
        }

        Spacer(Modifier.height(24.dp))

        //Distance by label
        Text(
            text = "Distance by",
            style = AppTheme.typography.semiBold.copy(
                color = AppColors.DarkCharcoal,
                fontSize = 20.sp,
            )
        )
        Spacer(Modifier.height(8.dp))
        //Range picker
        AppRangeSlider(
            value = selectedDistanceRange,
            onValueChange = onDistanceChange,
            valueRange = defaultDistanceRange,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(20.dp))

        //Date picker field
        DatePickerField(
            title = "Date",
            modifier = Modifier.fillMaxWidth(),
            value = formattedDate,
            onClick = onDateClick
        )

        Spacer(Modifier.height(24.dp))

        //Location field
        AppTextField(
            title = "Location",
            titleSpacing = 8.dp,
            titleStyle = AppTheme.typography.semiBold.copy(
                color = AppColors.DarkCharcoal,
                fontSize = 20.sp,
            ),
            modifier = Modifier.fillMaxWidth(),
            state = locationTextState,
            hint = "e.g. City",
            textStyle = AppTheme.typography.medium.copy(
                lineHeight = 24.sp,
                fontSize = 18.sp,
                color = AppColors.DarkCharcoal,
            ),
            hintTextColor = AppColors.FashionGray,
            borderColor = AppColors.FashionGray,
            validatorType = ValidatorType.None,
            imeAction = ImeAction.Done,
            showErrorMessage = false,
            unfocusedBorderWidth = 1.2.dp,
            focusedBorderWidth = 1.2.dp,
            capitalization = KeyboardCapitalization.Sentences,
            focusRequester = locationFocus,
        )

        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AppButton(
                title = "Clear All",
                onClick = onClearAllClick,
                backgroundColor = AppColors.HintGray,
                contentColor = AppColors.Error,
                style = AppButtonStyle.NONE,
                modifier = Modifier.weight(1f)
            )

            AppButton(
                title = "Show Result",
                onClick = onShowResultClick,
                style = AppButtonStyle.FILLED_GRADIENT,
                modifier = Modifier.weight(1f)
            )
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun FilterSheetPreview() = Box {
    FilterBottomSheet(
        isVisible = true,
        initialFilter = null,
        onDismiss = { },
        onApplyFilter = {},
    )
}