package com.example.paceapp.features.authentication.buildprofile.steps

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.paceapp.R
import com.example.paceapp.core.components.AppDigitPicker
import com.example.paceapp.core.components.AppSegmentedButtons
import com.example.paceapp.core.extensions.g2Continuity
import com.example.paceapp.features.authentication.buildprofile.domain.GaitPace
import com.example.paceapp.features.authentication.buildprofile.domain.GaitUnit
import com.example.paceapp.features.authentication.buildprofile.extensions.titleRes
import com.example.paceapp.theme.AppColors
import com.example.paceapp.theme.AppTheme
import com.kyant.capsule.ContinuousRoundedRectangle

@Composable
fun SetGaitContent(
    walkingGait: GaitPace,
    runningGait: GaitPace,
    onWalkingChange: (pace: GaitPace) -> Unit,
    onRunningChange: (pace: GaitPace) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.set_gait_step_description),
            style = AppTheme.typography.medium.copy(
                color = AppColors.White,
                fontSize = 20.sp,
                lineHeight = 32.sp
            )
        )
        Spacer(Modifier.height(16.dp))
        //Running gait picker
        GaitPickerView(
            modifier = Modifier.fillMaxWidth(),
            title = "Running",
            gait = runningGait,
            onGaitChanged = onRunningChange
        )
        Spacer(Modifier.height(24.dp))
        //Walking gait picker
        GaitPickerView(
            modifier = Modifier.fillMaxWidth(),
            title = "Walking",
            gait = walkingGait,
            onGaitChanged = onWalkingChange
        )

    }
}

@Composable
fun GaitPickerView(
    modifier: Modifier = Modifier,
    title: String,
    gait: GaitPace,
    onGaitChanged: (GaitPace) -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        //Title
        Text(
            text = title,
            style = AppTheme.typography.medium.copy(
                color = AppColors.White,
                fontSize = 16.sp,
            )
        )

        //PACE unit
        AppSegmentedButtons(
            modifier = Modifier
                .fillMaxWidth()
                .clip(ContinuousRoundedRectangle(12.dp, continuity = g2Continuity))
                .background(AppColors.White.copy(alpha = 0.1f))
                .padding(4.dp),
            segments = GaitUnit.entries,
            selectedSegment = gait.unit,
            itemTitle = { stringResource(it.titleRes) },
            onSegmentSelected = { newUnit ->
                onGaitChanged(gait.copy(unit = newUnit))
            },
        )

        //Digit picker
        AppDigitPicker(
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = AppColors.White,
                    shape = ContinuousRoundedRectangle(12.dp)
                )
                .padding(horizontal = 24.dp, vertical = 8.dp),
            initialValue = gait.value.toDouble(),
            range = 0.1..9.0,
            itemSpacing = 6.dp,
            padWithZero = true,
            step = 0.1,
            onValueChange = { newValue ->
                onGaitChanged(gait.copy(value = newValue.toFloat()))
            }
        )
    }
}