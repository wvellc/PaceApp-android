package net.paceapp.features.main.duplicateevent.components

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
import net.paceapp.core.components.eventsteps.EventDetailsStep
import net.paceapp.core.extensions.clearFocusOnTap
import net.paceapp.core.extensions.verticalScrollOnIme
import net.paceapp.features.main.duplicateevent.DuplicateEventContract.Event
import net.paceapp.features.main.duplicateevent.DuplicateEventContract.State
import net.paceapp.theme.AppTheme

@Composable
internal fun DuplicateEventContent(
    state: State,
    onEvent: (Event) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    AppBaseScreen(
        modifier = Modifier
            .fillMaxSize()
            .clearFocusOnTap(focusManager),
        isLoading = state.isLoading,
        hasPattern = true,
        appBar = {
            //App bar
            CommonAppBar(
                title = stringResource(R.string.edit_event),
                onBackClick = {
                    onEvent(Event.OnBackClick)
                },
            )
        },
    ) { innerPaddings ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScrollOnIme()
                .padding(top = innerPaddings.calculateTopPadding())
                .padding(AppTheme.screenPadding),
        ) {

            EventDetailsStep(
                modifier = Modifier.weight(1f),
                eventNameState = state.eventNameState,
                locationState = state.locationState,
                selectedDate = state.selectedDate,
                onDateSelected = {
                    onEvent(Event.OnDateSelected(it))
                },
                showDatePickerField = true,
            )


            //Save button
            AppButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .systemBarsPadding(),
                title = stringResource(R.string.save),
                onClick = {
                    coroutineScope.launch {
                        focusManager.clearFocus(force = true)
                        delay(200)
                        onEvent(Event.OnSaveButtonClick)
                    }
                }

            )
        }
    }
}
