package com.example.paceapp.features.main.updategait.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.paceapp.R
import com.example.paceapp.core.components.AppBaseScreen
import com.example.paceapp.core.components.AppButton
import com.example.paceapp.core.components.AppGaitContent
import com.example.paceapp.core.components.CommonAppBar
import com.example.paceapp.features.main.updategait.UpdateGaitContract.Event
import com.example.paceapp.features.main.updategait.UpdateGaitContract.State
import com.example.paceapp.theme.AppTheme

@Composable
internal fun UpdateGaitContent(
    state: State,
    onEvent: (Event) -> Unit
) {

    //Screen
    AppBaseScreen(
        modifier = Modifier
            .fillMaxSize(),
        isLoading = state.isLoading,
        hasPattern = true,
        appBar = {
            //App bar
            CommonAppBar(
                title = stringResource(R.string.profile_step_set_gait_title),
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
            //Gait content
            AppGaitContent(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                runningGait = state.runningGait,
                onRunningChange = { onEvent(Event.OnRunningGaitChanged(it)) },
                walkingGait = state.walkingGait,
                onWalkingChange = { onEvent(Event.OnWalkingGaitChanged(it)) }
            )

            //Close
            AppButton(
                title = stringResource(R.string.close),
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onEvent(Event.OnBackClick)
                }
            )
        }


    }
}
