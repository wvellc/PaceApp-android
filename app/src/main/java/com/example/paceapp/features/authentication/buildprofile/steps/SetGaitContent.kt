package com.example.paceapp.features.authentication.buildprofile.steps

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.paceapp.core.components.AppGaitContent
import com.example.paceapp.core.domain.models.GaitPace

@Composable
fun SetGaitContent(
    walkingGait: GaitPace,
    runningGait: GaitPace,
    onWalkingChange: (pace: GaitPace) -> Unit,
    onRunningChange: (pace: GaitPace) -> Unit
) {
    AppGaitContent(
        modifier = Modifier
            .fillMaxSize(),
        runningGait = runningGait,
        onRunningChange = onRunningChange,
        walkingGait = walkingGait,
        onWalkingChange = onWalkingChange
    )
}
