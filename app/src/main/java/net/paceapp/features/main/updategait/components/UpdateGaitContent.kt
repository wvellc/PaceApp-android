package net.paceapp.features.main.updategait.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import net.paceapp.R
import net.paceapp.core.components.AppBaseScreen
import net.paceapp.core.components.AppButton
import net.paceapp.core.components.profilesteps.SetGaitContent
import net.paceapp.core.components.CommonAppBar
import net.paceapp.features.main.updategait.UpdateGaitContract.Event
import net.paceapp.features.main.updategait.UpdateGaitContract.State
import net.paceapp.theme.AppTheme

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
            SetGaitContent(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                runningGait = state.runningGait,
                onRunningChange = { onEvent(Event.OnRunningGaitChanged(it)) },
                walkingGait = state.walkingGait,
                onWalkingChange = { onEvent(Event.OnWalkingGaitChanged(it)) }
            )

            //Save (persists the gait on back — mirrors iOS UpdateGaitScreen "Save").
            AppButton(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(R.string.save),
                onClick = {
                    onEvent(Event.OnBackClick)
                }
            )
        }


    }
}
